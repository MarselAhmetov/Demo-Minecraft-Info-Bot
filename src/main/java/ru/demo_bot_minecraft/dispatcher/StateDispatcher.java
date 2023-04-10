package ru.demo_bot_minecraft.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class StateDispatcher {

    private final List<Reply<Message>> replies;
    private final Map<BotState, List<Reply<Message>>> stateReplies = new HashMap<>();
    private final Set<Reply<Message>> anyStateReplies = new HashSet<>();
    private final Keyboards keyboards;
    private final TelegramUserRepository userRepository;

    @PostConstruct
    public void post() {
        replies.forEach(reply -> {
            if (reply.availableInAnyState()) {
                anyStateReplies.add(reply);
            }
            stateReplies.computeIfAbsent(reply.getState(), k -> new ArrayList<>()).add(reply);
        });
    }

    public BotApiMethod<?> dispatch(Message message, BotState state) {
        return handleState(message, state)
                .orElseGet(() -> handleOther(message));
    }

    private SendMessage getSendMessage(Message message) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    private BotApiMethod<?> handleOther(Message message) {
        return anyStateReplies.stream()
                .filter(reply -> reply.predicate(message))
                .findFirst()
                .map(reply -> reply.getReply(message))
                .orElseGet(() -> {
                    var sendMessage = getSendMessage(message);
                    sendMessage.setText(BotMessageEnum.USE_THE_KEYBOARD.getMessage());
                    var user = userRepository.getById(message.getFrom().getId());
                    sendMessage.setReplyMarkup(keyboards.getByState(user));
                    return ((BotApiMethod) sendMessage);
                });
    }

    private Optional<BotApiMethod<?>> handleState(Message message, BotState state) {
        return stateReplies.get(state).stream()
            .filter(reply -> reply.predicate(message))
            .findFirst()
            .map(reply -> reply.getReply(message));
    }
}
