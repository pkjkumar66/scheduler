FROM openjdk:17
EXPOSE 8080
ADD target/appointment-booking-app-image.jar appointment-booking-app-image.jar
ENTRYPOINT ["java","-jar","/appointment-booking-app-image.jar"]
