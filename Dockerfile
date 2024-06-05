FROM openjdk:21-jdk-slim

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn

COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw package -DskipTests

EXPOSE 8889

CMD ["java", "-jar", "target/statistics-0.0.1-SNAPSHOT.jar"]
