package com.natesanchez.stockwatcher.apis;

import android.net.Uri;

import com.natesanchez.stockwatcher.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;

public class SymDownloader implements Runnable{

  public static HashMap<String, String> symMap = new HashMap<>();
  final String SOURCE_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

  @Override
  public void run() {
    Uri dataUri = Uri.parse(SOURCE_URL);
    String urlToUse = dataUri.toString();

    StringBuilder sb = new StringBuilder();
    try {
      URL url = new URL(urlToUse);
      HttpsURLConnection uCon = (HttpsURLConnection) url.openConnection();
      uCon.setRequestMethod("GET");
      uCon.connect();
      if (uCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
        return;
      }
      InputStream is = uCon.getInputStream();
      BufferedReader br = new BufferedReader((new InputStreamReader(is)));
      String line;
      while((line = br.readLine()) != null) {
        sb.append(line).append('\n');
      }
    } catch (Exception e) {
      return;
    }
    process(sb.toString());
  }

  private void process(String s) {
    try {
      JSONArray jObjMain = new JSONArray(s);
      for (int i = 0; i <jObjMain.length(); i++) {
        JSONObject jStock = (JSONObject) jObjMain.get(i);
        String symbol = jStock.getString("symbol");
        String companyName   = jStock.getString("name");
        symMap.put(symbol, companyName);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<String> findStocks(String s) {
    String stockToMatch = s.toUpperCase().trim();
    HashSet<String> matchSet = new HashSet<>();
    for (String sym : symMap.keySet()) {
      if (sym.toUpperCase().trim().contains(stockToMatch)) {
        matchSet.add(String.format("%s - %s", sym, symMap.get(sym)));
      }
    }
    ArrayList<String> results = new ArrayList<>(matchSet);
    Collections.sort(results);
    return results;
  }
}
