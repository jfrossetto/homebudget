FROM amazoncorretto:11-alpine

RUN mkdir /apps

WORKDIR /apps

ARG JAR_FILE=homebudget-api/build/libs/*.jar
COPY ${JAR_FILE} ./homebudget-api.jar
COPY homebudget-api/build/resources/main/applicationDefault.properties ./application.properties
COPY homebudget-api/build/resources/main/log4j2.xml ./

EXPOSE 8081
EXPOSE 9090

CMD java -Dserver.port=8081 \
         -DapplicationProperties=application.properties \
         -DappLogDir=logs \
         -Dreactor.netty.http.server.accessLogEnabled=true \
         -Dlog4j.configurationFile=log4j2.xml \
         -Dfile.encoding=UTF-8 \
         -DinstanceName=HomebudgetApi \
         -Xms128m \
         -Xmx256m \
         -XX:+UseG1GC \
    -jar homebudget-api.jar

