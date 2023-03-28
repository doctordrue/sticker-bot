package org.doctordrue.sticker_bot.data.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "telegram_chat_settings")
@Accessors(chain = true)
public class TelegramChatSettings {

    private static final Long DEFAULT_REPLY_DURATION_SECONDS = 120L;
    private static final String DEFAULT_STICKER_SET_NAME = "MrPepe";
    private static final Long DEFAULT_REROLL_DURATION_SECONDS = 60L;

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "last_sticker_message_id")
    @Getter
    @Setter
    private Integer lastStickerMessageId;

    @Column(name = "last_sticker_message_timestamp")
    @Getter
    @Setter
    private LocalDateTime lastStickerMessageTimestamp;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> stickerSetNames = new HashSet<>();

    @Column(name = "reply_duration")
    private Duration replyDuration = Duration.ofSeconds(DEFAULT_REPLY_DURATION_SECONDS);

    @Column(name = "reroll_duration")
    @Getter
    @Setter
    @NotNull
    private Duration rerollDuration = Duration.ofSeconds(DEFAULT_REROLL_DURATION_SECONDS);

    @Column(name = "remove_tries")
    private Integer removeTries;

    public Integer getRemoveTries() {
        return removeTries == null ? 0 : removeTries;
    }

    public TelegramChatSettings setRemoveTries(Integer removeTries) {
        this.removeTries = removeTries;
        return this;
    }

    public TelegramChatSettings increaseRemoveTries() {
        this.setRemoveTries(getRemoveTries() + 1);
        return this;
    }

    public TelegramChatSettings resetRemoveTries() {
        this.removeTries = 0;
        return this;
    }

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

    public boolean removeStickerSet(String stickerSetName) {
        return this.stickerSetNames.remove(stickerSetName);
    }

    public static TelegramChatSettings createDefault(Long chatId) {
        return new TelegramChatSettings().setChatId(chatId)
                .setReplyDuration(Duration.ofSeconds(DEFAULT_REPLY_DURATION_SECONDS))
                .setRerollDuration(Duration.ofSeconds(DEFAULT_REROLL_DURATION_SECONDS))
                .setRemoveTries(0)
                .addStickerSet(DEFAULT_STICKER_SET_NAME);
    }

    @PrePersist
    @PostLoad
    private void setDefaults() {
        if (this.getRerollDuration() == null) {
            this.setRerollDuration(Duration.ofSeconds(DEFAULT_REROLL_DURATION_SECONDS));
        }
    }

}