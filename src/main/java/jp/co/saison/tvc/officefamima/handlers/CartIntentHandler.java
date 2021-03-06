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
    public static final String QUANTITY_KEY = "QUANTITY";
    public static final String QUANTITY_SLOT = "Quantity";

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("CartIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
		Session.maintain(input);

		Request request = input.getRequestEnvelope().getRequest();
		IntentRequest intentRequest = (IntentRequest) request;
		Intent intent = intentRequest.getIntent();
		Map<String, Slot> slots = intent.getSlots();

		// Get the color slot from the list of slots.
		Slot itemSlot = slots.get(ITEM_SLOT);

		String speechText, repromptText;
		boolean isAskResponse = false;
		String quantity = null;

		Slot quantitySlot = slots.get(QUANTITY_SLOT);
		if (quantitySlot != null) {
			quantity = quantitySlot.getValue();
		}
		if (quantity == null) {
			quantity = "1";
		}

		// Check for favorite color and create output to user.
		if (itemSlot != null) {
			// Store the user's favorite color in the Session and create response.
			DBResource db = new DBResource(Session.getCartKey(input));

			String targetItem = itemSlot.getValue();
			if (targetItem == null) {
				targetItem = Session.GetItemfromSession(input);
			}

			// {Item}のインテントでこのロジックに入った場合は、セッションではなくitemSlotから情報を取得
			if (db.existItem(targetItem)) {
				Session.AddItemToSession(input, targetItem);
				speechText = String.format(targetItem + "を、" + quantity + "個カートにいれました。");
				db.addItemCart(targetItem, quantity);
				repromptText = "";
			} else {
				speechText = targetItem + "はラインナップにありません。";
				repromptText = "商品名を教えてください。";
				isAskResponse = true;
			}
		} else {
			// Render an error since we don't know what the users favorite color is.
			speechText = "分かりません。";
			repromptText = "商品名を教えてください。";
			isAskResponse = true;
		}

		ResponseBuilder responseBuilder = input.getResponseBuilder();

		responseBuilder.withSimpleCard("ColorSession", speechText)
				.withSpeech(speechText)
				.withShouldEndSession(false);

		if (isAskResponse) {
		    responseBuilder.withShouldEndSession(false)
		            .withReprompt(repromptText);
		}

		return responseBuilder.build();
    }

}
