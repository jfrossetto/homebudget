FROM amazoncorretto:11-alpine

RUN mkdir /apps

WORKDIR /apps

COPY homebudget-api/build/libs/homebudget-api-0.0.1.jar ./homebudget-api.jar
COPY homebudget-api/build/resources/main/applicationDefault.properties ./application.properties
COPY homebudget-api/build/resources/main/log4j2.xml ./

EXPOSE 8081
EXPOSE 9090

ENV JAVA_OPTS =   -Dserver.port=8081 \
                	-DapplicationProperties=application.properties \
                	-DappLogDir=logs \
                	-Dreactor.netty.http.server.accessLogEnabled=true \
                	-Dlog4j.configurationFile=log4j2.xml \
                	-Dfile.encoding=UTF-8 \
                	-DinstanceName=HomebudgetApi \
                	-Xms256m \
                	-Xmx256m \
                	-XX:+UseG1GC \
                	-XX:+HeapDumpOnOutOfMemoryError \
                	-XX:HeapDumpPath=heapdump.bin \
                	-Dcom.sun.management.jmxremote.rmi.port=9090 \
                  -Dcom.sun.management.jmxremote=true \
                  -Dcom.sun.management.jmxremote.port=9090 \
                  -Dcom.sun.management.jmxremote.ssl=false \
                  -Dcom.sun.management.jmxremote.authenticate=false \
                  -Dcom.sun.management.jmxremote.local.only=false \
                  -Djava.rmi.server.hostname=localhost

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar homebudget-api.jar" ]
