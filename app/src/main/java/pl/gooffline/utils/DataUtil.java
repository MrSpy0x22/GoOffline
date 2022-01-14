package pl.gooffline.utils;

import java.util.HashMap;
import java.util.Map;

public class DataUtil {
    public static Map<String , String> getDefaultWordsWithCategory() {
        Map<String , String> result = new HashMap<>();

        //<editor-fold desc="Edukacja">
        result.put("Szkoła" , "Edukacja");
        result.put("Nauczyciel" , "Edukacja");
        result.put("Przerwa" , "Edukacja");
        result.put("Sekretariat" , "Edukacja");
        result.put("Uczeń" , "Edukacja");
        result.put("Matematyka" , "Edukacja");
        result.put("Historia" , "Edukacja");
        result.put("Dyrektor" , "Edukacja");
        result.put("Wychowawca" , "Edukacja");
        result.put("Boisko" , "Edukacja");
        //</editor-fold>

        return result;
    }
}
