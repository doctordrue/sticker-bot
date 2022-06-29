package org.doctordrue.sticker_bot.services;

import java.time.Duration;
import java.util.Random;
import java.util.Set;

import org.doctordrue.sticker_bot.data.entities.TelegramChatSettings;
import org.doctordrue.sticker_bot.data.repositories.TelegramChatSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Service
public class TelegramChatService {

   @Autowired
   private TelegramChatSettingsRepository telegramChatSettingsRepository;

   public TelegramChatSettings getOrCreate(Long chatId) {
      return this.telegramChatSettingsRepository.findById(chatId)
              .orElseGet(() -> this.telegramChatSettingsRepository.save(TelegramChatSettings.createDefault(chatId)));
   }

   public TelegramChatSettings update(TelegramChatSettings settings) {
      return this.telegramChatSettingsRepository.save(settings);
   }

   public TelegramChatSettings addStickerSet(Long chatId, String stickerSetName) {
      TelegramChatSettings persistedSettings = this.getOrCreate(chatId);
      persistedSettings.addStickerSet(stickerSetName);
      return this.telegramChatSettingsRepository.save(persistedSettings);
   }

   public boolean removeStickerSet(Long chatId, String stickerSetName) {
      TelegramChatSettings persistedSettings = this.getOrCreate(chatId);
      if (persistedSettings.removeStickerSet(stickerSetName)) {
         this.telegramChatSettingsRepository.save(persistedSettings);
         return true;
      }
      return false;
   }

   public String getRandomStickerSetName(Long chatId) {
      final Set<String> stickerSetNames = this.getOrCreate(chatId).getStickerSetNames();
      return stickerSetNames.stream().skip(new Random().nextInt(stickerSetNames.size())).findAny().orElse(null);
   }

   public Set<String> getStickerSetNames(Long chatId) {
      return this.getOrCreate(chatId).getStickerSetNames();
   }

   public TelegramChatSettings setTimeout(Long chatId, Long timeoutSeconds) {
      TelegramChatSettings settings = this.getOrCreate(chatId);
      settings.setReplyDuration(Duration.ofSeconds(timeoutSeconds).abs());
      return this.telegramChatSettingsRepository.save(settings);
   }

}
