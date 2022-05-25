package org.doctordrue.sticker_bot.services;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.api.objects.stickers.StickerSet;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Service
public class StickerPackService {

   private static final long MIN_STICKER_CHOOSING_TIME_SECONDS = 2;
   private static final long MAX_STICKER_CHOOSING_TIME_SECONDS = 6;

   public StickerSet findStickerSetByName(AbsSender sender, String stickerSetName) throws TelegramApiException {
      return sender.execute(GetStickerSet.builder().name(stickerSetName).build());
   }

   public Sticker getRandomSticker(AbsSender sender, String stickerSetName) throws TelegramApiException {
      StickerSet stickerSet = this.findStickerSetByName(sender, stickerSetName);
      return stickerSet.getStickers().get(new Random().nextInt(stickerSet.getStickers().size()));
   }

   public void sendSticker(AbsSender sender, Long chatId, Sticker sticker) throws TelegramApiException, InterruptedException {
      sender.execute(SendChatAction.builder().chatId(chatId.toString()).action("choose_sticker").build());
      long randomMillis = ThreadLocalRandom.current().nextLong(MIN_STICKER_CHOOSING_TIME_SECONDS * 1000, MAX_STICKER_CHOOSING_TIME_SECONDS * 1000);
      Thread.sleep(randomMillis);
      sender.execute(SendSticker.builder().chatId(chatId.toString()).sticker(new InputFile(sticker.getFileId())).build());
   }

}
