FROM openjdk:27-ea-1-jdk-slim

WORKDIR /app/zarpar
COPY . .
RUN chmod +x ./mvnw
RUN mkdir /uploads
RUN ["./mvnw", "clean", "install", "-DskipTests"]

ENTRYPOINT ["./mvnw","spring-boot:run"]
EXPOSE 8080