package org.doctordrue.sticker_bot.controllers.processors.stickers;

import java.time.Instant;

/**
 * @author Andrey_Barantsev
 * 5/26/2022
 **/
public class UserState {

   private boolean isTemporaryBlocked = false;
   private boolean isBlockMessageAlreadySent = false;
   private Instant timeoutStartedAt;
   private int messagesReceived = 0;

   public UserState(Instant timeoutStartedAt) {
      this.timeoutStartedAt = timeoutStartedAt;
   }

   public boolean isBlockMessageAlreadySent() {
      return isBlockMessageAlreadySent;
   }

   public boolean isTemporaryBlocked() {
      return isTemporaryBlocked;
   }

   public UserState setBlockMessageAlreadySent(boolean blockMessageAlreadySent) {
      isBlockMessageAlreadySent = blockMessageAlreadySent;
      return this;
   }

   public Instant getTimeoutStartedAt() {
      return timeoutStartedAt;
   }

   public int getMessagesReceived() {
      return messagesReceived;
   }

   public void reset(Instant timeoutStartedAt) {
      this.messagesReceived = 0;
      this.timeoutStartedAt = timeoutStartedAt;
      this.isBlockMessageAlreadySent = false;
      this.isTemporaryBlocked = false;
   }

   public void block() {
      this.isTemporaryBlocked = true;
      this.isBlockMessageAlreadySent = false;
   }

   public void addMessage() {
      this.messagesReceived++;
   }

   public void updateTimeoutStartedAt(Instant timeoutStartedAt) {
      this.timeoutStartedAt = timeoutStartedAt;
   }

   public static UserState of(Instant timeoutStartedAt) {
      return new UserState(timeoutStartedAt);
   }

}
