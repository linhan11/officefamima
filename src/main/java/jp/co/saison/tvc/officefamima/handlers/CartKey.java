package jp.co.saison.tvc.officefamima.handlers;

import java.util.Collections;
import java.util.UUID;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

public class CartKey {
  public static final String CART_SESSION_NAME = "CART";
  
  public static void SessionContinue(HandlerInput input) {
    input.getAttributesManager().setSessionAttributes(Collections.singletonMap(CART_SESSION_NAME, getCartKey(input)));
  }

  public static String getCartKey(HandlerInput input) {
    String cart = (String) input.getAttributesManager().getSessionAttributes().get(CART_SESSION_NAME);
    if (cart == null || cart.isEmpty()) {
      cart = UUID.randomUUID().toString();
      input.getAttributesManager().setSessionAttributes(Collections.singletonMap(CART_SESSION_NAME, cart));
    }
    return cart;
  }
}
