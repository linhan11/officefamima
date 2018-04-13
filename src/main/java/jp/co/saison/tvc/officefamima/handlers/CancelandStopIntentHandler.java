package jp.co.saison.tvc.officefamima.handlers;

import static com.amazon.ask.request.Predicates.*;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

public class CancelandStopIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
    	/*
		Session.SessionContinue(input);

		DBResource db = new DBResource(Session.getCartKey(input));
		db.cleanupItem();
		*/

        return input.getResponseBuilder()
                .withSpeech("わかりました、ではよい一日を")
                .withSimpleCard("ColorSession", "Goodbye")
                .build();
    }
}
