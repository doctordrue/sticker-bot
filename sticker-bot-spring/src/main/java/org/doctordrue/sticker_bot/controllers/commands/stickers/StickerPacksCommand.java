package org.doctordrue.sticker_bot.controllers.commands.stickers;

import java.util.Set;

import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Component
public class StickerPacksCommand extends BotCommand {

   private final TelegramChatService telegramChatService;

   public StickerPacksCommand(TelegramChatService telegramChatService) {
      super("stickerpacks", "Посмотреть названия текущих стикерпаков доступных для ответов бота");
      this.telegramChatService = telegramChatService;
   }

   @Override
   public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
      Set<String> stickerSetNames = this.telegramChatService.getStickerSetNames(chat.getId());
      StringBuilder sb = new StringBuilder("Стикер-сеты доступные в группе:\n");
      stickerSetNames.forEach(s -> sb.append(s).append("\n"));
      try {
         absSender.execute(SendMessage.builder().text(sb.toString()).chatId(chat.getId().toString()).build());
      } catch (TelegramApiException e) {
         throw new RuntimeException(e);
      }
   }
}
