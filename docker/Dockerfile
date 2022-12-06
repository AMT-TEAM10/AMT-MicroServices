FROM maven:3.8-amazoncorretto-18 AS build
WORKDIR /app
COPY ../ .
RUN mvn install -DskipTests
RUN mvn package -DskipTests

FROM amazoncorretto:18-alpine AS prod
WORKDIR /app
COPY --from=build /app/build/${SERVICE}/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
