FROM openjdk:17

COPY ./build/libs/brt-0.0.1-SNAPSHOT.jar /brt/brt.jar

VOLUME /brt/
WORKDIR /brt/

CMD ["java", "-jar", "brt.jar"]