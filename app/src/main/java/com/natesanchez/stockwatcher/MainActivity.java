package com.natesanchez.stockwatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, View.OnLongClickListener{

  private SwipeRefreshLayout swiper;
  private List<Stock> stockList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    swiper = findViewById(R.id.swiper);
    //swiper.setOnRefreshListener(this);
  }

  @Override
  public void onClick(View view) {

  }

  @Override
  public boolean onLongClick(View view) {
    return false;
  }

  /*@Override
  public void onRefresh() {
    new
  }*/

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
        jw.name("name").value(s.getName());
        jw.name("date").value(s.getDate().toString());
        jw.name("isEnabled").value(s.isEnabled());
        jw.name("type").value(s.getType());
        jw.name("iexId").value(s.getIexId());
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
                jObj.getString("name"),
                jObj.getLong("date"),
                jObj.getBoolean("isEnabled"),
                jObj.getString("type"),
                jObj.getInt("iexID")
        );
        //save json object into stock object
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}