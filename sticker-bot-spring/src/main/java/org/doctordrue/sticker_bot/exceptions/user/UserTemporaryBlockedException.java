package org.doctordrue.sticker_bot.exceptions.user;

import org.doctordrue.sticker_bot.exceptions.BaseBotException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * @author Andrey_Barantsev
 * 5/26/2022
 **/
public class UserTemporaryBlockedException extends BaseBotException {

    private static final String ERROR_USER_BLOCKED_TEMPLATE = "[%s](tg://user?id=%s)\\, утомил\\. Не буду тебе отвечать";

    private final User user;
    private final Message causeMessage;

    public UserTemporaryBlockedException(Message message, boolean isReplyNeeded) {
        super(message.getChat(), isReplyNeeded);
        this.user = message.getFrom();
        this.causeMessage = message;
    }

    @Override
    protected SendMessage getReplyMessage() {
        return getBuilder()
                .text(String.format(ERROR_USER_BLOCKED_TEMPLATE, this.user.getFirstName(), this.user.getId()))
                .replyToMessageId(causeMessage.getMessageId())
                .build();
    }
}
