FROM tomcat:9-jre8

RUN rm -rf /usr/local/tomcat/webapps/*
COPY build/2.4.5/BudgetMaster-v2.4.5.war $CATALINA_HOME/webapps/ROOT.war
COPY src/main/resources/config/templates/settings-docker.properties /root/.Deadlocker/BudgetMaster/settings.properties

EXPOSE 8080