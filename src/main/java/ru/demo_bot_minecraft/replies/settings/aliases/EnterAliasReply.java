package ru.demo_bot_minecraft.replies.settings.aliases;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.PlayerRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.anyText;

@Component
@RequiredArgsConstructor
public class EnterAliasReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final PlayerRepository playerRepository;
    private final PlayerAliasRepository playerAliasRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return anyText(message);
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.ADD_ALIAS;
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var userId = message.getFrom().getId();
        var playerNameAndAlias = getPlayerNameAndAlias(message);
        var playerOptional = playerRepository.findByName(playerNameAndAlias.getFirst());
        if (playerOptional.isEmpty()) {
            return playerNotFound(message, playerNameAndAlias.getFirst());
        }
        var player = playerOptional.get();
        var existing = playerAliasRepository.findByUserIdAndPlayerName(userId, player.getName());
        existing.ifPresentOrElse(playerAlias -> {
            playerAlias.setAlias(playerNameAndAlias.getSecond());
            playerAliasRepository.save(playerAlias);
        }, () -> playerAliasRepository.save(
                PlayerAlias.builder()
                        .player(player)
                        .alias(playerNameAndAlias.getSecond())
                        .user(TelegramUser.builder().id(userId).build())
                        .build()
        ));
        telegramUserRepository.setState(message.getFrom().getId(), UserState.ALIASES);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessage.ALIAS_ADDED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getAliasesKeyboard());
        return sendMessage;
    }

    private BotApiMethod<?> playerNotFound(Message message, String playerName) {
        telegramUserRepository.setState(message.getFrom().getId(), UserState.ALIASES);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(),
                BotMessage.PLAYER_NOT_FOUND.getMessage().formatted(playerName));
        sendMessage.setReplyMarkup(keyboards.getAliasesKeyboard());
        return sendMessage;
    }

    private Pair<String, String> getPlayerNameAndAlias(Message message) {
        var arr = message.getText().split(" ");
        if (arr.length == 2) {
            return Pair.of(arr[0], arr[1]);
        }
        throw new IllegalArgumentException("Invalid message");
    }
}
