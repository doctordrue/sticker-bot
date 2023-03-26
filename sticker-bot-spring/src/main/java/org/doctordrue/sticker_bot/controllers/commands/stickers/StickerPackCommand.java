package org.doctordrue.sticker_bot.controllers.commands.stickers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Component
public class StickerPackCommand extends BotCommand {

    public StickerPackCommand() {
        super("stickerpack", "Узнать название стикерпака");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(message.getChatId().toString());
        if (message.isReply() && message.getReplyToMessage().hasSticker()) {
            String setName = message.getReplyToMessage().getSticker().getSetName();
            builder.text(setName);
        } else {
            builder.text("Нужно послать команду ответом на сообщение со стикером");
        }
        try {
            absSender.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

    }
}
