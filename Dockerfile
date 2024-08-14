FROM openjdk:21
COPY target .
ENV RESOURCE_DIR="/resources"
ENTRYPOINT ["/bin/sh", "-c", "java -jar -DbasePath=${RESOURCE_DIR} exam-feedback-tool*.jar"]