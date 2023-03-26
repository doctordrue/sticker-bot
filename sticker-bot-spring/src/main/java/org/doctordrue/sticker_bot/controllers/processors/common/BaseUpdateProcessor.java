package org.doctordrue.sticker_bot.controllers.processors.common;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.function.Supplier;

/**
 * @author Andrey_Barantsev
 * 5/11/2022
 **/
public abstract class BaseUpdateProcessor {

    private Supplier<String> botUsernameSupplier;

    public abstract void execute(AbsSender absSender, Update update);

    public void setBotUsernameSupplier(Supplier<String> botUsernameSupplier) {
        this.botUsernameSupplier = botUsernameSupplier;
    }

    protected String getBotUsername() {
        return this.botUsernameSupplier.get();
    }
}
