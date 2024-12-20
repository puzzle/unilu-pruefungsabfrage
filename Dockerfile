FROM eclipse-temurin:21
RUN adduser --no-create-home eft-user
USER eft-user
COPY target .
ENV RESOURCE_DIR="/resources"
ENTRYPOINT ["/bin/sh", "-c", "java -jar -Dspring.profiles.active=prod exam-feedback-tool*.jar"]