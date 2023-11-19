package ru.demo_bot_minecraft.domain.enums;

import lombok.Getter;

@Getter
public enum BotMessage {

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
    PLAYERS_REVIVE_SUBSCRIPTION_CANCELED("""
        Подписка на возрождения игроков, отменена
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
    CURRENT_ALIASES("""
        Ваши алиасы: \n
        """),
    ENTER_PLAYER_NAME_AND_ALIAS("""
        Введите ник игрока и его алиас через пробел: NickName Alias
        """),
    ALIAS_ADDED("""
        Алиас добавлен
        """),
    PLAYER_NOT_FOUND("""
        Игрок с ником %s не найден
        """),
    ALIAS_REMOVED("""
        Алиас удален
        """),
    ENTER_PLAYER_NAME_TO_REMOVE_ALIAS("""
        Введите алиас который хотите удалить.
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
    PLAYERS_REVIVE_SUBSCRIBED("""
        Теперь вы будете получать сообщения, если потребуется возрождение игрока
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
    PLAYER_WAIT_FOR_REVIVE("""
        %s помер, чтобы возродить нужно:
        %s
        """),
    PLAYER_REVIVED("""
        %s возрожден!
        """),
    ADMIN_SECTION("""
        Админская секция
            """),
    BANNED("""
        Вы забанены администратором
            """),
    WAITING_FOR_APPROVE("""
        Ваша заявка на использование бота, находится на рассмотрении
        """),
    YOU_ARE_APPROVED("""
        Ваша заявка на использование бота, одобрена. Используйте клавиатуру, чтобы дать команды боту
        """),
    ENTER_USERNAME_TO_APPROVE("""
        Введите имя пользователя, которому хотите разрешить пользоваться ботом
        """),
    ENTER_USERNAME_TO_BAN("""
        Введите имя пользователя, которому хотите запретить пользоваться ботом
        """),
    ENTER_USERNAME_TO_UNBAN("""
        Введите имя пользователя, которому хотите снова разрешить пользоваться ботом
        """),
    USER_APPROVED("""
        Пользователь %s может пользоваться ботом
        """),
    USER_BANNED("""
        Пользователь %s теперь не может пользоваться ботом
        """),
    USER_UNBANNED("""
        Пользователь %s теперь снова может пользоваться ботом
        """),
    USER_NOT_FOUND("""
        Пользователь %s не найден
        """),
    USER_WAITING_FOR_APPROVE("""
        Пользователь %s ожидает подтверждения
        """),
    USERS_WAITING_FOR_APPROVE("""
        Пользователи ожидающие подтверждения: \n
        """),
    ;

    private final String message;

    BotMessage(String message) {
        this.message = message;
    }

}
