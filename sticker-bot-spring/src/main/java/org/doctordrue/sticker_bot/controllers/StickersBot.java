package org.doctordrue.sticker_bot.controllers;

import org.doctordrue.sticker_bot.controllers.commands.StartCommand;
import org.doctordrue.sticker_bot.controllers.commands.stickers.AddStickerPackCommand;
import org.doctordrue.sticker_bot.controllers.commands.stickers.SetTimeoutCommand;
import org.doctordrue.sticker_bot.controllers.commands.stickers.StickerPackCommand;
import org.doctordrue.sticker_bot.controllers.commands.stickers.StickerPacksCommand;
import org.doctordrue.sticker_bot.controllers.processors.NonCommandProcessor;
import org.doctordrue.sticker_bot.controllers.processors.stickers.StickerReplyProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

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
   private SetTimeoutCommand setTimeoutCommand;
   @Autowired
   private AddStickerPackCommand addStickerPackCommand;
   @Autowired
   private StickerPackCommand stickerPackCommand;
   @Autowired
   private StickerPacksCommand stickerPacksCommand;

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
      this.register(setTimeoutCommand);
      this.register(addStickerPackCommand);
      this.register(stickerPackCommand);
      this.register(stickerPacksCommand);
      this.nonCommandProcessor.register(stickerReplyProcessor);
   }
}
