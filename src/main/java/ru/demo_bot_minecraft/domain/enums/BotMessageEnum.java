package ru.demo_bot_minecraft.domain.enums;

public enum BotMessageEnum {

    SERVER_INFO("""
        Minecraft сервер: %s
        Игроков онайлн: %s
        Сейчас онлайн: %s
        """),

    SUBSCRIPTION("""
        Нажмите на кнопки, чтобы подписаться на информацию
        """),

    CURRENT_SUBSCRIPTIONS("""
        Вы подписаны на: \n
        """),

    PLAY_TIME("""
        Игровое время
        """),

    PLAY_TIME_DATA("""
        Игровое время: \n
        """),

    LOGS("""
        Логи: \n
        """),

    MAIN_MENU("""
        Главное меню
        """),

    NEW_PLAYERS_SUBSCRIPTION_CANCELED("""
        Подписка на новых игроков на сервере, отменена
        """),
    PLAYERS_JOIN_SUBSCRIPTION_CANCELED("""
        Подписка на вход игроков на сервер, отменена
        """),
    NEW_PLAYERS_SUBSCRIBED("""
        Теперь вы будете получать сообщения, когда игроки заходят на сервер
        """),
    PLAYERS_JOIN_SUBSCRIBED("""
        Теперь вы будете получать сообщения, когда новые игроки заходят на сервер
        """),
     WELCOME("""
         Привет! Используй клавиатуру, давать мне команды
         """);



    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
