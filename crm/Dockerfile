FROM openjdk:17

COPY ./build/libs/crm-0.0.1-SNAPSHOT.jar /crm/crm.jar

VOLUME /crm/
WORKDIR /crm/

EXPOSE 3000

CMD ["java", "-jar", "crm.jar"]