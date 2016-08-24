package cz.pazzi.inventura6k.data;

/**
 * Created by pavel on 21.08.16.
 */
public class Settings {
    public static String urlServer = "http://6k.pazzi.cz/";
    public static String urlServerApi = urlServer + "api/";
    public static String urlPhotoUpload = urlServerApi + "photoUploader.php";
    public static String urlNextRegNumber = urlServerApi + "items.php?action=nextRegNumber";
    public static String urlGetItems = urlServerApi + "items.php?action=get";

}
