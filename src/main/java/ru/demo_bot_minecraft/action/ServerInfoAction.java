package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.domain.ServerStats;
import ru.demo_bot_minecraft.service.MinecraftService;

@Component
@RequiredArgsConstructor
public class ServerInfoAction implements Action {

    private final MinecraftService minecraftService;
    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("Server")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
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
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
