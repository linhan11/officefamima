package jp.co.saison.tvc.officefamima.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

public class Session {
  public static final String CART_SESSION_NAME = "CART";
  public static final String ITEM_SESSION_NAME = "ITEM";
  
  public static void SessionContinue(HandlerInput input) {
    input.getAttributesManager().setSessionAttributes(input.getAttributesManager().getSessionAttributes());
  }
  
  public static void AddItemToSession(HandlerInput input, String item) {
    Map<String,Object> map = input.getAttributesManager().getSessionAttributes();
    map.put(ITEM_SESSION_NAME, item);
    input.getAttributesManager().setSessionAttributes(map);
  }
  
  public static String GetItemfromSession(HandlerInput input) {
    return (String) input.getAttributesManager().getSessionAttributes().get(ITEM_SESSION_NAME);
  }

  public static String getCartKey(HandlerInput input) {
    String cart = (String) input.getAttributesManager().getSessionAttributes().get(CART_SESSION_NAME);
    if (cart == null || cart.isEmpty()) {
      cart = UUID.randomUUID().toString();
      Map<String,Object> map = input.getAttributesManager().getSessionAttributes();
      map.put(CART_SESSION_NAME, cart);
      input.getAttributesManager().setSessionAttributes(map);
    }
    return cart;
  }
}
