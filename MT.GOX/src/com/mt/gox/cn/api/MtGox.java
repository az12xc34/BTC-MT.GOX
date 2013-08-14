/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mt.gox.cn.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import com.mt.gox.cn.model.Order;
import com.mt.gox.cn.model.Trade;

/**
 * @ClassName: MtGox
 * @Description:
 * @author LEIKANG
 */
public class MtGox implements TradeInterface {

	public enum Currency {
		BTC, USD, GBP, EUR, JPY, AUD, CAD, CHF, CNY, DKK, HKD, PLN, RUB, SEK, SGD, THB
	};

	private ApiKeys keys;

	private final HashMap<Currency, Integer> devisionFactors;

	private final double MIN_ORDER = 0.01; // BTC

	private final String API_BASE_URL = "https://data.mtgox.com/api/2/";
	
	private final String API_GET_INFO = "MONEY/INFO";

	//btc取现
	private final String API_WITHDRAW = "MONEY/BITCOIN/SEND_SIMPLE";
	//
	private final String API_LAG = "MONEY/ORDER/LAG";
	//添加委单
	private final String API_ADD_ORDER = "BTCUSD/MONEY/ORDER/ADD";
	//获取交易记录
	private final String API_TRADES_FETCH = "BTCUSD/MONEY/TRADES/FETCH";
	//取消订单
	private final String API_ORDER_CANCEL = "BTCUSD/MONEY/ORDER/CANCEL";
	//获取个人订单
	private final String API_GET_ORDERS = "BTCUSD/MONEY/ORDERS";
	//市场委单深度
	private final String API_DEPTH_FETCH = "BTCUSD/MONEY/DEPTH/FETCH";

	private final String SIGN_HASH_FUNCTION = "HmacSHA512";
	
	private final String ENCODING = "UTF-8";

	private boolean printHttpResponse;

	public MtGox(ApiKeys keys) {
		this.keys = keys;
		printHttpResponse = false;
		// set division Factors
		devisionFactors = new HashMap<Currency, Integer>();
		devisionFactors.put(Currency.BTC, 100000000);
		devisionFactors.put(Currency.USD, 100000);
		devisionFactors.put(Currency.GBP, 100000);
		devisionFactors.put(Currency.EUR, 100000);
		devisionFactors.put(Currency.JPY, 1000);
		devisionFactors.put(Currency.AUD, 100000);
		devisionFactors.put(Currency.CAD, 100000);
		devisionFactors.put(Currency.CHF, 100000);
		devisionFactors.put(Currency.CNY, 100000);
		devisionFactors.put(Currency.DKK, 100000);
		devisionFactors.put(Currency.HKD, 100000);
		devisionFactors.put(Currency.PLN, 100000);
		devisionFactors.put(Currency.RUB, 100000);
		devisionFactors.put(Currency.SEK, 1000);
		devisionFactors.put(Currency.SGD, 100000);
		devisionFactors.put(Currency.THB, 100000);

	}

	public void setPrintHTTPResponse(boolean resp) {
		this.printHttpResponse = resp;
	}

	@Override
	public String getLag() {
		String urlPath = API_LAG;
		HashMap<String, String> query_args = new HashMap<String, String>();
		String queryResult = query(urlPath, query_args);
		JSONTokener parser = new JSONTokener(queryResult);
		String lag = "";
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());
			JSONObject dataJson = (JSONObject) httpAnswerJson.get("data");

			lag = (String) dataJson.get("lag_text");
		} catch (Exception ex) {
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}
		return lag;
	}

	@Override
	public String withdrawBTC(double amount, String dest_address) {  
		String urlPath = API_WITHDRAW;
		HashMap<String, String> query_args = new HashMap<String, String>();
		/*
		 * Params address : Target bitcoin address amount_int : Amount of
		 * bitcoins to withdraw fee_int : Fee amount to be added to transaction
		 * (optional), maximum 0.01 BTC no_instant : Setting this parameter to 1
		 * will prevent transaction from being processed internally, and force
		 * usage of the bitcoin blockchain even if receipient is also on the
		 * system green : Setting this parameter to 1 will cause the TX to use
		 * MtGox鈥檚 green address
		 */
		query_args.put(
				"amount_int",
				Long.toString(Math.round(amount
						* devisionFactors.get(Currency.BTC))));
		query_args.put("address", dest_address);
		String queryResult = query(urlPath, query_args);

		/*
		 * Sample result On success, this method will return the transaction id
		 * (in offser trx ) which will contain either the bitcoin transaction id
		 * as hexadecimal or a UUID value in case of internal transfer.
		 */

		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject obj2 = (JSONObject) (parser.nextValue());
			// JSONObject data = (JSONObject)obj2.get("data");  

		} catch (Exception ex) {
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}

		return ""; //  Edit
	}

	@Override
	public String sellBTC(double amount, double price) {
		return placeOrder("sell",
				Math.round(amount * devisionFactors.get(Currency.BTC)), price);
	}

	@Override
	public String buyBTC(double amount, double price) {
		return placeOrder("buy",
				Math.round(amount * devisionFactors.get(Currency.BTC)), price);
	}

	public String placeOrder(String type, long amount_int, double price) {

		price = price * 100000;

		String toReturn = "";
		String result = "";
		String data = "";
		String urlPath = API_ADD_ORDER;
		HashMap<String, String> query_args = new HashMap<String, String>();
		/*
		 * Params type : {ask (sell) | bid(buy) } amount_int : amount of BTC to
		 * buy or sell, as an integer price_int : The price per bitcoin in the
		 * auxiliary currency, as an integer, optional if you wish to trade at
		 * the market price
		 */
		query_args.put("amount_int", Long.toString(amount_int));
		if (type.equals("sell"))
			query_args.put("type", "ask");
		else
			query_args.put("type", "bid");
		// "8265000"
		query_args.put("price_int", price + "");

		String queryResult = query(urlPath, query_args);
		/*
		 * Sample result {"result":"success","data":"abc123-def45-.."}
		 */
		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject obj2 = (JSONObject) (parser.nextValue());
			result = (String) obj2.get("result");
			data = (String) obj2.get("data");
			// lastPriceArray[0] = (Double)obj2.get("last"); //USD
		} catch (Exception ex) {
			System.out.println("=====placeOrder:"+ queryResult);
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}

		if (result.equals("success")) {
			toReturn = "executed : " + data;
		} else
			toReturn = "not executed : " + data; // test this branch

		return toReturn; // change
	}

	@Override
	public double[] getBalance() {
		String urlPath = API_GET_INFO;
		HashMap<String, String> query_args = new HashMap<String, String>();

		/*
		 * Params
		 */
		double[] balanceArray = new double[3];

		String queryResult = query(urlPath, query_args);
		/*
		 * Sample result { "data": { "Created": "yyyy-mm-dd hh:mm:ss", "Id":
		 * "abc123", "Index": "123", "Language": "en_US", "Last_Login":
		 * "yyyy-mm-dd hh:mm:ss", "Login": "username", "Monthly_Volume":
		 * **Currency Object**, "Trade_Fee": 0.6, "Rights": ['deposit',
		 * 'get_info', 'merchant', 'trade', 'withdraw'], "Wallets": { "BTC": {
		 * "Balance": **Currency Object**, "Daily_Withdraw_Limit": **Currency
		 * Object**, "Max_Withdraw": **Currency Object**,
		 * "Monthly_Withdraw_Limit": null, "Open_Orders": **Currency Object**,
		 * "Operations": 1, }, "USD": { "Balance": **Currency Object**,
		 * "Daily_Withdraw_Limit": **Currency Object**, "Max_Withdraw":
		 * **Currency Object**, "Monthly_Withdraw_Limit": **Currency Object**,
		 * "Open_Orders": **Currency Object**, "Operations": 0, }, "JPY":{...},
		 * "EUR":{...}, // etc, depends what wallets you have }, }, "result":
		 * "success" }
		 */

		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());

			JSONObject dataJson = (JSONObject) httpAnswerJson.get("data");
			JSONObject walletsJson = (JSONObject) dataJson.get("Wallets");

			JSONObject BTCwalletJson = (JSONObject) ((JSONObject) walletsJson
					.get("BTC")).get("Balance");

			String BTCBalance = (String) BTCwalletJson.get("value");

			boolean hasDollars = true;
			boolean hasEuros = true;
			JSONObject USDwalletJson, EURwalletJson;
			String USDBalance = "", EURBalance = "";

			try {
				USDwalletJson = (JSONObject) ((JSONObject) walletsJson
						.get("USD")).get("Balance");
				USDBalance = (String) USDwalletJson.get("value");
			} catch (Exception e) {
				hasDollars = false;
			}

			try {
				EURwalletJson = (JSONObject) ((JSONObject) walletsJson
						.get("EUR")).get("Balance");
				EURBalance = (String) EURwalletJson.get("value");
			} catch (Exception e) {
				hasEuros = false;
			}

			balanceArray[0] = Double.parseDouble(BTCBalance); // BTC

			if (hasDollars)
				balanceArray[1] = Double.parseDouble(USDBalance); // USD
			else
				balanceArray[1] = -1; // Account does not have USD wallet

			if (hasEuros)
				balanceArray[2] = Double.parseDouble(EURBalance); // EUR
			else
				balanceArray[2] = -1; // Account does not have EUR wallet

		} catch (Exception ex) {
			System.out.println("=====getBalance:"+ queryResult);
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}

		return balanceArray;
	}

	public String query(String path, HashMap<String, String> args) {
		GoxService query = new GoxService(path, args, keys);
		String queryResult = query.executeQuery(true);
		return queryResult;
		// should be done by a different thread ...
	}
	
	public String queryWithGetMethod(String path, HashMap<String, String> args) {
		GoxService query = new GoxService(path, args, keys);
		String queryResult = query.executeQuery(false);
		return queryResult;
	}

	/**
	 * 获取当前价格
	 */
	public double getLastPrice(Currency cur) {

		String urlPath = getTickerPath(cur, true);
		long divideFactor = devisionFactors.get(cur);
		HashMap<String, String> query_args = new HashMap<String, String>();
		String queryResult = query(urlPath, query_args);

		/*
		 * Result sample :{ "result":"success", "data": { "high": **Currency
		 * Object - USD**, "low": **Currency Object - USD**, "avg": **Currency
		 * Object - USD**, "vwap": **Currency Object - USD**, "vol": **Currency
		 * Object - BTC**, "last_local": **Currency Object - USD**, "last_orig":
		 * **Currency Object - ???**, "last_all": **Currency Object - USD**,
		 * "last": **Currency Object - USD**, "buy": **Currency Object - USD**,
		 * "sell": **Currency Object - USD**, "now": "1364689759572564" }}
		 */
		JSONTokener parser = new JSONTokener(queryResult);
		double last = 0;
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());

			JSONObject dataJson = (JSONObject) httpAnswerJson.get("data");
			JSONObject lastJson = (JSONObject) dataJson.get("last");
			String last_String = (String) lastJson.get("value");
			last = Double.parseDouble(last_String);

		} catch (Exception ex) {
			System.out.println("=====getLastPrice:"+ queryResult);
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}
		return last;
	}

	@SuppressLint("NewApi")
	private class GoxService {
		protected String path;
		protected HashMap args;
		protected ApiKeys keys;

		public GoxService(String path, HashMap<String, String> args,
				ApiKeys keys) {
			this.path = path;
			this.args = args;
			this.keys = keys;
		}

		// Build the query string given a set of query parameters
		private String buildQueryString(HashMap<String, String> args) {
			String result = new String();
			for (String hashkey : args.keySet()) {
				if (result.length() > 0)
					result += '&';
				try {
					result += URLEncoder.encode(hashkey, ENCODING) + "="
							+ URLEncoder.encode(args.get(hashkey), ENCODING);
				} catch (Exception ex) {
					Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			}
			return result;
		}

		private String signRequest(String secret, String hash_data) {
			String signature = "";
			try {
				Mac mac = Mac.getInstance(SIGN_HASH_FUNCTION);

				SecretKeySpec secret_spec = new SecretKeySpec(Base64.decode(
						secret, Base64.DEFAULT), SIGN_HASH_FUNCTION);
				 //SecretKeySpec secret_spec = new
				 //SecretKeySpec(secret.getBytes(), SIGN_HASH_FUNCTION);
				mac.init(secret_spec);
				signature = Base64.encodeToString(
						mac.doFinal(hash_data.getBytes()), Base64.DEFAULT);
				// signature = mac.doFinal(hash_data.getBytes()).toString();
			} catch (Exception e) {
				Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null,
						e);
			}
			return signature;
		}

		private String executeQuery(boolean isPostMethod) {
			String answer = "";
			boolean httpError = false;
			HttpsURLConnection connection = null;
			String nonce = String.valueOf(System.nanoTime() * 1000);
			try {
				// add nonce and build arg list
				args.put("nonce", nonce);
				String post_data = buildQueryString(args);
				String hash_data = path + "\0" + post_data; // Should be correct
				
				// args signature with apache cryptografic tools
				String signature = signRequest(keys.getPrivateKey(), hash_data);
				// build URL
				URL queryUrl = new URL(API_BASE_URL + path);
				// create and setup a HTTP connection
				connection = (HttpsURLConnection) queryUrl.openConnection();
				connection.setRequestMethod(isPostMethod?"POST":"GET");
				// connection.setRequestProperty("User-Agent",
				// "Advanced-java-client API v2");
				connection
						.setRequestProperty(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0");
				connection.setRequestProperty("Rest-Key", keys.getApiKey());
				connection.setRequestProperty("Rest-Sign",
						signature.replaceAll("\n", ""));
				connection.setDoOutput(true);
				connection.setDoInput(true);
				// Read the response
				DataOutputStream os = new DataOutputStream(
						connection.getOutputStream());
				os.writeBytes(post_data);
				os.close();

				BufferedReader br = null;
				if (connection.getResponseCode() >= 400) {
					httpError = true;//if HTTP error, do something else
										// with output!
					br = new BufferedReader(new InputStreamReader(
							(connection.getErrorStream())));
				} else
					br = new BufferedReader(new InputStreamReader(
							(connection.getInputStream())));
				String output;
				if (httpError)
					System.err.println("Post Data: " + post_data);
				if (printHttpResponse)
					System.out.println("Query to :" + path
							+ " , HTTP response : \n"); // do not log unless is
														// error > 400
				while ((output = br.readLine()) != null) {
					if (printHttpResponse)
						System.out.println(output);
					answer += output;
				}
			}
			// Capture Exceptions
			catch (IllegalStateException ex) {
				System.err.println(ex);
			} catch (IOException ex) {
				System.err.println(ex);
			} finally {
				// close the connection, set all objects to null
				if (connection != null)
					connection.disconnect();
				connection = null;
			}
			return answer;
		}
	}

	/**
	 * returns the string used to get the Ticker
	 * @return the string you're searching for;)
	 */
	private String getTickerPath(Currency cur, boolean fast) {
		return "BTC" + cur.toString() + "/MONEY/TICKER" + (fast ? "_FAST" : "");
	}

	/**
	 * @Title: getOpenOrders 
	 * @Description: 获取个人委单 
	 * @param @return    设定文件 
	 * @return List<Order>    返回类型 
	 * @throws
	 */
	public List<Order> getOpenOrders() {
		List<Order> orders = new ArrayList<Order>();
		String urlPath = API_GET_ORDERS;
		HashMap<String, String> formData = new HashMap<String, String>();
		String queryResult = query(urlPath, formData);
		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());
			JSONArray dataJson = (JSONArray) httpAnswerJson.get("data");
			for (int i = 0; i < dataJson.length(); i++) {
				JSONObject order = (JSONObject) dataJson.get(i);

				Order item = new Order();
				item.oid = order.getString("oid");
				item.amount = order.getJSONObject("amount").getDouble("value");
				item.price = order.getJSONObject("price").getDouble("value");
				item.type = order.getString("type");
				item.data = order.getLong("date");
				item.status = order.getString("status");
				orders.add(item);
			}

		} catch (Exception e) {
			System.out.println("=====getOpenOrders:"+ queryResult);
			e.printStackTrace();
		}

		return orders;
	}

	/**
	 * @Title: getQuote 
	 * @Description: 获取市场委单 
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public List<Order>[] getQuote() {
		// Quote bidq = mg.getQuote("bid");
		// Quote askq = mg.getQuote("ask");
		// String type = "bid";
		// private/orders
		String urlPath = API_DEPTH_FETCH;
		
		List<Order>[] list = new ArrayList[2];
		
		List<Order> buyorders = new ArrayList<Order>();
		List<Order> sellorders = new ArrayList<Order>();

		HashMap<String, String> formData = new HashMap<String, String>();

		String queryResult = query(urlPath, formData);

		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());
			JSONObject dataJson = (JSONObject) httpAnswerJson.get("data");
			JSONArray askJson = (JSONArray) dataJson.get("asks");
			JSONArray bidJson = (JSONArray) dataJson.get("bids");
			// e: {"amount":2,"stamp":"1373965913493990","price":96.8,"price_int":"9680000","amount_int":"200000000"}
			for (int i = 0; i < 300; i++) {
				JSONObject order = (JSONObject) askJson.get(i);
				
				Order item = new Order();
				item.amount = order.getDouble("amount");
				item.price = order.getDouble("price");
				item.type = "ask";
 
				sellorders.add(item);
			}

			for (int i = bidJson.length()-1; i > 0; i--) {
				JSONObject order = (JSONObject) bidJson.get(i);
				
				Order item = new Order();
				item.amount = order.getDouble("amount");
				item.price = order.getDouble("price");
				item.type = "bid";
				buyorders.add(item);
			}
			
			list[0] = buyorders;
			list[1] = sellorders;

			return list;

		} catch (Exception ex) {
			System.out.println("=====getQuote:"+ queryResult);
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * @Title: cancelOrder 
	 * @Description: 取消订单
	 * @param @param oid
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public String cancelOrder(String oid) {
		String urlPath = API_ORDER_CANCEL;

		HashMap<String, String> formData = new HashMap<String, String>();
		
		formData.put("oid", oid);
		String queryResult = query(urlPath, formData);
		
		return queryResult;
	}
	
	/**
	 * @Title: getTradesList 
	 * @Description: 获取交易历史记录
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public List<Trade> getTradesList() {
		String urlPath = API_TRADES_FETCH;
		HashMap<String, String> formData = new HashMap<String, String>();
		
		// {"date":1374569186,"price":"93.23999","amount":"0.01","price_int":"9323999","amount_int":"1000000",
		//"tid":"1374569186526635","price_currency":"USD","item":
		//	"BTC","trade_type":"bid","primary":"N","properties":"market,mixed_currency"}
		
		List<Trade> trades = new ArrayList<Trade>();
		
		String queryResult = queryWithGetMethod(urlPath, formData);
		
		JSONTokener parser = new JSONTokener(queryResult);
		try {
			JSONObject httpAnswerJson = (JSONObject) (parser.nextValue());
			JSONArray dataJson = (JSONArray) httpAnswerJson.get("data");
			for (int i = dataJson.length() -1 ; i > 0; i--) {
				JSONObject item = (JSONObject) dataJson.get(i);
				Trade trade = new Trade();
				trade.date = item.getLong("date");
				trade.price = item.getDouble("price");
				trade.amount = item.getDouble("amount");
				trade.trade_type = item.getString("trade_type");
				trades.add(trade);
			}
			
		}catch (Exception ex) {
			System.out.println("=====getTradesList:"+ queryResult);
			Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return trades;
	}

}