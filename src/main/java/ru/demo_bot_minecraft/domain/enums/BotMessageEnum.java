package ru.demo_bot_minecraft.domain.enums;

public enum BotMessageEnum {

    SERVER_INFO("""
        Minecraft server: %s
        Players online: %s
        Currently online: %s
        """);
    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
