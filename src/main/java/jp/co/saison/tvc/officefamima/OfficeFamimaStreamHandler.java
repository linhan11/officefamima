package jp.co.saison.tvc.officefamima;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

import jp.co.saison.tvc.officefamima.handlers.CancelandStopIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.HelpIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.ItemIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.LaunchRequestHandler;
import jp.co.saison.tvc.officefamima.handlers.SessionEndedRequestHandler;
import jp.co.saison.tvc.officefamima.handlers.SummaryIntentHandler;

public class OfficeFamimaStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new ItemIntentHandler(),
                        new SummaryIntentHandler(),
                        new LaunchRequestHandler(),
                        new CancelandStopIntentHandler(),
                        new SessionEndedRequestHandler(),
                        new HelpIntentHandler())
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    public OfficeFamimaStreamHandler() {
        super(getSkill());
    }

}

