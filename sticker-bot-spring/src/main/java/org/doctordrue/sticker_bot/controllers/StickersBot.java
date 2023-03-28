package org.doctordrue.sticker_bot.controllers;

import org.doctordrue.sticker_bot.controllers.commands.StartCommand;
import org.doctordrue.sticker_bot.controllers.commands.stickers.*;
import org.doctordrue.sticker_bot.controllers.processors.NonCommandProcessor;
import org.doctordrue.sticker_bot.controllers.processors.stickers.StickerReplyProcessor;
import org.doctordrue.sticker_bot.exceptions.BaseBotException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrey_Barantsev
 * 5/20/2022
 **/
@Component
public class StickersBot extends TelegramLongPollingCommandBot {

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private StartCommand startCommand;
    @Autowired
    private HelpCommand helpCommand;
    @Autowired
    private SetTimeoutCommand setTimeoutCommand;
    @Autowired
    private AddStickerPackCommand addStickerPackCommand;
    @Autowired
    private StickerPackCommand stickerPackCommand;
    @Autowired
    private StickerPacksCommand stickerPacksCommand;
    @Autowired
    private RemoveStickerPackCommand removeStickerPackCommand;

    @Autowired
    private RerollCommand rerollCommand;

    @Autowired
    private SetRerollTimeoutCommand setRerollTimeoutCommand;

    @Autowired
    private StickerReplyProcessor stickerReplyProcessor;

    private final NonCommandProcessor nonCommandProcessor;

    public StickersBot() {
        super();
        this.nonCommandProcessor = new NonCommandProcessor(this::getBotUsername);
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        this.nonCommandProcessor.execute(this, update);
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onRegister() {
        this.register(startCommand);
        this.register(helpCommand);
        this.register(setTimeoutCommand);
        this.register(addStickerPackCommand);
        this.register(stickerPackCommand);
        this.register(stickerPacksCommand);
        this.register(removeStickerPackCommand);
        this.register(rerollCommand);
        this.register(setRerollTimeoutCommand);
        this.nonCommandProcessor.register(stickerReplyProcessor);

        try {
            this.execute(SetMyCommands.builder()
                    .clearCommands()
                    .commands(this.getRegisteredCommands().stream()
                            .map(c -> BotCommand.builder()
                                    .command(c.getCommandIdentifier())
                                    .description(c.getDescription())
                                    .build())
                            .collect(Collectors.toList()))
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            try {
                this.onUpdateReceived(update);
            } catch (BaseBotException e) {
                e.sendReplyMessageIfNeeded(this);
            }
        }
    }
}
