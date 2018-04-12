/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package jp.co.saison.tvc.officefamima.handlers;

import static com.amazon.ask.request.Predicates.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.response.ResponseBuilder;

public class CartIntentHandler implements RequestHandler {
    public static final String ITEM_KEY = "ITEM";
    public static final String CART_KEY = "CART";
    public static final String ITEM_SLOT = "Item";

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("CartIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
      CartKey.SessionContinue(input);  

      
      Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        // Get the color slot from the list of slots.
        Slot itemSlot = slots.get(ITEM_SLOT);

        String speechText, repromptText;
        boolean isAskResponse = false;

        // Check for favorite color and create output to user.
        if (itemSlot != null) {
            // Store the user's favorite color in the Session and create response.
            DBResource db = new DBResource();

            // {Item}のインテントでこのロジックに入った場合は、セッションではなくitemSlotから情報を取得
            String targetItem = (String) input.getAttributesManager().getSessionAttributes().get(ITEM_KEY);
            String cartkey = (String) input.getAttributesManager().getSessionAttributes().get(CART_KEY);

 
            
            //ありえない
            if (cartkey == null) {
              //todo launchのハンドらで今回のセッション管理用のUUIDを払い出す必要がある
                input.getAttributesManager().setSessionAttributes(Collections.singletonMap(CART_KEY, targetItem));
            }

            if (db.existItem(targetItem)) {
        		speechText =
        				String.format(targetItem + "をカートにいれました。");
        		db.addItemCart(targetItem);
            
        		//今回カートに入れるものをセッションに残す。次{ITEM}のないカートリクエストは、今回カートに入れたものと同じとする
            } else {
        		speechText =
        				targetItem + "はラインナップにありません。";
            }
                        
            repromptText =
                    "You can ask me your favorite color by saying, what's my favorite color?";

        } else {
            // Render an error since we don't know what the users favorite color is.
            speechText = "分かりません。";
            repromptText =
                    "I'm not sure what your favorite color is. You can tell me your favorite "
                            + "color by saying, my color is red";
            isAskResponse = true;
        }

        
        ResponseBuilder responseBuilder = input.getResponseBuilder();

        responseBuilder.withSimpleCard("ColorSession", speechText)
                .withSpeech(speechText)
                .withShouldEndSession(false);

        /*
        if (isAskResponse) {
            responseBuilder.withShouldEndSession(false)
                    .withReprompt(repromptText);
        }
         */

        return responseBuilder.build();
    }

}
