package org.doctordrue.sticker_bot.exceptions.reroll;

import org.doctordrue.sticker_bot.exceptions.BaseBotException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UnableToRerollException extends BaseBotException {
    private final Message causeMessage;

    public UnableToRerollException(Message causeMessage) {
        super(causeMessage.getChat(), true);
        this.causeMessage = causeMessage;
    }

    @Override
    protected SendMessage getReplyMessage() {
        return getBuilder()
                .text("Невозможно обновить последний стикер")
                .disableNotification(true)
                .replyToMessageId(causeMessage.getMessageId())
                .build();
    }
}
