package org.doctordrue.sticker_bot.services;

import org.doctordrue.sticker_bot.data.entities.TelegramChatSettings;
import org.doctordrue.sticker_bot.data.repositories.TelegramChatSettingsRepository;
import org.doctordrue.sticker_bot.exceptions.reroll.UnableToRerollException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.Set;

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

    public TelegramChatSettings setReplyDuration(Long chatId, Long timeoutSeconds) {
        TelegramChatSettings settings = this.getOrCreate(chatId);
        settings.setReplyDuration(Duration.ofSeconds(timeoutSeconds).abs());
        return this.telegramChatSettingsRepository.save(settings);
    }

    public TelegramChatSettings setRerollDuration(Long chatId, Long timeoutSeconds) {
        TelegramChatSettings settings = this.getOrCreate(chatId);
        settings.setRerollDuration(Duration.ofSeconds(timeoutSeconds));
        return this.telegramChatSettingsRepository.save(settings);
    }

    public void updateLastStickerMessage(Long chatId, Message message) {
        TelegramChatSettings persistedSetting = this.getOrCreate(chatId);
        persistedSetting.setLastStickerMessageId(message.getMessageId());
        persistedSetting.setLastStickerMessageTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()));
        this.telegramChatSettingsRepository.save(persistedSetting);
    }

    public void verifyCanRerollBy(Message rerollCommandMessage) {
        final TelegramChatSettings settings = this.getOrCreate(rerollCommandMessage.getChatId());
        final LocalDateTime lastStickerTimestamp = settings.getLastStickerMessageTimestamp();
        final LocalDateTime commandTimestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(rerollCommandMessage.getDate()), ZoneId.systemDefault());

        if (lastStickerTimestamp != null && lastStickerTimestamp.plus(settings.getRerollDuration()).isBefore(commandTimestamp)) {
            throw new UnableToRerollException(rerollCommandMessage);
        }
    }

}
