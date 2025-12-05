FROM openjdk:21-jdk-slim

WORKDIR /app

# Устанавливаем необходимые пакеты
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Копируем Gradle wrapper и build файлы
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Копируем исходный код
COPY src src

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Собираем приложение
RUN ./gradlew build -x test

# Запускаем приложение
CMD ["java", "-jar", "build/libs/Dl-0.0.1-SNAPSHOT.jar"]
