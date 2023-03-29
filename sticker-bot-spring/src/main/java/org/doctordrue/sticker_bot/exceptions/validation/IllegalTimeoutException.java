package org.doctordrue.sticker_bot.exceptions.validation;

import org.doctordrue.sticker_bot.exceptions.BaseBotException;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class IllegalTimeoutException extends BaseBotException {
    private final Long enteredValue;
    private final Long allowedMin;
    private final Long allowedMax;

    public IllegalTimeoutException(Long chatId, Long enteredValue, Long allowedMin, Long allowedMax) {
        super(chatId, true);
        this.enteredValue = enteredValue;
        this.allowedMin = allowedMin;
        this.allowedMax = allowedMax;
    }

    @Override
    protected SendMessage getReplyMessage() {
        return getBuilder().parseMode(ParseMode.HTML)
                .text("Недопустимое значение параметра, значение должно быть больше или равно " + this.allowedMin + " и меньше или равно " + this.allowedMax)
                .build();
    }
}
