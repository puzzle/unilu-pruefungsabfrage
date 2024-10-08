FROM openjdk:21
RUN adduser --no-create-home eft-user
USER eft-user
COPY target .
ENV RESOURCE_DIR="/resources"
ENTRYPOINT ["/bin/sh", "-c", "java -jar -DbasePath=${RESOURCE_DIR} exam-feedback-tool*.jar"]