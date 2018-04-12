package jp.co.saison.tvc.officefamima.handlers;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class DBResource {
	AmazonDynamoDB client;
	DynamoDB dynamoDB;
	Random random;
	String cartkey;

	
	//リファクタリングの際にコンストラクタでDBをオープンしMAPを作成し閉じることろまでやる
	//メソッドはすべてMAPに対してクエリを行う
	
	public DBResource() {
		client = AmazonDynamoDBClientBuilder.standard()
 			.withRegion(Regions.AP_NORTHEAST_1)
 			.build();
		dynamoDB = new DynamoDB(client);
		//ここでcartkeyを払い出しはしない。今は実験用にコーディングされている
		random = new Random();
		cartkey = String.format("ID%05d", random.nextInt(65535));
	}

	public boolean existItem(String name) {
		if (name == null) {
			return false;
		}

    	ScanRequest scanRequest = new ScanRequest()
    		    .withTableName("OfficeFamima");

    	ScanResult result = client.scan(scanRequest);

    	for (Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> item : result.getItems()) {
			if (name.equals(item.get("Name").getS())) {
				return true;
			}
    	}

    	return false;
	}

	public String getPrice(String name) {
    	ScanRequest scanRequest = new ScanRequest()
    		    .withTableName("OfficeFamima");

    	ScanResult result = client.scan(scanRequest);

    	for (Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> item : result.getItems()) {
			if (name.equals(item.get("Name").getS())) {
				return item.get("Price").getS();
			}
    	}

    	return "";
	}

	//Java的にはDBのクラスがあって、クエリ用とカート用は継承してクラスを作るのがよいと思われる
	
	public boolean addItemCart(String name) {
		return addItemCart(name, "1");
	}

	public boolean addItemCart(String name, String quantity) {
		try {
	  		Table table = dynamoDB.getTable("OfficeFamimaCart");
				Item item = new Item()
						.withString("ID", UUID.randomUUID().toString()) // 毎回振る
						.withString("SID", cartkey)
						.withString("Name", name)
						.withNumber("Quantity", Integer.parseInt(quantity));
				table.putItem(item);
				
				return true;
	      } catch (Exception e) {
			System.err.println("Failed to add new attribute in " + name);
			System.err.println(e.getStackTrace());
	      }
	      return false;
	}
}
