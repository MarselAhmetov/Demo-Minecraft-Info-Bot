package ru.demo_bot_minecraft.domain.enums;

import lombok.Getter;

@Getter
public enum RequestMessagesEnum {
    SERVER("Сервер"),
    PLAY_TIME("Игровое время"),
    LOGS("Логи"),
    SUBSCRIPTION("Подписки"),
    NEW_PLAYERS_SUBSCRIPTION("Новые игроки"),
    PLAYERS_JOIN_SUBSCRIPTION("Подключения игроков"),
    DOWNTIME_SUBSCRIPTION("Падения сервера"),
    PLAYERS_REVIVE_SUBSCRIPTION("Возрождения игроков"),
    CANCEL_NEW_PLAYERS_SUBSCRIPTION("Отписаться от \"Новые игроки\""),
    CANCEL_PLAYERS_REVIVE_SUBSCRIPTION("Отписаться от \"Возрождения игроков\""),
    CANCEL_PLAYERS_JOIN_SUBSCRIPTION("Отписаться от \"Подключения игроков\""),
    CANCEL_DOWNTIME_SUBSCRIPTION("Отписаться от \"Падения сервера\""),

    MAIN_MENU("В главное меню"),

    TODAY("Сегодня"),
    YESTERDAY("Вчера"),
    MONTH("Месяц"),
    WEEK("Неделя"),
    ALL_TIME("Все время"),
    START("/start"),
    SETTINGS("Настройки"),
    ADD_NICKNAME("Добавить ник"),
    ADD_ALIAS("Добавить алиас"),
    REMOVE_ALIAS("Удалить алиас"),
    ALIASES("Алиасы"),
    REMOVE_NICKNAME("Удалить ник"),

    ADMIN_SECTION("Админ"),
    USERS_LIST("Список пользователей"),
    BAN_USER("Забанить пользователя"),
    UNBAN_USER("Разбанить пользователя"),
    APPROVE_USER("Разрешить пользоваться"),
    ;

    private final String message;

    RequestMessagesEnum(String message) {
        this.message = message;
    }

}
