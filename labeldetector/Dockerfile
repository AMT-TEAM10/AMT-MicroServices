FROM maven:3.8-amazoncorretto-18 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM amazoncorretto:18-alpine AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
