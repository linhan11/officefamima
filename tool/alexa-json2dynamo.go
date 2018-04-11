/*
* Alexa Skillの対話モデルスキーマから商品名と価格のリストを作成し
* DynamoDBに保存する
 */
package main

import (
	"encoding/json"
	"fmt"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/guregu/dynamo"
	"io/ioutil"
	"os"
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
					ID   string `json:"id"`
					Name struct {
						Value string `json:"value"`
					} `json:"name"`
				} `json:"values"`
			} `json:"types"`
		} `json:"languageModel"`
	} `json:"interactionModel"`
}

type Event struct {
	Name  string
	Price string
}

func main() {
	raw, err := ioutil.ReadFile("../speechAssets/SkillBuilder.json")
	if err != nil {
		fmt.Printf("open failure(%s)\n", err.Error())
		os.Exit(1)
	}
	var as AlexaSkill
	json.Unmarshal(raw, &as)

	db := dynamo.New(session.New(), &aws.Config{Region: aws.String("ap-northeast-1")})
	table := db.Table("OfficeFamima")

	//既にあるものは全削除
	var results []Event
	if err := table.Scan().All(&results); err != nil {
		fmt.Printf("get failure(%s)\n", err.Error())
	}
	for _, evt := range results {
		table.Delete("Name", evt.Name)
	}

	//新規に登録する
	for _, item := range as.InteractionModel.LanguageModel.Types[0].Values {
		evt := Event{Name: item.Name.Value, Price: item.ID}
		if err := table.Put(evt).Run(); err != nil {
			fmt.Printf("put failure(%s:%s) %s\n", item.Name.Value, item.ID, err.Error())
			os.Exit(1)
		}
	}
}
