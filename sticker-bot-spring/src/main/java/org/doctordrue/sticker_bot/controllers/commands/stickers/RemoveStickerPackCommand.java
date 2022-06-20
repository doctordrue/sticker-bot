package org.doctordrue.sticker_bot.controllers.commands.stickers;

import java.util.Optional;

import org.doctordrue.sticker_bot.services.StickerPackService;
import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.beans.factory.annotation.Value;
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
 * 6/20/2022
 **/
@Component
public class RemoveStickerPackCommand extends ManCommand {

   private final StickerPackService stickerPackService;
   private final TelegramChatService telegramChatService;

   @Value("${telegram.bot.stickerpack.cannot_be_removed.regex}")
   private String notRemovableRegex;

   public RemoveStickerPackCommand(StickerPackService stickerPackService, TelegramChatService telegramChatService) {
      super(
              "/remove",
              "Удаляет стикеры из стикер-пака из набора возможных для ответа стикеров",
              "Формат:\n" +
                      "/remove &lt;название стикерпака&gt; - для получения названия стикерпака можно послать команду /stickerpack в ответ на сообщение с понравившимся стикером\n" +
                      "/remove - послать в ответ на сообщение со стикером - удалить из набора стикерпак с этим стикером");
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
         if (maybeSetName.get().matches(notRemovableRegex)) {
            // Easter egg ;)
            builder.text("Нельзя удалить " + maybeSetName.get() + "!");
         } else {
            if (this.telegramChatService.removeStickerSet(message.getChatId(), maybeSetName.get())) {
               builder.text("Стикер сет " + maybeSetName.get() + " удален");
            } else {
               builder.text("Стикер сет " + maybeSetName.get() + " не найден в настройках чата");
            }
         }
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
