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
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

public class LaunchRequestHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
		Session.maintain(input);
		String speechText = "オフィスファミマにようこそ、全商品の値段と個々の値段を聞くことができます";
		String repromptText = "Please tell me your favorite color by saying, my favorite color is red";
		return input.getResponseBuilder()
				.withSimpleCard("ColorSession", speechText)
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.build();
    }
}
