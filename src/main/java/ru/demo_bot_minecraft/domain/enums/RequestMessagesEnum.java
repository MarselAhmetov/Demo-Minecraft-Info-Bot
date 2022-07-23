package ru.demo_bot_minecraft.domain.enums;

public enum RequestMessagesEnum {
    SERVER("Server"),
    PLAY_TIME("Play time"),
    LOGS("Logs"),
    SUBSCRIPTION("Subscriptions"),
    NEW_PLAYERS_SUBSCRIPTION("New players"),
    PLAYERS_JOIN_SUBSCRIPTION("Players join"),
    CANCEL_NEW_PLAYERS_SUBSCRIPTION("Cancel new players"),
    CANCEL_PLAYERS_JOIN_SUBSCRIPTION("Cancel players join"),
    MAIN_MENU("Main menu"),

    TODAY("Today"),
    YESTERDAY("Yesterday"),
    MONTH("Month"),
    WEEK("Week"),
    ALL_TIME("All time");
    private final String message;

    RequestMessagesEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
