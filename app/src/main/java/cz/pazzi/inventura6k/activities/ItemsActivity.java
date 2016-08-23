package cz.pazzi.inventura6k.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import cz.pazzi.inventura6k.R;

public class ItemsActivity extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        list = (ListView)findViewById(R.id.listView);
    }
}
