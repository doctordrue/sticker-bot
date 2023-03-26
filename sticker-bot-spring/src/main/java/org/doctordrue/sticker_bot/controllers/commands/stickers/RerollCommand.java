package org.doctordrue.sticker_bot.controllers.commands.stickers;

import org.doctordrue.sticker_bot.services.StickerPackService;
import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class RerollCommand extends BotCommand {


    private final TelegramChatService telegramChatService;


    private final StickerPackService stickerPackService;

    public RerollCommand(TelegramChatService telegramChatService, StickerPackService stickerPackService) {
        super("reroll", "Обновляет последнее сообщение бота новым стикером");
        this.telegramChatService = telegramChatService;
        this.stickerPackService = stickerPackService;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        final Chat chat = message.getChat();
        final Integer stickerMessageId = this.telegramChatService.getOrCreate(chat.getId()).getLastStickerMessageId();
        if (stickerMessageId != null) {
            try {
                boolean isDeleted = absSender.execute(DeleteMessage.builder().chatId(chat.getId()).messageId(stickerMessageId).build());
                if (isDeleted) {
                    // send new sticker
                    String randomStickerSetName = this.telegramChatService.getRandomStickerSetName(chat.getId());
                    Sticker sticker = this.stickerPackService.getRandomSticker(absSender, randomStickerSetName);
                    this.stickerPackService.sendSticker(absSender, chat.getId(), sticker);

                    // delete command message
                    absSender.execute(DeleteMessage.builder().chatId(message.getChatId()).messageId(message.getMessageId()).build());
                } else {
                    absSender.execute(SendMessage.builder().chatId(chat.getId()).replyToMessageId(message.getMessageId())
                            .disableNotification(true)
                            .replyToMessageId(message.getMessageId())
                            .text("Что-то пошло не так :(")
                            .build());
                }
            } catch (TelegramApiException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

    }

}
