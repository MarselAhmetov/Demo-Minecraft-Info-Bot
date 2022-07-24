package ru.demo_bot_minecraft.domain.enums;

public enum RequestMessagesEnum {
    SERVER("Сервер"),
    PLAY_TIME("Игровое время"),
    LOGS("Логи"),
    SUBSCRIPTION("Подписки"),
    NEW_PLAYERS_SUBSCRIPTION("Новые игроки"),
    PLAYERS_JOIN_SUBSCRIPTION("Подключения игроков"),
    CANCEL_NEW_PLAYERS_SUBSCRIPTION("Отписаться от \"Новые игроки\""),
    CANCEL_PLAYERS_JOIN_SUBSCRIPTION("Отписаться от \"Подключения игроков\""),
    MAIN_MENU("В главное меню"),

    TODAY("Сегодня"),
    YESTERDAY("Вчера"),
    MONTH("Месяц"),
    WEEK("Неделя"),
    ALL_TIME("Все время"),
    START("/start");
    private final String message;

    RequestMessagesEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
