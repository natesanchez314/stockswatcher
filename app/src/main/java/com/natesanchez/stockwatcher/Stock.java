package com.natesanchez.stockwatcher;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

  private String symbol;
  private String name;
  private Date date;
  private boolean enabled;
  private String type;
  private int iexId;

  Stock(String s, String n, Long d, boolean e, String t, int i) {
    this.symbol = s;
    this.name = n;
    this.date = new Date(d);
    this.enabled = e;
    this.type = t;
    this.iexId = i;
  }

  public String getSymbol() { return symbol; }

  public String getName() { return name; }

  public Date getDate() { return date; }

  public boolean isEnabled() { return enabled; }

  public String getType() { return type; }

  public int getIexId() { return iexId; }
}
