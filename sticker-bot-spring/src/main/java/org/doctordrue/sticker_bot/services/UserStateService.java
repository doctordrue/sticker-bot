package org.doctordrue.sticker_bot.services;

import org.doctordrue.sticker_bot.data.dto.UserState;
import org.doctordrue.sticker_bot.exceptions.user.UserTemporaryBlockedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserStateService {

    @Value("#{T(java.time.Duration).ofMillis(${telegram.bot.mention.cooldown.timeout_ms})}")
    private Duration cooldownTimeout;
    @Value("#{T(java.time.Duration).ofSeconds(${telegram.bot.mention.cooldown.duration_sec})}")
    private Duration cooldownDuration;
    @Value("${telegram.bot.mention.cooldown.max_messages_in_timeout_allowed}")
    private Integer maxAllowedMessages;
    private final Map<Long, UserState> userStateMap = new HashMap<>();

    /**
     * Verify <b>user</b> can trigger boot to reply with sticker.
     * E.g. if user triggers bot too often system can restrict access to bot for some time for this user
     *
     * @param message {@link Message} sent in update which is supposed to trigger a bot sticker reply
     */
    public void verifyUserCanSendMessage(Message message) {
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


}
