# MSTUCA Schedule Telegram Bot

Telegram-бот для автоматического получения и отображения расписания занятий
---

## Технологический стек

* **Язык:** Java 17
* **Фреймворк:** Spring Boot 3 (Spring Data JPA, Spring Web, Spring Scheduling)
* **База данных:** PostgreSQL
* **Миграции / Схема:** Hibernate (автоматическое управление сущностями в схеме `mstuca_schedule_bot_schema`)
* **Интеграция:** Внешнее API РУЗ МТУСА через `RestTemplate` с механизмом повторных попыток (Retry)
* **Telegram API:** TelegramBots Spring Boot Starter
* **Контейнеризация:** Docker & Docker Compose

---

## Основной функционал

1. **Интерактивное меню:** Удобная клавиатура (`ReplyKeyboardMarkup`) для быстрого получения расписания:
   * На текущую неделю
   * На следующую неделю
   * На сегодня / на завтра
2. **Автоматическое обновление:** Фоновая задача (`@Scheduled`), которая регулярно сверяет локальную базу данных с актуальным расписанием с сайта МТУСА.
3. **Отказоустойчивость:** 
   * Защита от сетевых сбоев и временных ошибок API (`SSLHandshakeException`, коды ошибок сервера) с помощью механизма автоматических повторных попыток (Retry).
   * Защита от пустых ответов и сбоев генерации сообщений для Telegram.
4. **Безопасность:** Конфиденциальные данные (токен бота, пароли базы данных) вынесены в защищенные переменные окружения (`.env`).

---

## Структура проекта

```text
mstuca-schedule-bot/
├── src/main/java/com/example/mstuca_schedule_bot/
│   ├── config/       # Конфигурация приложения (BotConfig и др.)
│   ├── dto/          # Data Transfer Objects для парсинга API
│   ├── entity/       # JPA-сущности (Lesson)
│   ├── mapper/       # MapStruct мапперы для конвертации DTO в Entity
│   ├── repository/   # Репозитории для работы с PostgreSQL
│   ├── service/      # Бизнес-логика, API-клиент и логика Telegram-бота
│   └── MstucaScheduleBotApplication.java # Точка входа
├── src/main/resources/
│   └── application.yaml  # Основные настройки Spring Boot
├── Dockerfile            # Инструкция сборки Docker-образа бэкенда
├── docker-compose.yml    # Оркестрация контейнеров (БД + Бэкенд)
└── .env                  # Переменные окружения (скрыто в git)
```

---

## Быстрый старт и локальный запуск

### Требования
* Установленный **Docker** и **Docker Compose**
* Установленный **Git** (опционально)

### Инструкция по запуску:

1. **Клонируйте репозиторий:**
   ```bash
   git clone <ссылка_на_ваш_репозиторий>
   cd mstuca-schedule-bot
   ```

2. **Создайте файл с переменными окружения (`.env`):**
   В корне проекта создайте файл `.env` и пропишите в него доступы:
   ```env
   BOT_NAME=ваше_имя_бота
   BOT_TOKEN=ваш_токен_от_botfather
   POSTGRES_DB=schedule_db
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=ваш_пароль_для_бд
   ```

3. **Запустите проект через Docker Compose:**
   ```bash
   docker compose up --build -d
   ```
   Эта команда соберет проект, поднимет контейнер с PostgreSQL и запустит вашего бота в фоновом режиме.

4. **Просмотр логов (для отладки):**
   ```bash
   docker logs -f schedule_bot_backend
   ```

---

## Остановка проекта

Чтобы остановить контейнеры, выполните в папке с проектом:
```bash
docker compose down
```