/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except
 * in compliance with the License. A copy of the License is located at
 * 
 * http://aws.amazon.com/apache2.0/
 * 
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package jp.co.saison.tvc.officefamima.handlers;

import static com.amazon.ask.request.Predicates.*;
import java.util.Map;
import java.util.Optional;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

// カートに入っているものを精算する
public class CheckIntentHandler implements RequestHandler {
  @Override
  public boolean canHandle(HandlerInput input) {
    return input.matches(intentName("CheckIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput input) {
    Session.maintain(input);
    String speechText;

    DBResource db = new DBResource(Session.getCartKey(input));

    StringBuffer sb = new StringBuffer();
    int total = 0;

    //カートに入っている商品のスピーチ文字列と合計額を計算
    for (Map.Entry<String, String> e : db.scanForCartTable().entrySet()) {
      sb.append(String.format("%s%s", total == 0 ? "" : "と", e.getKey()));
      int price = Integer.parseInt(db.getPrice(e.getKey()));
      int quantity = Integer.parseInt(e.getValue());
      total += price * quantity;
    }

    if (total == 0) {
      speechText = "カートには何も入っていません。ご利用ありがとうございました。";
    } else {
      sb.append(String.format("で%d円になります。お買い上げありがとうございました。", total));
      speechText = sb.toString();
    }
    return input.getResponseBuilder().withSpeech(speechText)
        .withSimpleCard("ColorSession", "Goodbye").build();
  }
}
