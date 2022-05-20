package org.doctordrue.sticker_bot.data.entities;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "telegram_chat_settings")
public class TelegramChatSettings {
   private static final Long DEFAULT_REPLY_DURATION_SECONDS = 120L;
   private static final String DEFAULT_STICKER_SET_NAME = "MrPepe";

   @Id
   @Column(name = "chat_id", nullable = false)
   private Long chatId;

   @ElementCollection(fetch = FetchType.EAGER)
   private Set<String> stickerSetNames = new HashSet<>();

   @Column(name = "reply_duration")
   private Duration replyDuration = Duration.ofSeconds(DEFAULT_REPLY_DURATION_SECONDS);

   public Long getChatId() {
      return chatId;
   }

   public TelegramChatSettings setChatId(Long chatId) {
      this.chatId = chatId;
      return this;
   }

   public Set<String> getStickerSetNames() {
      return stickerSetNames;
   }

   public TelegramChatSettings setStickerSetNames(Set<String> stickerSetNames) {
      this.stickerSetNames = stickerSetNames;
      return this;
   }

   public Duration getReplyDuration() {
      return replyDuration;
   }

   public TelegramChatSettings setReplyDuration(Duration replyDuration) {
      this.replyDuration = replyDuration;
      return this;
   }

   public TelegramChatSettings addStickerSet(String stickerSetName) {
      this.stickerSetNames.add(stickerSetName);
      return this;
   }

   public static TelegramChatSettings createDefault(Long chatId) {
      return new TelegramChatSettings().setChatId(chatId)
              .setReplyDuration(Duration.ofSeconds(DEFAULT_REPLY_DURATION_SECONDS))
              .addStickerSet(DEFAULT_STICKER_SET_NAME);
   }
}