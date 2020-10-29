package com.natesanchez.stockwatcher;

public class StockLoaderRunnable implements Runnable {

  private static final String TAG = "CountryLoaderRunnable";
  private MainActivity mainActivity;
  private static final String DATA_URL = "";

  StockLoaderRunnable(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
  }

  @Override
  public void run() {

  }
}
