FROM openjdk:17
WORKDIR /project
COPY . .
EXPOSE 8000
CMD ["java", "-jar"]
