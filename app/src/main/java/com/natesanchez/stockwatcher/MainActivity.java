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

import com.natesanchez.stockwatcher.apis.SymDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, View.OnLongClickListener{

  private SwipeRefreshLayout swiper;
  private RecyclerView recyclerView;
  private StockAdapter stockAdapter;
  private List<Stock> stockList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SymDownloader sd = new SymDownloader();
    new Thread(sd).start();

    readJSON();
    stockList.add(new Stock("test1", "test1", 10.0, 10.0, 10.0));
    stockList.add(new Stock("test2", "test2", 20.0, 20.0, 20.0));
    stockList.add(new Stock("test3", "test3", 30.0, 30.0, 30.0));
    stockList.add(new Stock("test4", "test4", 40.0, 40.0, 40.0));
    stockList.add(new Stock("test5", "test5", 50.0, 50.0, 50.0));
    // swiper set up
    swiper = findViewById(R.id.swiper);
    swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        doRefresh();
      }
    });
    // recycler view setup
    recyclerView = findViewById(R.id.stockRecycler);
    stockAdapter = new StockAdapter(stockList, this);
    recyclerView.setAdapter(stockAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        stockList.remove(recyclerView.getChildLayoutPosition(view));
        Toast.makeText(getApplicationContext(),"Stock deleted",Toast.LENGTH_SHORT).show();
        stockAdapter.notifyDataSetChanged();
      }
    });
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        Toast.makeText(getApplicationContext(),"Cancelled deletion",Toast.LENGTH_SHORT).show();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
    return false;
  }

  private void writeJSON() {
    try {
      FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.json_stocks_file), Context.MODE_PRIVATE);
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

  private void readJSON() {
    try {
      FileInputStream fis = getApplicationContext().openFileInput(getString(R.string.json_stocks_file));
      byte[] data = new byte[(int) fis.available()];
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
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void doRefresh() {
    stockAdapter.notifyDataSetChanged();
    swiper.setRefreshing(false);
    Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
  }

  public void doneWithData() {
    swiper.setRefreshing(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.addStockButton:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol");
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            addStock(et.getText().toString().trim());
          }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(getApplicationContext(),"No stock added",Toast.LENGTH_SHORT).show();
          }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
      default:
        Toast.makeText(this, "error adding", Toast.LENGTH_SHORT).show();
        return false;
    }
  }

  private void addStock(String sym) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Make a Selection");
    //builder.setPositiveButton()
    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        Toast.makeText(getApplicationContext(),"No stock added",Toast.LENGTH_SHORT).show();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
    Toast.makeText(getApplicationContext(),"Stock added",Toast.LENGTH_SHORT).show();
    stockAdapter.notifyDataSetChanged();
  }

  private boolean checkNetworkConnection() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }
}