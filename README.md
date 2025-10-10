# File Service with MinIO and PostgreSQL

Файловый сервис на Spring Boot с интеграцией MinIO для хранения файлов и PostgreSQL для метаданных.

## Возможности

- Загрузка файлов в MinIO
- Скачивание файлов из MinIO
- Хранение метаданных файлов в PostgreSQL
- REST API для управления файлами

## Технологии

- Spring Boot 3.5.6
- PostgreSQL 15
- MinIO
- Java 17
- Docker & Docker Compose

## Запуск

### 1. Запуск инфраструктуры

```bash
docker-compose up -d
```

Это запустит:
- PostgreSQL на порту 5432
- MinIO на портах 9000 (API) и 9001 (Web UI)

### 2. Запуск приложения

```bash
./gradlew bootRun
```

Приложение будет доступно на http://localhost:8080

## API Endpoints

### Загрузка файла
```
POST /api/files/upload
Content-Type: multipart/form-data

Параметры:
- file: файл для загрузки
```

### Скачивание файла
```
GET /api/files/download/{storedName}
```

### Получение информации о файле
```
GET /api/files/info/{storedName}
```

### Удаление файла
```
DELETE /api/files/{storedName}
```

## Примеры использования

### Загрузка файла
```bash
curl -X POST -F "file=@example.txt" http://localhost:8080/api/files/upload
```

### Скачивание файла
```bash
curl -O http://localhost:8080/api/files/download/{storedName}
```

## Конфигурация

Настройки находятся в `src/main/resources/application.properties`:

- **PostgreSQL**: localhost:5432/file_service_db
- **MinIO**: localhost:9000
- **Bucket**: file-storage

## MinIO Web UI

Доступен по адресу: http://localhost:9001
- Username: minioadmin
- Password: minioadmin
