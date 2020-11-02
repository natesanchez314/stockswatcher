package com.natesanchez.stockwatcher.apis;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.natesanchez.stockwatcher.MainActivity;
import com.natesanchez.stockwatcher.R;
import com.natesanchez.stockwatcher.Stock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class StockDownloader implements Runnable {

    private MainActivity mainActivity;
    private String targetStock;
    private String STOCK_URL;
    private final String TOKEN = "pk_be772f392c24462ab1922fb2dffd69c8";

    public StockDownloader(MainActivity mainActivity, String targetStock){
        this.mainActivity = mainActivity;
        this.targetStock = targetStock;
        STOCK_URL = String.format("https://cloud.iexapis.com/stable/stock/%s/quote?token=%s",
                targetStock, TOKEN);
    }

    @Override
    public void run() {
        Uri.Builder ub = Uri.parse(STOCK_URL).buildUpon();
        //ub.appendQueryParameter("fullText", "true");
        String urlToUse = ub.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection uCon = (HttpURLConnection) url.openConnection();
            uCon.setRequestMethod("GET");
            uCon.connect();
            if (uCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("Stock Downloader", "run: HTTP ResponseCode NOT OK: " + uCon.getResponseCode());
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
            //JSONArray jArray = new JSONArray(s);
            //JSONObject jStock = (JSONObject) jArray.get(0);
            JSONObject jStock = new JSONObject(s);
            String symbol = jStock.getString("symbol");
            String companyName = jStock.getString("companyName");
            double latestPrice;
            if (!jStock.getString("latestPrice").equals("null")) {
                latestPrice = jStock.getDouble("latestPrice");
            } else {
                latestPrice = 0.0;
            }
            double change;
            if (!jStock.getString("change").equals("null")) {
                change = jStock.getDouble("change");
            } else {
                change = 0.0;
            }
            double changePercent;
            if (!jStock.getString("changePercent").equals("null")) {
                changePercent = jStock.getDouble("changePercent");
            } else {
                changePercent = 0.0;
            }
            final Stock stock = new Stock(
                    symbol,
                    companyName,
                    latestPrice,
                    change,
                    changePercent
            );
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.addStock(stock);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
