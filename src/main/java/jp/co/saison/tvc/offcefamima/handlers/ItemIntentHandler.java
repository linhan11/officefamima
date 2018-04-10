/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package jp.co.saison.tvc.offcefamima.handlers;

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

public class ItemIntentHandler implements RequestHandler {
    public static final String ITEM_KEY = "ITEM";
    public static final String ITEM_SLOT = "Item";

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("ItemIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
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
            String targetItem = itemSlot.getValue();
            input.getAttributesManager().setSessionAttributes(Collections.singletonMap(ITEM_KEY, targetItem));

            speechText =
                    String.format("%sは１００円です。", targetItem);
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
