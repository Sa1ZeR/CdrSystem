FROM openjdk:17

COPY ./build/libs/cdr-0.0.1-SNAPSHOT.jar /cdr/cdr.jar

VOLUME /cdr/
WORKDIR /cdr/

CMD ["java", "-jar", "cdr.jar"]