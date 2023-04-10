package ru.demo_bot_minecraft.replies.home;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.dto.Description;
import ru.demo_bot_minecraft.domain.dto.Extra;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.service.MinecraftService;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class ServerInfoReply implements Reply<Message> {

    private final MinecraftService minecraftService;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.SERVER);
    }

    @Override
    public BotApiMethod<?> getReply(Message message) {
        return minecraftService.getMinecraftServerStats().getServerStats()
            .map(serverStats -> {
                StringBuilder messageBuilder = new StringBuilder();
                serverStats.getPlayersInfo().getPlayersOnline()
                    .forEach(player -> messageBuilder.append(player.getName()).append("\n"));
                var text = BotMessageEnum.SERVER_INFO.getMessage().formatted(getText(serverStats.getDescription()),
                    serverStats.getPlayersInfo().getOnline() + "/" + serverStats.getPlayersInfo().getMax(),
                    messageBuilder.toString());
                return SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text(text)
                    .replyMarkup(keyboards.getDefaultKeyboard())
                    .build();
            })
            .orElse(SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(BotMessageEnum.SERVER_IS_UNAVAILABLE.getMessage())
                .replyMarkup(keyboards.getDefaultKeyboard())
                .build());
    }

    private String getText(Description description) {
        if (description.getText() != null && !description.getText().isEmpty()) {
            return description.getText();
        } else if (description.getExtra() != null && !description.getExtra().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            description.getExtra().stream().map(Extra::getText)
                .forEach(stringBuilder::append);
            return stringBuilder.toString();
        } else {
            return "Сервер";
        }
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
