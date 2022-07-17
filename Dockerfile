FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} demo_bot_minecraft-0.0.1.jar
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Dbot.username=${BOT_NAME}", "-Dbot.token=${BOT_TOKEN}", "-Dbot.webhook-path=${BOT_WEBHOOK_PATH}", "-Dminecraft.server.address=${MINECRAFT_SERVER_ADDRESS}", "-Dminecraft.server.port=${MINECRAFT_SERVER_PORT}", "-jar", "demo_bot_minecraft-0.0.1.jar"]