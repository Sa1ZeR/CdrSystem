FROM openjdk:17

COPY ./build/libs/hrs-0.0.1-SNAPSHOT.jar /hrs/hrs.jar

VOLUME /hrs/
WORKDIR /hrs/

CMD ["java", "-jar", "hrs.jar"]