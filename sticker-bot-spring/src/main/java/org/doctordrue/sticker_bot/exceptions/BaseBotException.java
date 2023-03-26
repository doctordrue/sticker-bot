package org.doctordrue.sticker_bot.exceptions;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Describes any exception which can (and actually should) be handled by bot's processors.
 * Bot can send messages of these exceptions to chat where exception happened
 *
 * @author Andrey_Barantsev
 * 5/26/2022
 **/
public abstract class BaseBotException extends RuntimeException {

    protected static final String ERROR_PREFIX = "*Произошла ошибка\\!*\n";

    private final boolean isReplyNeeded;
    private final Chat chat;
    private final SendMessage.SendMessageBuilder replyBuilder;

    public BaseBotException(Chat chat, boolean isReplyNeeded) {
        super();
        this.isReplyNeeded = isReplyNeeded;
        this.chat = chat;
        this.replyBuilder = SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .chatId(this.chat.getId().toString());
    }

    public BaseBotException(Chat chat, boolean isReplyNeeded, Throwable cause) {
        super(cause);
        this.isReplyNeeded = isReplyNeeded;
        this.chat = chat;
        this.replyBuilder = SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .chatId(this.chat.getId().toString());
    }

    public boolean isReplyNeeded() {
        return isReplyNeeded;
    }

    public void sendReplyMessageIfNeeded(AbsSender absSender) {
        if (this.isReplyNeeded) {
            try {
                absSender.execute(getReplyMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected SendMessage.SendMessageBuilder getBuilder() {
        return this.replyBuilder;
    }

    protected abstract SendMessage getReplyMessage();
}
