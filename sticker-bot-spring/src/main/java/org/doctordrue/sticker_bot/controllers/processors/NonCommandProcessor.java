package org.doctordrue.sticker_bot.controllers.processors;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.doctordrue.sticker_bot.controllers.processors.common.BaseUpdateProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * @author Andrey_Barantsev
 * 5/11/2022
 **/
public class NonCommandProcessor {

   private final List<BaseUpdateProcessor> processors;
   private final Supplier<String> botUsernameSupplier;


   public NonCommandProcessor(Supplier<String> botUsernameSupplier) {
      this.processors =  new LinkedList<>();
      this.botUsernameSupplier = botUsernameSupplier;
   }

   public NonCommandProcessor register(BaseUpdateProcessor processor) {
      processor.setBotUsernameSupplier(this.botUsernameSupplier);
      this.processors.add(processor);
      return this;
   }

   public void execute(AbsSender absSender, Update update) {
      if (update.hasMessage()) {
         processors.forEach(p -> p.execute(absSender, update));
      }
   }

}
