package ru.demo_bot_minecraft.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.domain.ServerStats;
import ru.demo_bot_minecraft.service.MinecraftService;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final MinecraftService minecraftService;
    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleUpdate(Update update) throws TelegramApiException {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        String received = update.getMessage().getText();

        if (received.equalsIgnoreCase("Сервер")) {
            String address = "51.255.77.37";
            Integer port = 25586;
            ServerStats serverStats = minecraftService.getMinecraftServerStats(address, port);
            messageBuilder
                .append("Minecraft server: ").append(address).append(":")
                .append(port).append("\n")
                .append("Players online: ").append(serverStats.getPlayersInfo().getOnline()).append("/")
                .append(serverStats.getPlayersInfo().getMax()).append("\n");
            if (serverStats.getPlayersInfo().getPlayersOnline() != null) {
                messageBuilder.append("Currently online: \n");
                serverStats.getPlayersInfo().getPlayersOnline()
                    .forEach(player -> messageBuilder
                        .append(player.getName())
                        .append("\n"));
            }
        } else {
            messageBuilder
                .append(
                    "Пока, что могу только присылать статистику по серверу по запросу: \"Сервер\"");
        }
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        execute(message);
    }
}
