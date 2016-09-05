package cz.pazzi.inventura6k.comunication;

import com.google.gson.JsonElement;

/**
 * Created by pavel on 21.08.16.
 */
public interface ServerListener {
    void OnServerResult(String result);
    void OnServerOK(JsonElement json);
    void OnServerError(String error);
}
