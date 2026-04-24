FROM maven:3.9.6-eclipse-temurin-17 AS build

# Forcer l'encodage UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /app

COPY . .

# Nettoyer et compiler
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]