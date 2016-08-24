package cz.pazzi.inventura6k.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cz.pazzi.inventura6k.R;
import cz.pazzi.inventura6k.ServerGateway;
import cz.pazzi.inventura6k.comunication.ItemsAdapter;
import cz.pazzi.inventura6k.comunication.ServerListener;
import cz.pazzi.inventura6k.data.Item;
import cz.pazzi.inventura6k.data.Settings;

public class ItemsActivity extends AppCompatActivity {

    ListView list;
    List<Item> items;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        list = (ListView)findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);


        items = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(this, items);
        list.setAdapter(itemsAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(ItemsActivity.this, InventoryActivity.class);
            startActivity(i);
            }
        });

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(ItemsActivity.this, LoanActivity.class);
//                i.putExtra("loanId", ((Item)parent.getItemAtPosition(position)).id);
//                startActivity(i);
//            }
//        });

        onRefresh();
    }

    public void Parse(JsonElement json) {
        items.clear();
        JsonArray jLoans = json.getAsJsonArray();
        Log.d(getClass().getSimpleName(), "size: " + jLoans.size());

        for (int i = 0; i < jLoans.size(); i++) {
//            Log.d(getClass().getSimpleName(), "i = " + i);
            Item item = new Item(jLoans.get(i).getAsJsonObject());
            items.add(item);
        }
        list.invalidateViews();
    }

    public void onRefresh() {
        Log.d(getClass().getSimpleName(), "on refresh by swipe");
//        new LoansDownloader(this).execute();

        DownloadPage(0);
    }

    private void DownloadPage(int page) {
        new ServerGateway(Settings.urlGetItems + "&orderBy=regNumber&order=ASC&from=" + page * 20 + "&count=20", new ServerListener() {
            @Override
            public void OnServerResult(String result) {
                JsonParser jp = new JsonParser(); //from gson
                Log.d(getClass().getSimpleName(), "result: " + result);
                Parse(jp.parse(result));
            }

            @Override
            public void OnServerOK(JsonElement json) {
//                Parse(json);
            }

            @Override
            public void OnServerError(String error) {
                Toast.makeText(ItemsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
