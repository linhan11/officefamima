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
import java.util.stream.Collectors;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

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
    //db.scanForCartTable();
 
    for (Map.Entry<String, String> e : db.scanForCartTable().entrySet()) {
      sb.append(String.format("%s%s", total == 0 ? "":"と", e.getKey()));
      int price = Integer.parseInt(db.getPrice(e.getKey()));
      int quantity = Integer.parseInt(e.getValue());
      total += price * quantity;
    }
 
    /*
    for (Map<String, AttributeValue> item : db.scanForCartTable()) {
      sb.append(String.format("%s%s%s", total == 0 ? "":"と", item.get("Name").getS(), item.get("Quantity").getN().toString()));
      
                                                                                                
    //int price = Integer.parseInt( db.getPrice(item.get("Name").getS()) );
     
      //int price = Integer.parseInt(db.getPrice(item.get("Name").getS()));
      //int quantity = Integer.parseInt(item.get("Quantity").getS());
      //total += price * quantity;
    }
    */
    sb.append(String.format("で%d円になります。お買い上げありがとうございました。", total));

    speechText = sb.toString();
    /*
    ResponseBuilder responseBuilder = input.getResponseBuilder();

    responseBuilder.withSimpleCard("ColorSession", speechText).withSpeech(speechText)
        .withShouldEndSession(true);

    responseBuilder.withShouldEndSession(true).withReprompt(speechText);

    return responseBuilder.build();
    */
    return input.getResponseBuilder()
        .withSpeech(speechText)
        .withSimpleCard("ColorSession", "Goodbye")
        .build();
  }

}
