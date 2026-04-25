FROM maven:3.9.6-eclipse-temurin-21 AS build

# Forcer l'encodage UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Lancer l'application avec le profil prod
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]