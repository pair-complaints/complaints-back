FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /app

# Устанавливаем Maven (apk в Alpine)
RUN apk add --no-cache maven

# Копируем корень проекта
COPY .. /app/
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

# Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Копируем конкретный JAR
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
