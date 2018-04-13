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

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;


public class SummaryIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SummaryIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
    	String speechText;
        Session.maintain(input);

        DBResource db = new DBResource("");

    	StringBuffer sb = new StringBuffer();

    	db.allItems().stream().forEach(d -> sb.append(String.format("%sは%s円です。", d.get("Name").getS(), d.get("Price").getS())));

    	speechText = sb.toString();

        ResponseBuilder responseBuilder = input.getResponseBuilder();

        responseBuilder.withSimpleCard("ColorSession", speechText)
                .withSpeech(speechText)
                .withShouldEndSession(false);

        responseBuilder.withShouldEndSession(false)
                .withReprompt(speechText);

        return responseBuilder.build();
    }

}
