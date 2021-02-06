package com.natesanchez.stockwatcher

import java.io.Serializable
import java.util.*

class Stock(symbol: String, name: String, price: Double, change: Double, changePercent: Double)
  : Serializable, Comparable<Stock> {

  val symbol: String = symbol
  val name: String = name
  var latestPrice: Double = price
  var change: Double = change
  var changePercent: Double = changePercent

  override fun compareTo(other: Stock): Int {
    return symbol.compareTo(other.symbol)
  }

  override fun equals(other: Any?): Boolean {
    if (this == other) return true
    if (other == null || !(other is Stock)) return false
    var stock: Stock = other as Stock
    return symbol.equals(stock.symbol)
  }

  override fun hashCode(): Int {
    return Objects.hash(symbol, name)
  }
}