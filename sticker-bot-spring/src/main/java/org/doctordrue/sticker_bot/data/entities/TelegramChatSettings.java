package org.doctordrue.sticker_bot.data.entities;

import javax.persistence.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "telegram_chat_settings")
public class TelegramChatSettings {

    private static final Long DEFAULT_REPLY_DURATION_SECONDS = 120L;
    private static final String DEFAULT_STICKER_SET_NAME = "MrPepe";

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column
    private Integer lastStickerMessageId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> stickerSetNames = new HashSet<>();

    @Column(name = "reply_duration")
    private Duration replyDuration = Duration.ofSeconds(DEFAULT_REPLY_DURATION_SECONDS);

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
                .setRemoveTries(0)
                .addStickerSet(DEFAULT_STICKER_SET_NAME);
    }

    public TelegramChatSettings setLastStickerMessageId(Integer lastStickerMessageId) {
        this.lastStickerMessageId = lastStickerMessageId;
        return this;
    }

    public Integer getLastStickerMessageId() {
        return lastStickerMessageId;
    }
}