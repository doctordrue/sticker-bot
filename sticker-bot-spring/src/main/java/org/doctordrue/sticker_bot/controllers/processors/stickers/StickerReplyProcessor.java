package org.doctordrue.sticker_bot.controllers.processors.stickers;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.doctordrue.sticker_bot.controllers.processors.common.BaseUpdateProcessor;
import org.doctordrue.sticker_bot.data.entities.TelegramChatSettings;
import org.doctordrue.sticker_bot.exceptions.user.UserTemporaryBlockedException;
import org.doctordrue.sticker_bot.services.StickerPackService;
import org.doctordrue.sticker_bot.services.TelegramChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Andrey_Barantsev
 * 5/13/2022
 **/
@Component
public class StickerReplyProcessor extends BaseUpdateProcessor {

   private static final Temporal BEGINNING_OF_TIME = Instant.EPOCH;
   private final Map<Long, Temporal> lastReplyTimestampsMap = new HashMap<>();
   private final Map<Long, UserState> userStateMap = new HashMap<>();

   @Autowired
   private StickerPackService stickerPackService;
   @Autowired
   private TelegramChatService telegramChatService;
   @Value("${telegram.bot.mention.regex}")
   private String botMentionRegex;
   @Value("#{T(java.time.Duration).ofMillis(${telegram.bot.mention.cooldown.timeout_ms})}")
   private Duration cooldownTimeout;
   @Value("#{T(java.time.Duration).ofSeconds(${telegram.bot.mention.cooldown.duration_sec})}")
   private Duration cooldownDuration;
   @Value("${telegram.bot.mention.cooldown.max_messages_in_timeout_allowed}")
   private Integer maxAllowedMessages;

   @Transactional
   @Override
   public void execute(AbsSender absSender, Update update) {
      if (update.hasMessage()) {
         Message message = update.getMessage();
         long chatId = message.getChatId();
         TelegramChatSettings settings = this.telegramChatService.getOrCreate(chatId);
         if (isTimeoutReady(settings) || isMentioned(message, this.getBotUsername())) {
            // timeout exceed or bot was mentioned somehow - bot can reply
            verifyUser(message);
            String stickerSetName = this.telegramChatService.getRandomStickerSetName(chatId);
            try {
               Sticker sticker = this.stickerPackService.getRandomSticker(absSender, stickerSetName);
               this.stickerPackService.sendSticker(absSender, chatId, sticker);
               this.lastReplyTimestampsMap.put(chatId, Instant.now());
            } catch (TelegramApiException | InterruptedException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   /**
    * Verify <b>user</b> can trigger boot to reply with sticker.
    * E.g. if user triggers bot too often system can restrict access to bot for some time for this user
    *
    * @param message {@link Message} sent in update which is supposed to trigger a bot sticker reply
    */
   protected void verifyUser(Message message) {
      User user = message.getFrom();
      final Instant messageSentAt = Instant.ofEpochSecond(message.getDate());

      if (userStateMap.containsKey(user.getId())) {
         UserState state = this.userStateMap.get(user.getId());
         state.addMessage();
         Duration durationSinceLastTimeoutStart = Duration.between(state.getTimeoutStartedAt(), messageSentAt);

         // block & unblock user logic below
         if (state.isTemporaryBlocked()) {
            // blocked user flow here
            if (durationSinceLastTimeoutStart.compareTo(cooldownDuration) > 0) {
               // cooled down already, unblocking
               state.reset(messageSentAt);
            }
            state.updateTimeoutStartedAt(messageSentAt);
         } else {
            // user is not blocked yet, but we possibly will block him
            if (durationSinceLastTimeoutStart.compareTo(cooldownTimeout) > 0) {
               // reset user state, no need to block replies
               state.reset(messageSentAt);
            } else {
               // message received too early, need to check
               state.addMessage();
               if (state.getMessagesReceived() > maxAllowedMessages) {
                  // need to block user
                  state.block();
               }
            }
         }

         // exception logic
         if (state.isTemporaryBlocked()) {
            boolean isReplyNeeded = !state.isBlockMessageAlreadySent();
            state.setBlockMessageAlreadySent(true);
            throw new UserTemporaryBlockedException(message, isReplyNeeded);
         }
      } else {
         userStateMap.put(user.getId(), UserState.of(messageSentAt));
      }
   }

   protected boolean isMentioned(Message message, String userName) {
      List<Predicate<Message>> conditions = Arrays.asList(
              m -> m.getText().matches(botMentionRegex),
              m -> m.hasEntities() && m.getEntities().stream()
                      .filter(e -> "mention".equals(e.getType()))
                      .anyMatch(e -> e.getText().contains("@" + userName)));
      if (message.hasText()) {
         return conditions.stream().anyMatch(c -> c.test(message));
      } else {
         return false;
      }
   }

   private boolean isTimeoutReady(TelegramChatSettings settings) {
      Temporal then = this.lastReplyTimestampsMap.getOrDefault(settings.getChatId(), BEGINNING_OF_TIME);
      Temporal now = Instant.now();
      Duration duration = Duration.between(then, now);
      return settings.getReplyDuration().compareTo(duration) < 0;
   }

}
