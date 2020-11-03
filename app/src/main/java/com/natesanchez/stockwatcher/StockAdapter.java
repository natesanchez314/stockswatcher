package com.natesanchez.stockwatcher;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder>{

  private List<Stock> stockList;
  private MainActivity mainAct;
  private String priceDown = "#A71D13";
  private String priceUp = "#28AA2D";

  StockAdapter(List<Stock> sList, MainActivity mAct) {
    this.stockList = sList;
    this.mainAct = mAct;
  }

  @NonNull
  @Override
  public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View stockView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_entry, parent, false);
    stockView.setOnClickListener(mainAct);
    stockView.setOnLongClickListener(mainAct);
    return new StockViewHolder(stockView);
  }

  @Override
  public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
    Stock stock = stockList.get(position);

    holder.symbol.setText(stock.getSymbol());
    holder.companyName.setText(stock.getName());
    holder.latestPrice.setText(String.format("%.2f", stock.getLatestPrice()));
    holder.change.setText(String.format("%.2f", stock.getChange()));
    holder.changePercent.setText(String.format("(%.2f%%)", stock.getChangePercent()));
    if (stock.getChangePercent() < 0) {
      holder.symbol.setTextColor(Color.parseColor(priceDown));
      holder.companyName.setTextColor(Color.parseColor(priceDown));
      holder.latestPrice.setTextColor(Color.parseColor(priceDown));
      holder.change.setTextColor(Color.parseColor(priceDown));
      holder.changePercent.setTextColor(Color.parseColor(priceDown));
      holder.arrow.setImageResource(R.drawable.arrow_drop_down_24px);
    } else {
      holder.symbol.setTextColor(Color.parseColor(priceUp));
      holder.companyName.setTextColor(Color.parseColor(priceUp));
      holder.latestPrice.setTextColor(Color.parseColor(priceUp));
      holder.change.setTextColor(Color.parseColor(priceUp));
      holder.changePercent.setTextColor(Color.parseColor(priceUp));
      holder.arrow.setImageResource(R.drawable.arrow_drop_up_24px);
    }
  }

  @Override
  public int getItemCount() {
    return stockList.size();
  }
}
