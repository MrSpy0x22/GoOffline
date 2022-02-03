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

        //<editor-fold desc="Imiona">
        result.put("Adam" , "Imiona");
        result.put("Marcin" , "Imiona");
        result.put("Dominika" , "Imiona");
        result.put("Dominik" , "Imiona");
        result.put("Natalia" , "Imiona");
        result.put("Anna" , "Imiona");
        result.put("Urszula" , "Imiona");
        result.put("Karolina" , "Imiona");
        result.put("Krzysztof" , "Imiona");
        result.put("Wiktor" , "Imiona");
        //</editor-fold>

        //<editor-fold desc="Pojazdy">
        result.put("Rower" , "Pojazdy");
        result.put("Samochod" , "Pojazdy");
        result.put("Smieciarka" , "Pojazdy");
        result.put("Motor" , "Pojazdy");
        result.put("Skuter" , "Pojazdy");
        result.put("Hulajnoga" , "Pojazdy");
        result.put("Samolot" , "Pojazdy");
        result.put("Helikopter" , "Pojazdy");
        result.put("Rakieta" , "Pojazdy");
        //</editor-fold>

        return result;
    }
}
