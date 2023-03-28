package org.doctordrue.sticker_bot.controllers.commands.stickers;

import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Component
public class SetTimeoutCommand extends BotCommand {

    private final TelegramChatService telegramChatService;

    public SetTimeoutCommand(TelegramChatService telegramChatService) {
        super("timeout", "Установить таймаут (в секундах) для отправки ботом рандомных стикеров");
        this.telegramChatService = telegramChatService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder sb = new StringBuilder();
        SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(chat.getId().toString());
        if (arguments.length == 0) {
            long timeoutSeconds = this.telegramChatService.getOrCreate(chat.getId()).getReplyDuration().getSeconds();
            sb.append("Текущий таймаут ответа стикером: ").append(timeoutSeconds)
                    .append("\nЧтобы задать новый таймаут, добавьте параметр таймаута в секундах.")
                    .append("\nПример (для установки таймаута 2 минуты): /timeout 120");
        } else {
            try {
                Long duration = Long.parseLong(arguments[0]);
                this.telegramChatService.setReplyDuration(chat.getId(), duration);
                sb.append("Таймаут ответа стикером успешно изменен. Новое значение: ").append(duration).append(" секунд");
            } catch (NumberFormatException e) {
                sb.append("Некорректное значение таймаута:").append(arguments[0]).append(". Пример (для установки таймаута 2 минуты): /timeout 120");
            }
        }
        builder.text(sb.toString());
        try {
            absSender.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
