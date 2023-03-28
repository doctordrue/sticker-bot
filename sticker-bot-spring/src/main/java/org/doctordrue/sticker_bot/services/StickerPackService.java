package org.doctordrue.sticker_bot.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.api.objects.stickers.StickerSet;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Service
@Log4j2
public class StickerPackService {
    @Autowired
    private TelegramChatService telegramChatService;

    private static final long MIN_STICKER_CHOOSING_TIME_SECONDS = 2;
    private static final long MAX_STICKER_CHOOSING_TIME_SECONDS = 6;

    public StickerSet findStickerSetByName(AbsSender sender, String stickerSetName) throws TelegramApiException {
        return sender.execute(GetStickerSet.builder().name(stickerSetName).build());
    }

    public Sticker getRandomSticker(AbsSender sender, String stickerSetName) throws TelegramApiException {
        StickerSet stickerSet = this.findStickerSetByName(sender, stickerSetName);
        return stickerSet.getStickers().get(ThreadLocalRandom.current().nextInt(stickerSet.getStickers().size()));
    }

    public void sendSticker(AbsSender sender, Long chatId, Sticker sticker, Message replyOnMessage) throws TelegramApiException, InterruptedException {
        sender.execute(SendChatAction.builder().chatId(chatId.toString()).action("choose_sticker").build());
        long randomMillis = ThreadLocalRandom.current().nextLong(MIN_STICKER_CHOOSING_TIME_SECONDS * 1000, MAX_STICKER_CHOOSING_TIME_SECONDS * 1000);
        Thread.sleep(randomMillis);
        Message message = sender.execute(SendSticker.builder()
                .chatId(chatId.toString())
                .sticker(new InputFile(sticker.getFileId()))
                .replyToMessageId(replyOnMessage == null ? null : replyOnMessage.getMessageId())
                .build());
        this.telegramChatService.updateLastStickerMessage(chatId, message);
    }

    public void sendSticker(AbsSender sender, Long chatId, Sticker sticker) throws TelegramApiException, InterruptedException {
        this.sendSticker(sender, chatId, sticker, null);
    }

}
