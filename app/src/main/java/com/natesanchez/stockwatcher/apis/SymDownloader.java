package com.natesanchez.stockwatcher.apis;

import android.net.Uri;

import java.util.HashMap;

public class SymDownloader implements Runnable{

  final String STOCK_URL = "https://cloud.iexapis.com/stable/stock/rest/quote?token=pk_be772f392c24462ab1922fb2dffd69c8";
  public static HashMap<String, String> symMap = new HashMap<>();

  @Override
  public void run() {
    Uri dataUri = Uri.parse(STOCK_URL);
   // String

  }
}
