package ru.demo_bot_minecraft.replies;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.GenericValidator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.ServerEvent;
import ru.demo_bot_minecraft.domain.dto.PlayHistory;
import ru.demo_bot_minecraft.domain.dto.ServerAction;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.repository.ServerEventRepository;

@Component
@RequiredArgsConstructor
public class StatisticReply implements Reply<Message> {

    private final Keyboards keyboards;
    private final ServerEventRepository serverEventRepository;

    public static final Integer SECONDS_IN_HOUR = 3600;
    public static final Integer SECONDS_IN_MINUTES = 60;

    @Override
    public boolean predicate(Message message) {
        var text = message.getText();
        return isValidData(text);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var serverEvents = findServerEvents(message.getText());
        var playHistories = getStatisticData(serverEvents);
        playHistories.sort((o1, o2) -> o2.getPlayTimeSeconds().compareTo(o1.getPlayTimeSeconds()));
        SendMessage sendMessage;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Play time: \n");
        playHistories.forEach(playHistory -> messageBuilder.append(playHistory.getPlayerName()).append(" ")
            .append(playHistory.getPlayTimeSeconds() / SECONDS_IN_HOUR).append(":")
            .append((playHistory.getPlayTimeSeconds() % SECONDS_IN_HOUR) / SECONDS_IN_MINUTES).append(":")
            .append(playHistory.getPlayTimeSeconds() % SECONDS_IN_MINUTES)
            .append("\n"));
        sendMessage = new SendMessage(message.getChatId().toString(), messageBuilder.toString());
        sendMessage.setReplyMarkup(keyboards.getPlayTimeKeyboard());
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.PLAY_TIME;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }

    private List<ServerEvent> findServerEvents(String text) {
        if (text.equalsIgnoreCase(RequestMessagesEnum.TODAY.getMessage())) {
            return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                LocalDate.now(ZoneId.of("Europe/Moscow")).atStartOfDay(),
                LocalDateTime.now(ZoneId.of("Europe/Moscow"))
            );
        }
        if (text.equalsIgnoreCase(RequestMessagesEnum.YESTERDAY.getMessage())) {
            return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                LocalDate.now(ZoneId.of("Europe/Moscow")).minusDays(1).atStartOfDay(),
                LocalDate.now(ZoneId.of("Europe/Moscow")).atStartOfDay()
            );
        }
        if (text.equalsIgnoreCase(RequestMessagesEnum.WEEK.getMessage())) {
            return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                LocalDate.now(ZoneId.of("Europe/Moscow")).with(DayOfWeek.MONDAY).atStartOfDay(),
                LocalDateTime.now(ZoneId.of("Europe/Moscow"))
            );
        }
        if (text.equalsIgnoreCase(RequestMessagesEnum.MONTH.getMessage())) {
            return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                LocalDate.now(ZoneId.of("Europe/Moscow")).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
                LocalDateTime.now(ZoneId.of("Europe/Moscow"))
            );
        }
        if (text.equalsIgnoreCase(RequestMessagesEnum.ALL_TIME.getMessage())) {
            return serverEventRepository.findAll(Sort.by(Sort.Direction.ASC, "time"));
        }

        String dateRegex = "dd.MM.yyyy";
        boolean isDate = GenericValidator.isDate(text, dateRegex, true);

        if (isDate) {
            var date = LocalDate.parse(text, DateTimeFormatter.ofPattern(dateRegex));
            return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
            );
        }

        if (text.length() > 10) {
            String[] matches = Pattern.compile(dateRegex)
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
            if (matches.length == 2) {
                var from = LocalDate.parse(matches[0]);
                var to = LocalDate.parse(matches[1]);
                if (from.isBefore(to)) {
                    return serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(
                        from.atStartOfDay(),
                        to.atStartOfDay()
                    );
                }
            }
        }

        throw new IllegalArgumentException("Something went wrong");
    }

    private boolean isValidData(String text) {

        boolean result = text.equalsIgnoreCase(RequestMessagesEnum.TODAY.getMessage()) ||
            text.equalsIgnoreCase(RequestMessagesEnum.YESTERDAY.getMessage()) ||
            text.equalsIgnoreCase(RequestMessagesEnum.WEEK.getMessage()) ||
            text.equalsIgnoreCase(RequestMessagesEnum.MONTH.getMessage()) ||
            text.equalsIgnoreCase(RequestMessagesEnum.ALL_TIME.getMessage());

        if (result) {
            return result;
        }

        String dateRegex = "dd.MM.yyyy";
        boolean isDate = GenericValidator.isDate(text, dateRegex, true);
        if (isDate) {
            return true;
        }

        if (text.length() > 10) {
            String[] matches = Pattern.compile(dateRegex)
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
            if (matches.length == 2) {
                return true;
            }
        }

        return false;
    }

    private List<PlayHistory> getStatisticData(List<ServerEvent> events) {
        Map<String, PlayHistory> result = new HashMap<>();
        Map<String, LocalDateTime> joinTimes = new HashMap<>();
        Map<String, LocalDateTime> leftTimes = new HashMap<>();

        for (ServerEvent event : events) {
            if (event.getAction().equals(ServerAction.JOIN)) {
                var playerName = event.getPlayer().getName();
                joinTimes.put(playerName, event.getTime());
                result.putIfAbsent(playerName, PlayHistory.builder()
                    .playerName(playerName)
                    .playTimeSeconds(0L)
                    .build());
            }
            if (event.getAction().equals(ServerAction.LEFT)) {
                var leftTime = event.getTime();
                leftTimes.put(event.getPlayer().getName(), leftTime);
                var joinTime = joinTimes.get(event.getPlayer().getName());

                if (joinTime != null) {
                    Duration duration = Duration.between(joinTime, leftTime);
                    result.get(event.getPlayer().getName()).addPlayTimeSeconds(duration.getSeconds());
                } else {
                    Duration duration = Duration.between(leftTime.toLocalDate().atStartOfDay(), leftTime);
                    result.get(event.getPlayer().getName()).addPlayTimeSeconds(duration.getSeconds());

                }
            }
        }
        for (Entry<String, LocalDateTime> entry : joinTimes.entrySet()) {
            var joinTime = entry.getValue();
            var leftTime = leftTimes.get(entry.getKey());
            if (joinTime != null && (leftTime == null || joinTime.isAfter(leftTime))) {
                var today = LocalDate.now(ZoneId.of("Europe/Moscow")).atStartOfDay();
                if (joinTime.isBefore(today)) {
                    Duration duration = Duration.between(joinTime,
                        joinTime.toLocalDate().plusDays(1).atStartOfDay());
                    result.get(entry.getKey()).addPlayTimeSeconds(duration.getSeconds());
                } else {
                    Duration duration = Duration.between(joinTime,
                        LocalDateTime.now(ZoneId.of("Europe/Moscow")));
                    result.get(entry.getKey()).addPlayTimeSeconds(duration.getSeconds());
                }
            }
        }
        return new ArrayList<>(result.values());
    }
}
