package com.example.mstuca_schedule_bot.service;

import com.example.mstuca_schedule_bot.config.BotConfig;
import com.example.mstuca_schedule_bot.dto.LessonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class TelegramScheduleBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(ScheduleApiClient.class);

    private final BotConfig botConfig;
    private final ScheduleService scheduleService;

    public TelegramScheduleBot(BotConfig botConfig, ScheduleService scheduleService) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        this.scheduleService = scheduleService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            var user = update.getMessage().getFrom();
            String username = user.getUserName() != null ? "@" + user.getUserName() : "без_ника";
            String firstName = user.getFirstName();
            Long userId = user.getId();

            log.info("Пользователь {} (имя: {}, id: {}) написал: \"{}\"",
                    username, firstName, userId, text);
            switch (text) {
                case "/start":
                    sendMessage(chatId, "Добро пожаловать! Выберите нужную неделю:");
                    break;

                case "На текущую неделю":
                    try {
                        sendMessage(chatId, createMessageForDate(scheduleService.getCurrentOrNextWeek("current")));
                    } catch (Exception e) {
                        sendMessage(chatId, "Сайт с расписанием не отвечает :( Попробуйте позже.");
                    }
                    break;

                case "На следующую неделю":
                    try {
                        sendMessage(chatId, createMessageForDate(scheduleService.getCurrentOrNextWeek("next")));
                    } catch (Exception e) {
                        sendMessage(chatId, "Сайт с расписанием не отвечает :( Попробуйте позже.");
                    }
                    break;
                case "На сегодня":
                    LocalDate today = LocalDate.now();
                    if (today.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                            || today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                        sendMessage(chatId, "Сегодня выходной :)");
                    } else {
                        sendMessage(chatId, createMessageForDate(scheduleService.getLessonsOfToday()));
                    }
                    break;
                case "На завтра":
                    LocalDate tomorrow = LocalDate.now().plusDays(1);
                    if (tomorrow.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                            || tomorrow.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                        sendMessage(chatId, "Завтра выходной :) \n Вот расписание на ПН");
                    }
                    sendMessage(chatId, createMessageForDate(scheduleService.getLessonsOfTomorrowOrMondayNextWeek()));
                    break;
                default:
                    sendMessage(chatId, "Команда не распознана");
                    break;
            }
        }
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = getKeyboardRows();
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        int maxAttempts = 3;
        int attempt = 0;
        boolean sent = false;

        while (attempt < maxAttempts && !sent) {
            try {
                attempt++;
                execute(sendMessage); // Пытаемся отправить сообщение в Telegram
                sent = true; // Если дошли сюда, значит отправилось успешно!

            } catch (TelegramApiException e) {
                System.err.println("ОШИБКА ОТПРАВКИ (попытка " + attempt + "/" + maxAttempts + "): " + e.getMessage());

                if (attempt >= maxAttempts) {
                    System.err.println("Все попытки отправить сообщение исчерпаны.");
                    e.printStackTrace();
                } else {
                    try {
                        // пауза перед повтором
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private static List<KeyboardRow> getKeyboardRows() {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("На текущую неделю"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("На следующую неделю"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("На сегодня"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("На завтра"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        return keyboard;
    }

    public String createMessageForDate(List<LessonDto> lessons) {

        if (lessons == null || lessons.isEmpty()) {
            return "На этот период расписания пока нет 📭";
        }

        String[] reverceDate;
        StringBuilder fullMessage = new StringBuilder();

        for (int i = 0; i < lessons.size(); i++) {
            LessonDto lessonDto = lessons.get(i);
            reverceDate = lessonDto.getDate().split("-");
            String date = reverceDate[2] + "." + reverceDate[1] + "." + reverceDate[0];

            if (i > 0 && lessonDto.getDate().equals(lessons.get(i - 1).getDate())) {
                fullMessage.append(
                        "\uD83D\uDD58 " + lessonDto.getBeginLesson() + " - " + lessonDto.getEndLesson() + "\n   " +
                                lessonDto.getDiscipline() + "\n   ⭐\uFE0F" +
                                lessonDto.getKindOfWork() + "⭐\uFE0F \n   " +
                                lessonDto.getBuilding() + " (" + lessonDto.getAuditorium() + ")\n   " +
                                lessonDto.getLecturer() + "\n\n");
            } else {
                fullMessage.append(
                        "                             \uD83D\uDDD3" +
                                lessonDto.getDayOfWeekString() + " " + date + "\uD83D\uDDD3\n" +
                                "\uD83D\uDD58 " + lessonDto.getBeginLesson() + " - " + lessonDto.getEndLesson() + "\n   " +
                                lessonDto.getDiscipline() + "\n   ⭐\uFE0F" +
                                lessonDto.getKindOfWork() + "⭐\uFE0F \n   " +
                                lessonDto.getBuilding() + " (" + lessonDto.getAuditorium() + ")\n   " +
                                lessonDto.getLecturer() + "\n\n");
            }
        }
        return fullMessage.toString();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
