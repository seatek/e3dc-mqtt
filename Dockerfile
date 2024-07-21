FROM adoptopenjdk:11-hotspot

ADD build/libs/e3dc-mqtt.jar /srv/e3dc-mqtt.jar

CMD ["java","-jar","/srv/e3dc-mqtt.jar","--spring.profiles.active=docker"]