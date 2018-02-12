package jp.motlof.cb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Balance {
	
	public static Balance balance = new Balance();
	
	private String time;
	private JsonObject btcjson, znyjson;
	
	private Balance() {try {
		reload();
	} catch (JsonSyntaxException | IOException e) {
		e.printStackTrace();
	}}
	
	public double getPrice(PriceParamType type) {
		double btc_jpy = btcjson.get("last_price").getAsDouble();
		double zny_jpy = znyjson.get(type.getParam()).getAsDouble();
		return (zny_jpy*btc_jpy);
	}
	
	public String getLoadTime() {return time;}
	
	public void reload() throws JsonSyntaxException, MalformedURLException, IOException {
		Gson json = new Gson();
		btcjson = json.fromJson(getJson(new URL("https://api.zaif.jp/api/1/last_price/btc_jpy")), JsonObject.class);
		znyjson = json.fromJson(getJson(new URL("https://c-cex.com/t/zny-btc.json")), JsonObject.class).get("ticker").getAsJsonObject();
		Calendar cal = Calendar.getInstance();
		time = new SimpleDateFormat("MM/dd HH:mm :").format(cal.getTime());
	}
	
	private static String getJson(URL url) throws IOException{
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		if(url.toString().contains("c-cex"))
			connection.setRequestProperty("user-agent", "ZNY Checker");
		connection.connect();
		
		if(HttpsURLConnection.HTTP_OK != connection.getResponseCode()) {
			throw new IOException(connection.getResponseCode()+" : "+connection.getResponseMessage());
		}
		
		return responceToString(connection.getInputStream());
	}
	
	private static String responceToString(InputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			String line = reader.readLine();
			while(line!=null) {
				builder.append(line);
				line = reader.readLine();
			}
			
			return builder.toString();
		}
	}
	
	enum PriceParamType {
		BUY("buy"), SELL("sell"), LAST("lastprice");
		
		private String type;
		
		private PriceParamType(String type) {
			this.type = type;
		}
		
		public String getParam() {
			return type;
		}
	}
}
