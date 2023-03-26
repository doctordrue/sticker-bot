package org.doctordrue.sticker_bot.controllers.processors.other;

import org.doctordrue.sticker_bot.controllers.processors.common.BaseUpdateProcessor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

/**
 * @author Andrey_Barantsev
 * 5/11/2022
 **/
public class ReplyToBotMentionProcessor extends BaseUpdateProcessor {

    @Override
    public void execute(AbsSender absSender, Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if (message.getEntities() != null) {
                Optional<MessageEntity> botMentionOptional = message.getEntities().stream()
                        .filter(e -> "mention".equals(e.getType()))
                        .filter(e -> e.getText().equals("@" + this.getBotUsername()))
                        .findAny();
                if (botMentionOptional.isPresent()) {
                    SendMessage reply = new SendMessage();
                    reply.setText(getReplyMessage(message));
                    reply.setChatId(chatId.toString());
                    reply.setReplyToMessageId(message.getMessageId());
                    try {
                        absSender.execute(reply);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getReplyMessage(Message message) {
        StringBuilder builder = new StringBuilder();
        String text = message.getText().toLowerCase();
        User from = message.getFrom();
        boolean isQuestion = false;
        if (text.contains("ночи")) {
            builder.append("Доброй ночи, ");
        } else if (text.contains("привет")) {
            builder.append("Привет, ");
        } else {
            builder.append("Как дела, ");
            isQuestion = true;
        }
        builder.append(from.getFirstName());
        if (from.getLastName() != null) {
            builder.append(" ").append(from.getLastName());
        }
        if (isQuestion) {
            builder.append("?");
        } else {
            builder.append("!");
        }
        return builder.toString();
    }
}
