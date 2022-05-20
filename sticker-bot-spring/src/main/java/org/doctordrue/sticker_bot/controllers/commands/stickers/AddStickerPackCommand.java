package org.doctordrue.sticker_bot.controllers.commands.stickers;

import java.util.Optional;

import org.doctordrue.sticker_bot.services.StickerPackService;
import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.stickers.StickerSet;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Component
public class AddStickerPackCommand extends ManCommand {
   private final StickerPackService stickerPackService;
   private final TelegramChatService telegramChatService;

   public AddStickerPackCommand(StickerPackService stickerPackService, TelegramChatService telegramChatService) {
      super(
              "addstickerpack",
              "Добавляет стикер-пак в сет возможных для ответа стикеров",
              "Формат:\n" +
                      "/addstickerpack &lt;название стикерпака&gt; - для получения названия стикерпака можно послать команду /stickerpack в ответ на сообщение с понравившимся стикером\n" +
                      "/addstickerpack - послать в ответ на сообщение со стикером - добавить в сет стикерпак с этим стикером");
      this.stickerPackService = stickerPackService;
      this.telegramChatService = telegramChatService;
   }

   @Override
   public void processMessage(AbsSender absSender, Message message, String[] arguments) {
      SendMessage.SendMessageBuilder builder = SendMessage.builder().chatId(message.getChatId().toString());
      Optional<String> maybeSetName = Optional.empty();
      if (message.isReply()) {
         if (message.getReplyToMessage().hasSticker()) {
            // command sent as reply to sticker - so adding stickerpack of this sticker
            maybeSetName = Optional.of(message.getReplyToMessage().getSticker().getSetName());
         }
      }
      if (maybeSetName.isEmpty() && arguments.length > 0) {
         // try to get sticker set name from arguments
         maybeSetName = getStickerSet(absSender, arguments[0]).map(StickerSet::getName);
      }
      if (maybeSetName.isPresent()) {
         this.telegramChatService.addStickerSet(message.getChatId(), maybeSetName.get());
         builder.text("Стикер сет " + maybeSetName.get() + " добавлен в чат");
      } else {
         if (arguments.length > 0) {
            builder.text("Стикер сет '" + arguments[0] + "' не найден");
         } else {
            builder.text(this.getDescription());
         }
      }
      try {
         absSender.execute(builder.build());
      } catch (TelegramApiException e) {
         throw new RuntimeException(e);
      }
   }

   private Optional<StickerSet> getStickerSet(AbsSender sender, String stickerSetName) {
      Optional<StickerSet> maybeStickerSet = Optional.empty();
      try {
         maybeStickerSet = Optional.of(this.stickerPackService.findStickerSetByName(sender, stickerSetName));
      } catch (TelegramApiException ignored) {
         //
      }
      return maybeStickerSet;
   }

   @Override
   public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

   }
}
