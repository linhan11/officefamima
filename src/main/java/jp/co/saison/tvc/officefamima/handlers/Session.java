package jp.co.saison.tvc.officefamima.handlers;

import java.util.Map;
import java.util.UUID;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;

/**
 * セッションを管理するクラス
 * カートIDは買い物中の情報をDynamoDBに保持する場合の検索キーとなる
 * Lambdaではインスタンスを持ちまわすことができないため、このクラスはすべてクラスメソッドになっている
 * @author H1007694
 *
 */
public class Session {
  private static final String CART_SESSION_NAME = "CART";
  private static final String ITEM_SESSION_NAME = "ITEM";

  /**
   * 今あるセッションをそのまま維持する
   * @param input
   */
  public static void maintain(HandlerInput input) {
	getCartKey(input);//カートIDが未発行の場合は発行する
    input.getAttributesManager().setSessionAttributes(input.getAttributesManager().getSessionAttributes());
  }

  /**
   * ユーザが発話した商品名をセッションに追加する
   * @param input
   * @param item
   */
  public static void AddItemToSession(HandlerInput input, String item) {
    Map<String,Object> map = input.getAttributesManager().getSessionAttributes();
    map.put(ITEM_SESSION_NAME, item);
    input.getAttributesManager().setSessionAttributes(map);
  }

  /**
   * セッションから商品名を返却する
   * @param input
   * @return
   */
  public static String GetItemfromSession(HandlerInput input) {
    return (String) input.getAttributesManager().getSessionAttributes().get(ITEM_SESSION_NAME);
  }

  /**
   * セッションからカートIDを返却する
   * @param input
   * @return
   */
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
