FROM adoptopenjdk/openjdk16
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} demo_bot_minecraft-0.0.1.jar
ENTRYPOINT ["java", "-Dbot.username=${BOT_NAME}", "-Dbot.token=${BOT_TOKEN}", "-Dminecraft.server.address=${MINECRAFT_SERVER_ADDRESS}", "-Dminecraft.server.port=${MINECRAFT_SERVER_PORT}", "-jar", "demo_bot_minecraft-0.0.1.jar"]