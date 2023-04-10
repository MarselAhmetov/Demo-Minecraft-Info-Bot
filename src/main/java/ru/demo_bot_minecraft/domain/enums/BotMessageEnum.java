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

    SETTINGS("""
        Настройки
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

    DOWNTIME_SUBSCRIPTION_CANCELED("""
        Подписка на падения сервера, отменена
        """),
    ENTER_YOUR_NICKNAME("""
        Введите ваш ник
        """),
    NICKNAME_ADDED("""
        Ник добавлен
        """),
    NICKNAME_REMOVED("""
        Ник удален
        """),
    NEW_PLAYERS_SUBSCRIBED("""
        Теперь вы будете получать сообщения, когда игроки заходят на сервер
        """),
    PLAYERS_JOIN_SUBSCRIBED("""
        Теперь вы будете получать сообщения, когда новые игроки заходят на сервер
        """),
    DOWNTIME_SUBSCRIBED("""
        Теперь вы будете получать сообщения, если сервер упадет
        """),
     WELCOME("""
         Привет! Используйте клавиатуру, чтобы давать мне команды
         """),
    USE_THE_KEYBOARD("""
         Пожалуйста, используйте клавиатуру
         """),
    SERVER_IS_UNAVAILABLE("""
        Сервер недоступен
        """),
    UNKNOWN_COMMAND("""
        Я получил ваше сообщение: %s, но пока не знаю как его обрабатывать
        """),
    UNKNOWN_ERROR("""
        Неожиданная ошибка :(
        """),
    PLAYER_JOIN("""
        %s зашел на сервер
        """),
    NEW_PLAYER_JOIN("""
        НОВЫЙ игрок: %s зашел на сервер
        """),
    ;



    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
