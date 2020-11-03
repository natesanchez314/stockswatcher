package com.natesanchez.stockwatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.natesanchez.stockwatcher.apis.StockDownloader;
import com.natesanchez.stockwatcher.apis.SymDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, View.OnLongClickListener{

  private SwipeRefreshLayout swiper;
  private RecyclerView recyclerView;
  private StockAdapter stockAdapter;
  private final List<Stock> stockList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ArrayList<Stock> tempList = readJSON();
    // swiper set up
    swiper = findViewById(R.id.swiper);
    swiper.setOnRefreshListener(this::doRefresh);
    // recycler view setup
    recyclerView = findViewById(R.id.stockRecycler);
    stockAdapter = new StockAdapter(stockList, this);
    recyclerView.setAdapter(stockAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    if (!checkNetworkConnection()) {
      notConnectedToTheInternet();
      for (Stock s : tempList) {
        s.setLatestPrice(0);
        s.setChange(0);
        s.setChangePercent(0);
      }
    } else {
      SymDownloader sd = new SymDownloader();
      new Thread(sd).start();
      for (Stock s : tempList) {
        doSelection(s.getSymbol());
      }
    }
    Collections.sort(stockList);
    stockAdapter.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public void onClick(View view) {
    Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onLongClick(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Delete stock");
    builder.setMessage("Are you sure you want to delete this stock?");
    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
      stockList.remove(recyclerView.getChildLayoutPosition(view));
      Toast.makeText(getApplicationContext(),"Stock deleted",Toast.LENGTH_SHORT).show();
      writeJSON();
      stockAdapter.notifyDataSetChanged();
    });
    builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(getApplicationContext(),"Cancelled deletion",Toast.LENGTH_SHORT).show());
    AlertDialog dialog = builder.create();
    dialog.show();
    writeJSON();
    return false;
  }

  private void writeJSON() {
    try {
      FileOutputStream fos = getApplicationContext().
              openFileOutput(getString(R.string.json_stocks_file), Context.MODE_PRIVATE);
      JsonWriter jw = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
      jw.setIndent("  ");
      jw.beginArray();
      for (Stock s : stockList) {
        // save stock object into json object
        jw.beginObject();
        jw.name("symbol").value(s.getSymbol());
        jw.name("companyName").value(s.getName());
        jw.name("latestPrice").value(s.getLatestPrice());
        jw.name("change").value(s.getChange());
        jw.name("changePercent").value(s.getChangePercent());
        jw.endObject();
      }
      jw.endArray();
      jw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ArrayList<Stock> readJSON() {
    ArrayList<Stock> tempList = new ArrayList<>();
    try {
      FileInputStream fis = getApplicationContext().
              openFileInput(getString(R.string.json_stocks_file));
      byte[] data = new byte[fis.available()];
      int loaded = fis.read(data);
      fis.close();
      String json = new String(data);

      JSONArray notesArray = new JSONArray(json);
      for (int i = 0; i < notesArray.length(); i++) {
        JSONObject jObj = notesArray.getJSONObject(i);
        Stock s = new Stock(
                jObj.getString("symbol"),
                jObj.getString("companyName"),
                jObj.getDouble("latestPrice"),
                jObj.getDouble("change"),
                jObj.getDouble("changePercent")
        );
        tempList.add(s);
      }
      //stockAdapter.notifyDataSetChanged();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tempList;
  }

  private void doRefresh() {
    if (!checkNetworkConnection()) {
      notConnectedToTheInternet();
    } else {
      updateStocks();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != R.id.addStockButton) {
      Toast.makeText(this, "error adding", Toast.LENGTH_SHORT).show();
      return false;
    } else {
      if (!checkNetworkConnection()) {
        notConnectedToTheInternet();
        return false;
      } else {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol");
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(et);
        builder.setPositiveButton("OK", (dialogInterface, i) -> makeStockDialog(et.getText().toString().trim()));
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "No stock added", Toast.LENGTH_SHORT).show());
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
      }
    }
  }

  public void doSelection(String stock) {
    String[] sym = stock.split("-");
    StockDownloader stockDownloader = new StockDownloader(this, sym[0].trim());
    new Thread(stockDownloader).start();
  }

  public void makeStockDialog(String sym) {
    final ArrayList<String> results = SymDownloader.findStocks(sym);
    if (results.size() == 0) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Symbol not found: sym");
      builder.setMessage("Unable to find a stock with the given symbol");
      AlertDialog dialog = builder.create();
      dialog.show();
    } else if (results.size() == 1) {
      doSelection(results.get(0));
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      String[] array = results.toArray(new String[0]);
      builder.setTitle("Make a Selection");
      builder.setItems(array, (dialogInterface, i) -> {
        String symbol = results.get(i);
        doSelection(symbol);
      });
      builder.setNegativeButton("Nevermind", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "No stock added", Toast.LENGTH_SHORT).show());
      AlertDialog dialog = builder.create();
      dialog.show();
    }
  }

  private boolean checkNetworkConnection() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

  private void notConnectedToTheInternet() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("No Network Connection");
    builder.setMessage("Stocks cannot be updated without a network connection.");
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  public void addStock(Stock stockToAdd) {
    if (stockList.contains(stockToAdd)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Stock already added");
      builder.setMessage(String.format("The stock %s is already added", stockToAdd.getSymbol()));
      AlertDialog dialog = builder.create();
      dialog.show();
    } else {
      stockList.add(stockToAdd);
      Collections.sort(stockList);
      writeJSON();
      stockAdapter.notifyDataSetChanged();
    }
  }

  private void updateStocks() {
    stockList.clear();
    ArrayList<Stock> tempList = readJSON();
    for (Stock s : tempList) {
      doSelection(s.getSymbol());
    }
    stockAdapter.notifyDataSetChanged();
    swiper.setRefreshing(false);
  }
}