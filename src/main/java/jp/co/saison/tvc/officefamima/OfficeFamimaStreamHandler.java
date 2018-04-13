package jp.co.saison.tvc.officefamima;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

import jp.co.saison.tvc.officefamima.handlers.AskCartInfoIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.CancelandStopIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.CartIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.CheckIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.HelpIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.ItemIntentHandler;
import jp.co.saison.tvc.officefamima.handlers.LaunchRequestHandler;
import jp.co.saison.tvc.officefamima.handlers.SessionEndedRequestHandler;
import jp.co.saison.tvc.officefamima.handlers.SummaryIntentHandler;

public class OfficeFamimaStreamHandler extends SkillStreamHandler {

  private static Skill getSkill() {
    return Skills.standard().addRequestHandlers(new ItemIntentHandler(), // 商品の値段を答える
        new SummaryIntentHandler(), // 全商品の値段を答える
        new CartIntentHandler(), // カートに入れる
        new LaunchRequestHandler(), // 開始する
        new CancelandStopIntentHandler(), // 中止する
        new SessionEndedRequestHandler(), // ?
        new HelpIntentHandler(), // ヘルプを発話
        new AskCartInfoIntentHandler(), // カートに入っているものを答える
        new CheckIntentHandler())// 精算する
        // Add your skill id below
        // .withSkillId("")
        .build();
  }

  public OfficeFamimaStreamHandler() {
    super(getSkill());
  }

}

