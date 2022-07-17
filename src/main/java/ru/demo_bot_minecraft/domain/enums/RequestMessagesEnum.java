package ru.demo_bot_minecraft.domain.enums;

public enum RequestMessagesEnum {
    SERVER("Server"),
    LOGS("Logs"),
    SUBSCRIPTION("Subscriptions"),
    NEW_PLAYERS_SUBSCRIPTION("New players"),
    PLAYERS_JOIN_SUBSCRIPTION("Players join"),
    CANCEL_NEW_PLAYERS_SUBSCRIPTION("Cancel new players"),
    CANCEL_PLAYERS_JOIN_SUBSCRIPTION("Cancel players join"),
    MAIN_MENU("Main menu");
    private final String message;

    RequestMessagesEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
