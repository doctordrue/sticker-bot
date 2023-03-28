package org.doctordrue.sticker_bot.controllers.commands.stickers;


import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SetRerollTimeoutCommand extends BotCommand {

    @Autowired
    private TelegramChatService telegramChatService;

    public SetRerollTimeoutCommand() {
        super("/reroll_timeout", "Установить таймаут (в секундах) после которого будет невозможно сделать /reroll последнего отправленного ботом стикера");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder sb = new StringBuilder();
        SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(chat.getId().toString());
        if (arguments.length == 0) {
            long timeoutSeconds = this.telegramChatService.getOrCreate(chat.getId()).getRerollDuration().getSeconds();
            sb.append("Текущий таймаут для команды /reroll: ").append(timeoutSeconds)
                    .append("\nЧтобы задать новый таймаут, добавьте параметр таймаута в секундах.")
                    .append("\nПример (для установки таймаута 2 минуты): /reroll_timeout 120");
        } else {
            try {
                Long duration = Long.parseLong(arguments[0]);
                this.telegramChatService.setRerollDuration(chat.getId(), duration);
                sb.append("Таймаут команды /reroll успешно изменен. Новое значение: ").append(duration).append(" секунд");
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
