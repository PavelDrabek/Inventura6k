package cz.pazzi.inventura6k.data;

import android.util.Log;

import com.google.gson.JsonObject;

import java.util.Calendar;

/**
 * Created by pavel on 21.08.16.
 */
public class Item {
    public String id;
    public String name;
    public String regNumber;
    public String price;
    public String place;
    public Calendar buyDate;
    public String description;

    public String imgUrl;

    public Item(JsonObject json) {
        try {
            id = json.get("id").getAsString();
            name = json.get("name").getAsString();
            regNumber = json.get("regNumber").getAsString();
            if(!json.get("price").isJsonNull()) {
                price = json.get("price").getAsString();
            }
            if(!json.get("place").isJsonNull()) {
                place = json.get("place").getAsString();
            }
            //buyDate = DateParser.ParseDate(json.get("buyDate").getAsString());
            if(!json.get("description").isJsonNull()) {
                description = json.get("description").getAsString();
            }
            if(!json.get("imgUrl").isJsonNull()) {
                imgUrl = json.get("imgUrl").getAsString();
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "error, json: " + json.toString());
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }
}
