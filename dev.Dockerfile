FROM openjdk:8-jdk-alpine
WORKDIR /app-root
ENTRYPOINT ["mvn","spring-boot:run"]