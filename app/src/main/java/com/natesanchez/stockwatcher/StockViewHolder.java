package com.natesanchez.stockwatcher;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder{

  TextView symbol;
  TextView companyName;
  TextView latestPrice;
  TextView change;
  TextView changePercent;
  ImageView arrow;

  public StockViewHolder(@NonNull View itemView) {
    super(itemView);

    symbol = itemView.findViewById(R.id.entry_symbol);
    companyName = itemView.findViewById(R.id.entry_name);
    latestPrice = itemView.findViewById(R.id.entry_latest_price);
    change = itemView.findViewById(R.id.entry_change);
    changePercent = itemView.findViewById(R.id.entry_change_percent);
    arrow = itemView.findViewById(R.id.arrow);
  }
}
