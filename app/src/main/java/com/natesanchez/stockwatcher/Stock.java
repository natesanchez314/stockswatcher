package com.natesanchez.stockwatcher;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Stock implements Serializable, Comparable<Stock> {

  private String symbol;
  private String companyName;
  private double latestPrice;
  private double change;
  private double changePercent;

  public Stock(String sym, String name, double price, double change, double changePercent) {
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

  public void setLatestPrice(double d) { this.latestPrice = d; }

  public void setChange(double d) { this.change = d; }

  public void setChangePercent(double d) { this.change = d; }

  @Override
  public int compareTo(Stock stock) {
    return symbol.compareTo(stock.getSymbol());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stock stock = (Stock) o;
    return symbol.equals(stock.symbol) &&
            companyName.equals(stock.companyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, companyName);
  }
}
