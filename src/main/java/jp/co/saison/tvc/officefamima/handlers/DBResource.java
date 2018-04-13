package jp.co.saison.tvc.officefamima.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.Attribute;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

public class DBResource {
  AmazonDynamoDB client;
  DynamoDB dynamoDB;
  String cartkey;
  private static final String ITEM_TABLE = "OfficeFamima";
  private static final String CART_TABLE = "OfficeFamimaCart";

  /**
   * 東京リージョンのDynamoにアクセスする準備を行う
   * 
   * @param cartkey
   */
  public DBResource(String cartkey) {
    client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build();
    dynamoDB = new DynamoDB(client);
    this.cartkey = cartkey;
  }

  /**
   * 商品テーブルに指定された商品があるか検索する
   * 
   * @param name
   * @return
   */
  public boolean existItem(String name) {
    if (name == null) {
      return false;
    }
    ScanRequest scanRequest = new ScanRequest().withTableName(ITEM_TABLE);
    ScanResult result = client.scan(scanRequest);

    return result.getItems().stream().filter(s -> name.equals(s.get("Name").getS())).count() == 0
        ? false
        : true;
  }

  /**
   * 指定された商品の値段を返却する
   * 
   * @param name
   * @return
   */
  public String getPrice(String name) {
    ScanRequest scanRequest = new ScanRequest().withTableName(ITEM_TABLE);
    ScanResult result = client.scan(scanRequest);

    return result.getItems().stream().filter(s -> name.equals(s.get("Name").getS())).findFirst()
        .get().get("Price").getS();
  }

  /**
   * 商品テーブルの全リストを返却する
   * 
   * @return
   */
  public List<Map<String, AttributeValue>> scanForItemTable() {
    return scanTable(ITEM_TABLE);
  }

  /**
   * カートテーブルから今回のセッションのリストをmapで返却する
   * 
   * @return map<商品名, 数量>
   */
  public Map<String, String> scanForCartTable() {
    Map<String, String> map = new HashMap<>();

    scanTable(CART_TABLE).stream().filter(s -> cartkey.equals(s.get("SID").getS()))
        .forEach(s -> map.put(s.get("Name").getS(), s.get("Quantity").getN()));
    return map;
  }

  /**
   * 指定されたテーブルの全リストを返却する
   * 
   * @param table
   * @return
   */
  private List<Map<String, AttributeValue>> scanTable(String table) {
    ScanRequest scanRequest = new ScanRequest().withTableName(table);
    ScanResult result = client.scan(scanRequest);
    return result.getItems();
  }

  public void cleanupItem() {
    Map<String, List<WriteRequest>> map = new HashMap<String, List<WriteRequest>>();

    DeleteRequest delRequest = new DeleteRequest();
    delRequest.addKeyEntry("SID", new AttributeValue().withS(cartkey));

    WriteRequest writeReq = new WriteRequest(delRequest);
    List<WriteRequest> listwritereq = new ArrayList<>();
    listwritereq.add(writeReq);

    map.put(CART_TABLE, listwritereq);

    BatchWriteItemRequest request = new BatchWriteItemRequest();
    request.setRequestItems(map);

    client.batchWriteItem(request);
  }

  /**
   * 商品をカートに一つ追加する
   * 
   * @param name
   * @return
   */
  public boolean addItemCart(String name) {
    return addItemCart(name, "1");
  }

  /**
   * 商品をカートに指定された数追加する
   * 
   * @param name
   * @param quantity
   * @return
   */
  public boolean addItemCart(String name, String quantity) {
    try {
      Table table = dynamoDB.getTable(CART_TABLE);
      Item item = new Item().withString("ID", UUID.randomUUID().toString()) // 毎回振る
          .withString("SID", cartkey).withString("Name", name)
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
