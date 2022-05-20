package org.doctordrue.sticker_bot.data.repositories;

import org.doctordrue.sticker_bot.data.entities.TelegramChatSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramChatSettingsRepository extends JpaRepository<TelegramChatSettings, Long> {

}