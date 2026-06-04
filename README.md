# ВариантУм

> **🥉 3 место** · Хакатон СберОбразование × Школа 21 «ИИ для образования: автоматизация рутинных задач»

**ИИ-сервис для автоматической генерации равносложных вариантов школьных контрольных работ.**

Один эталон из учебника — и за 5 минут вместо 2 часов учитель получает готовый комплект вариантов: одинаковых по сложности, разных по содержанию, сразу с ответами и экспортом в PDF/DOCX.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=springboot)
![React](https://img.shields.io/badge/React-18-61DAFB?logo=react)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript)
![GigaChat](https://img.shields.io/badge/GigaChat-API-21A038?logo=sberbank)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker)

---

## Проблема

Учителя тратят от **40 минут до 2 часов** на ручное создание нескольких вариантов одной контрольной.  
При этом почти половина аудитории — педагоги 45+, которым сложно работать с перегруженными инструментами и ИИ-чатами.  
Обычный LLM-чат генерирует текст, но не решает методическую задачу: сохранить одинаковую сложность и структуру вариантов.

---

## Что было реализовано

### Три сценария работы

**1. Генерация по эталону**
Загружаете готовое задание (текст / PDF / DOCX / фото) — сервис анализирует структуру, выделяет инварианты и вариативные элементы, затем генерирует N вариантов того же типа и сложности. Распознавание через GigaChat Vision с Tesseract OCR как офлайн-фолбэком.

**2. Генерация по критериям**
Без эталона: задаёте предмет, класс, тему, тип заданий, количество вариантов, время выполнения и уровень сложности — комплект генерируется с нуля.

**3. Доработка из библиотеки**
Открываете сохранённый комплект из личной библиотеки и дорабатываете его: перегенерируете весь комплект, один вариант или одно задание.

### Редактор комплекта
- WYSIWYG-редактор с поддержкой формул (KaTeX / MathLive) и графиков функций
- Анализ варианта: класс, предмет, тип, количество заданий
- Настройки вариации для каждого задания или для всего комплекта сразу
- Три режима сложности: одинаковая / возрастающая / произвольная
- Чекбокс предотвращения совпадения ответов между вариантами

### Сравнение вариантов
- Параллельный просмотр всех вариантов одновременно
- Подсветка различий между вариантами
- История изменений (снимки версий в БД)
- Промпт-пожелание учителя на любом шаге («задачи про космос», «без дробей»)
- AI-правка одного задания или всего комплекта через GigaChat

### Экспорт
- PDF и DOCX с полями ФИО / класс / дата
- Отдельный файл с ответами для учителя
- Рендеринг формул и графиков в SVG при экспорте

### Онлайн-форма для учеников
- Учитель назначает вариант и отправляет ученику ссылку
- Ученик входит по имени/фамилии, выполняет задание и отправляет ответы (текст + файлы)
- Учитель проверяет ответы и ставит оценку прямо на платформе
- Список учеников с общей ссылкой на форму

### Личная библиотека
- Поиск по комплектам
- История версий каждого комплекта
- Повторное использование и доработка

### Онбординг
- Интерактивная пошаговая инструкция запускается сразу после регистрации
- Можно запустить с любой страницы — тур начнётся именно с неё
- Адаптирован для педагогов старшего возраста

### Лимиты генерации
- Лимит на пользователя отображается в процентах
- Стоимость запроса зависит от количества вариантов, заданий и их сложности
- Предотвращает злоупотребление API при MVP-деплое

---

## Стек

| Слой | Технологии |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2 (Web, WebFlux, Security/JWT, Data JPA, Data Redis) |
| **Frontend** | React 18 + TypeScript, Vite, TanStack Query, Zustand, Tailwind CSS, Radix UI, Tiptap, KaTeX, MathLive |
| **БД и хранилище** | PostgreSQL 16 + Flyway, Redis 7, MinIO (S3-совместимое) |
| **LLM** | GigaChat API (мультимодель: Lite / Pro / Max), авто-фолбэк 402 → Lite |
| **Парсинг** | Apache PDFBox, Apache POI, Tess4J (Tesseract OCR, русская модель) |
| **Экспорт** | iText 7 (PDF), docx4j (DOCX), SVG-рендеринг формул и графиков |
| **Инфраструктура** | Docker + docker-compose, Nginx (reverse proxy + статика) |

---

## Архитектура

```
Browser
   │
   ▼
Nginx (80)
   ├── /          → React SPA (static)
   └── /api       → Spring Boot (8080)
                       ├── AuthController     JWT auth + refresh tokens
                       ├── ProjectController  проекты / комплекты заданий
                       ├── VariantController  варианты, AI-правка
                       ├── AnalyzeController  анализ эталона через GigaChat
                       ├── ExportController   PDF / DOCX
                       ├── FormController     онлайн-форма для учеников
                       ├── FileController     загрузка файлов → MinIO
                       └── LimitsController   лимиты на генерацию
                                │
                    ┌───────────┼───────────┐
                    ▼           ▼           ▼
               PostgreSQL     Redis      MinIO
               (данные)     (кэш/сессии) (файлы)
                                │
                                ▼
                          GigaChat API
                     (Lite / Pro / Max)
```

---

## Структура репозитория

```
variantum/
├── backend/            Spring Boot приложение (Maven, Java 21)
│   └── src/main/java/ru/variantum/
│       ├── controller/ REST-контроллеры
│       ├── service/    бизнес-логика (llm/, auth/, export/)
│       ├── domain/     JPA-сущности
│       └── config/     конфигурация (Security, GigaChat, MinIO)
├── frontend/           React + Vite + TypeScript
│   └── src/
│       ├── pages/      9 экранов приложения
│       ├── features/   экспорт, редактор
│       ├── tour/       интерактивный онбординг
│       └── api/        клиентский слой
├── docker-compose.yml  postgres + redis + minio + backend + frontend + nginx
├── .env.example        пример переменных окружения
└── README.md
```

---

## Запуск

### Требования
- **Docker** + **docker compose** (для полного стека)
- Для разработки без Docker — **JDK 21** и **Node.js 20+**
- **Ключ GigaChat** (Client ID + Client Secret) — получить в [GigaChat Studio](https://developers.sber.ru/studio/workspaces/)

### Шаг 1. Переменные окружения

```bash
cp .env.example .env
```

Обязательные значения в `.env`:

| Переменная | Назначение |
|---|---|
| `GIGACHAT_CLIENT_ID` | UUID клиента из GigaChat Studio |
| `GIGACHAT_CLIENT_SECRET` | секрет клиента |
| `JWT_SECRET` | случайная строка — `openssl rand -base64 64` |
| `DB_PASSWORD` | пароль PostgreSQL |
| `MINIO_SECRET_KEY` | секрет MinIO |

> Бесплатная модель называется `GigaChat` (не `GigaChat-2-Lite`). Основная — `GigaChat-2-Pro`.

### Шаг 2а. Полный стек одной командой

```bash
docker compose up --build -d
```

| Сервис | URL |
|---|---|
| Frontend | http://localhost |
| Backend API | http://localhost/api |
| Swagger UI | http://localhost/api/swagger-ui.html |
| MinIO Console | http://localhost:9001 |

### Шаг 2б. Режим разработки

```bash
# Только инфраструктура
docker compose up -d postgres redis minio

# Backend → http://localhost:8080/api
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run

# Frontend → http://localhost:5173
cd frontend
npm install && npm run dev
```

Миграции БД (Flyway) применяются автоматически при старте backend.

### Проверка

```bash
curl http://localhost:8080/api/health   # → 200 OK
```

### Остановка

```bash
docker compose down          # остановить
docker compose down -v       # остановить и удалить тома (данные пропадут)
```

---

## Команда

| | Имя | Роль |
|---|---|---|
| 👩‍💻 | **Кристина** | Backend (Java/Spring), Frontend, БД, Prompt Engineering |
| 👩‍💻 | **Соня** | Frontend (React/TS), UI-дизайн, Backend, Prompt Engineering |
