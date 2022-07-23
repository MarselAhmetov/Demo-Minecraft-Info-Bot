package ru.demo_bot_minecraft.replies;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.dto.ServerStats;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.service.MinecraftService;

@Component
@RequiredArgsConstructor
public class ServerInfoReply implements Reply<Message> {

    private final MinecraftService minecraftService;
    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText().equals(RequestMessagesEnum.SERVER.getMessage());
    }

    @Override
    public BotApiMethod<?> getReply(Message message) {
        ServerStats serverStats = minecraftService.getMinecraftServerStats(address, port);
        StringBuilder messageBuilder = new StringBuilder();
        serverStats.getPlayersInfo().getPlayersOnline()
                    .forEach(player -> messageBuilder.append(player.getName()).append("\n"));
        var text = BotMessageEnum.SERVER_INFO.getMessage().formatted(serverStats.getDescription().getText(),
            serverStats.getPlayersInfo().getOnline() + "/" + serverStats.getPlayersInfo().getMax(),
            messageBuilder.toString());
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text(text)
            .replyMarkup(keyboards.getDefaultKeyboard())
            .build();
    }

    @Override
    public BotState getState() {
        return BotState.DEFAULT;
    }

    @Override
    public boolean availableInAnyState() {
        return true;
    }
}
