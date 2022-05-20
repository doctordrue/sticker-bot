package org.doctordrue.sticker_bot.controllers.commands;

import org.doctordrue.sticker_bot.data.entities.TelegramChatSettings;
import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/12/2022
 **/
@Component
public class StartCommand extends ManCommand {
   private final TelegramChatService telegramChatService;

   public StartCommand(TelegramChatService telegramChatService) {
      super(
              "start",
              "Инициализирует бота в новом чате",
              "Формат:\n" +
                      "/start");
      this.telegramChatService = telegramChatService;
   }

   @Override
   public void processMessage(AbsSender absSender, Message message, String[] arguments) {
      this.execute(absSender, message.getMessageId(), message.getChat());
   }

   @Override
   public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

   }

   public void execute(AbsSender absSender, Integer messageId, Chat chat) {
      SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(chat.getId().toString()).disableNotification(true);
         TelegramChatSettings settings = this.telegramChatService.getOrCreate(chat.getId());
         String chatName = chat.getTitle();
         String description = chat.getDescription();
         this.telegramChatService.update(settings);
         builder.text("Бот инициализирован, настройки чата сохранены в базе данных\n" +
                 "timeout = " + settings.getReplyDuration().getSeconds() + " секунд\n" +
                 "stickerpacks = " + settings.getStickerSetNames().toString());
      try {
         absSender.execute(builder.build());
      } catch (TelegramApiException e) {
         e.printStackTrace();
      }
   }
}
