FROM openjdk:15
ADD target/lagalt-0.0.1-SNAPSHOT.jar lagalt.jar
ENTRYPOINT ["java", "-jar", "/lagalt.jar"]