/*
* Alexa Skillの対話モデルスキーマから商品名と価格のリストを作成する
 */
package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
)

/*
 https://mholt.github.io/json-to-go/
 に../speechAssets/SkillBuilder.jsonをドラッグして
 毎回新しい構造体を作成すること
*/
type AlexaSkill struct {
	InteractionModel struct {
		LanguageModel struct {
			InvocationName string `json:"invocationName"`
			Intents        []struct {
				Name    string        `json:"name"`
				Samples []interface{} `json:"samples"`
				Slots   []struct {
					Name string `json:"name"`
					Type string `json:"type"`
				} `json:"slots,omitempty"`
			} `json:"intents"`
			Types []struct {
				Name   string `json:"name"`
				Values []struct {
					Name struct {
						Value string `json:"value"`
					} `json:"name"`
				} `json:"values"`
			} `json:"types"`
		} `json:"languageModel"`
	} `json:"interactionModel"`
}

func main() {
	raw, err := ioutil.ReadFile("../speechAssets/SkillBuilder.json")
	if err != nil {
		fmt.Printf("open failure(%s)\n", err.Error())
	}
	var as AlexaSkill
	json.Unmarshal(raw, &as)

	for _, item := range as.InteractionModel.LanguageModel.Types[0].Values {
		fmt.Printf("map.put(\"%s\", \"%s\")\n", item.Name.Value, "１００")
	}
}
