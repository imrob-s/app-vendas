FROM gradle:jdk21-alpine as build

WORKDIR /app

COPY build.gradle /app/
COPY settings.gradle /app/
COPY src /app/src

RUN gradle clean build

FROM amazoncorretto:21.0.3-alpine

WORKDIR /app

COPY --from=build /app/build/libs/vendas.server-0.0.1-SNAPSHOT.jar app-vendas.jar

EXPOSE 8080

CMD ["java", "-jar", "app-vendas.jar"]