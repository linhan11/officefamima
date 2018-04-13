package jp.co.saison.tvc.officefamima.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class DBResource {
  AmazonDynamoDB client;
  DynamoDB dynamoDB;
  String cartkey;
  private static final String ITEM_TABLE = "OfficeFamima";
  private static final String CART_TABLE = "OfficeFamimaCart";
  List<Map<String, AttributeValue>> cart_scan;

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
   * 現在カートに入っている商品の一覧を結合して返却する
   * 
   * @param sep 結合文字列
   * @return
   */
  public String CartItemStringJoin(String sep) {

    return scanTable(CART_TABLE).stream().filter(s -> cartkey.equals(s.get("SID").getS()))
        .map(s -> s.get("Name").getS()).collect(Collectors.joining(sep));
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

<<<<<<< HEAD
  public void cleanupCartItem() {
=======
  private List<Map<String, AttributeValue>> getCartItems() {
	  List<Map<String, AttributeValue>> lists = new ArrayList<>();
	  scanTable(CART_TABLE).stream().filter(s -> cartkey.equals(s.get("SID").getS())).forEach(s -> lists.add(s));
	  return lists;
  }
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
    Table table = dynamoDB.getTable(CART_TABLE);
    scanTable(CART_TABLE).stream().filter(s -> cartkey.equals(s.get("SID").getS()))
        .forEach(s -> table.deleteItem("ID", s.get("ID").getS().toString()));
    /*
     * for (Map<String, AttributeValue> item : getCartItems()) { table.deleteItem("ID",
     * item.get("ID").getS().toString()); }
     */
=======
  public void cleanupCartItem() {
	  Table table = dynamoDB.getTable(CART_TABLE);
	  for (Map<String, AttributeValue> item : getCartItems()) {
		  table.deleteItem("ID", item.get("ID").getS().toString());
	  }
>>>>>>> refs/remotes/origin/master
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
