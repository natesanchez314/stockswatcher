package com.natesanchez.stockwatcher;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

  private String symbol;
  private String companyName;
  private double latestPrice;
  private double change;
  private double changePercent;

  Stock(String sym, String name, double price, double change, double changePercent) {
    this.symbol = sym;
    this.companyName = name;
    this.latestPrice = price;
    this.change = change;
    this.changePercent = changePercent;
  }

  public String getSymbol() { return symbol; }

  public String getName() { return companyName; }

  public double getLatestPrice() { return latestPrice; }

  public double getChange() { return change; }

  public double getChangePercent() { return changePercent; }
}
