package org.doctordrue.sticker_bot.exceptions.reroll;

import org.doctordrue.sticker_bot.exceptions.BaseBotException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Duration;

public class UnableToRerollException extends BaseBotException {
    private final Message causeMessage;
    private final Duration timeoutSet;
    private final Duration timePassed;

    public UnableToRerollException(Message causeMessage, Duration timeoutSet, Duration timePassed) {
        super(causeMessage.getChat(), true);
        this.causeMessage = causeMessage;
        this.timeoutSet = timeoutSet;
        this.timePassed = timePassed;
    }

    @Override
    protected SendMessage getReplyMessage() {
        return getBuilder()
                .text("Невозможно обновить последний стикер. Таймаут обновления " + this.timeoutSet.getSeconds() + " секунд. Прошло: " + this.timePassed.getSeconds() + " секунд")
                .disableNotification(true)
                .replyToMessageId(causeMessage.getMessageId())
                .build();
    }
}
