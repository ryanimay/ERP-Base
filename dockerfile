FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY /target/erp-0.0.1.jar /app/erp-0.0.1.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "erp-0.0.1.jar"]