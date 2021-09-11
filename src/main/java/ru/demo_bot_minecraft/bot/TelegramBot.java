package ru.demo_bot_minecraft.bot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.ServerInfoStore;
import ru.demo_bot_minecraft.domain.ServerStats;
import ru.demo_bot_minecraft.service.MinecraftService;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final MinecraftService minecraftService;
    private final ServerInfoStore serverInfoStore;
    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;

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
        } else if (received.equalsIgnoreCase("Логи сервера")) {
            messageBuilder.append("Logs: \n");
            serverInfoStore.getPlayersEvents().values().forEach(events -> events.forEach(event -> messageBuilder.append(event.getTime().format(
                DateTimeFormatter.ofPattern("dd.MM HH:mm"))).append(" ").append(event.getPlayer().getName()).append(" ").append(event.getAction()).append("\n")));
        } else if (received.equalsIgnoreCase("История сервера")) {
            messageBuilder.append("History: \n");
            serverInfoStore.getPlayingInfo().forEach((key, value) -> value.forEach(timeInterval -> messageBuilder.append(key).append(" ").append(timeInterval.getStart().format(DateTimeFormatter.ofPattern("dd.MM HH:mm:ss"))).append(" - ").append(timeInterval.getFinish().format(DateTimeFormatter.ofPattern("dd.MM HH:mm:ss"))).append("\n")));
        } else {
            messageBuilder
                .append(
                    "Пока, что могу только присылать информацию по серверу по запросам: \"Сервер\"\n\"Логи сервера\"\n\"История сервера\"");
        }
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(getKeyboard());
        execute(message);
    }

    private ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        firstRow.add("Сервер");
        firstRow.add("Логи сервера");
        firstRow.add("История Сервера");
        rows.add(firstRow);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }
}
