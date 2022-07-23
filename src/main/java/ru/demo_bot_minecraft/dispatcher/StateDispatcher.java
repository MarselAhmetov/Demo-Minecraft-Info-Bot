package ru.demo_bot_minecraft.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.domain.enums.BotState;

@Component
@RequiredArgsConstructor
public class StateDispatcher {

    private final List<Reply<Message>> replies;
    private final Map<BotState, List<Reply<Message>>> stateReplies;
    private final List<Reply<Message>> anyStateReplies;

    @PostConstruct
    public void post() {
        replies.forEach(reply -> {
            if (reply.availableInAnyState()) {
                anyStateReplies.add(reply);
            }
            stateReplies.putIfAbsent(reply.getState(), new ArrayList<>());
            stateReplies.get(reply.getState()).add(reply);
        });
    }

    public BotApiMethod<?> dispatch(Message message, BotState state) {
        Optional<BotApiMethod<?>> optionalBotApiMethod = handleState(message, state);
        if (optionalBotApiMethod.isEmpty()) {
            return handleOther(message).orElse(new SendMessage(message.getChatId().toString(), "Unexpected error sorry :("));
        } else {
            return optionalBotApiMethod.get();
        }
    }

    private SendMessage getSendMessage(Message message) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    private Optional<BotApiMethod<?>> handleOther(Message message) {
        var response = anyStateReplies.stream()
            .filter(reply -> reply.predicate(message))
            .findFirst()
            .map(reply -> reply.getReply(message));
        if (response.isEmpty()) {
            var sendMessage = getSendMessage(message);
            sendMessage.setText("Please use the keyboard");
            return Optional.of(sendMessage);
        } else {
            return Optional.of(response.get());
        }
    }

    private Optional<BotApiMethod<?>> handleState(Message message, BotState state) {
        return stateReplies.get(state)
            .stream()
            .filter(reply -> reply.predicate(message))
            .findFirst()
            .map(reply -> reply.getReply(message));
    }
}
