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
import com.amazon.ask.response.ResponseBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

/**
 * 現在カートに入っている商品を答える
 */
public class AskCartInfoIntentHandler implements RequestHandler {
  @Override
  public boolean canHandle(HandlerInput input) {
    return input.matches(intentName("AskCartInfoIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput input) {
    Session.maintain(input);
    String speechText;
    int nItem = 0;
    int total = 0;

    DBResource db = new DBResource(Session.getCartKey(input));
    StringBuffer sb = new StringBuffer();
 
    sb.append("現在カートの中には");
    for (Map.Entry<String, String> e : db.scanForCartTable().entrySet()) {
      sb.append(String.format("%s%s", nItem == 0 ? "" : "と", e.getKey()));
      nItem++;
            int price = Integer.parseInt(db.getPrice(e.getKey()));
      int quantity = Integer.parseInt(e.getValue());
      total += price * quantity;
    }
        
    if (nItem == 0) {
      speechText = "現在カートに何も入っていません。";
    }else {
      sb.append(String.format("の計%d点が入っています。金額は%d円です。", nItem, total));
      speechText = sb.toString();      
    }

    ResponseBuilder responseBuilder = input.getResponseBuilder();

    responseBuilder.withSimpleCard("ColorSession", speechText).withSpeech(speechText)
        .withShouldEndSession(false);

    responseBuilder.withShouldEndSession(false).withReprompt(speechText);
    return responseBuilder.build();
  }

}
