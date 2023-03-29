package org.doctordrue.sticker_bot.services;

import org.doctordrue.sticker_bot.exceptions.validation.IllegalTimeoutException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public void verifyTimeout(Long chatId, Long timeoutSeconds, Long allowedMin, Long allowedMax) {
        if (timeoutSeconds < allowedMin || timeoutSeconds > allowedMax) {
            throw new IllegalTimeoutException(chatId, timeoutSeconds, allowedMin, allowedMax);
        }
    }
}
