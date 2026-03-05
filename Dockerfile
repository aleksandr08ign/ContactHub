# Этап сборки
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Копируем файлы для загрузки зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем собранный jar из этапа builder
COPY --from=builder /app/target/*.jar app.jar

# Создаём непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]