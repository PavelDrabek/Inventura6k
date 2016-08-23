package cz.pazzi.inventura6k.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import cz.pazzi.inventura6k.R;
import cz.pazzi.inventura6k.comunication.ItemsAdapter;
import cz.pazzi.inventura6k.data.Item;

public class ItemsActivity extends AppCompatActivity {

    ListView list;
    List<Item> items;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        list = (ListView)findViewById(R.id.listView);

        items = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(this, items);
        list.setAdapter(itemsAdapter);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(ItemsActivity.this, LoanActivity.class);
//                i.putExtra("loanId", ((Item)parent.getItemAtPosition(position)).id);
//                startActivity(i);
//            }
//        });
    }

    public void Parse(JsonElement json) {
//        items.clear();
        JsonArray jLoans = json.getAsJsonArray();
        for (int i = 0; i < jLoans.size(); i++) {
//            Log.d(getClass().getSimpleName(), "i = " + i);
            Item nLoan = new Item(jLoans.get(i).getAsJsonObject());
            items.add(nLoan);
        }
        list.invalidateViews();
    }

    public void onRefresh() {
        Log.d(getClass().getSimpleName(), "on refresh by swipe");
//        new LoansDownloader(this).execute();
    }
}
