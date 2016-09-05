package cz.pazzi.inventura6k.comunication;

import cz.pazzi.inventura6k.ServerGateway;
import cz.pazzi.inventura6k.data.Settings;

/**
 * Created by Pavel on 24.08.2016.
 */
public class ItemsDownloader extends ServerGateway {
    public ItemsDownloader(String url, ServerListener listener, int page) {
        super(Settings.urlGetItems + "orderBy=regNumber&order=ASC&from=" + page * 20 + "&count=20", listener);
    }
}
