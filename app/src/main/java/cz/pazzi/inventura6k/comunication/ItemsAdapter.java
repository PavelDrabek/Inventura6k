package cz.pazzi.inventura6k.comunication;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cz.pazzi.inventura6k.R;
import cz.pazzi.inventura6k.data.Item;
import cz.pazzi.inventura6k.data.Settings;

/**
 * Created by Pavel on 23.08.2016.
 */
public class ItemsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Item> items;

    public ItemsAdapter(Context mContext, List<Item> mProductList) {
        this.mContext = mContext;
        this.items = mProductList;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = View.inflate(mContext, R.layout.item_item, null);

        Item item = items.get(position);
        TextView tName = (TextView)itemView.findViewById(R.id.tName);
        TextView tRegNumber = (TextView)itemView.findViewById(R.id.tRegNumber);
        TextView tPlace = (TextView)itemView.findViewById(R.id.tPlace);

        ImageView img = (ImageView)itemView.findViewById(R.id.imgPhoto);

        tName.setText(item.name);
        tRegNumber.setText("6/" + item.regNumber);
        tPlace.setText(item.place);

        if(item.imgUrl != null && !item.imgUrl.isEmpty()) {
            Log.d("aa", "downloading img: " + Settings.urlServer + item.imgUrl);
            new PhotoDownloader(mContext, Settings.urlServer + item.imgUrl, img).execute();
        }

        return itemView;
    }
}
