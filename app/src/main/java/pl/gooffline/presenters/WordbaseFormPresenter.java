package pl.gooffline.presenters;

import android.content.Context;

import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.CategoryDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

public class WordbaseFormPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onClickSave();
        void onClickNavigationBack();
        String onValidate();
        void onViewReady();
    }

    //region Pola klasy
    private CategoryDao categoryDao;
    private WordbaseDao wordbaseDao;
    private List<String> categories;
    //endregion

    //region Konstruktor/y
    public WordbaseFormPresenter(Context context) {
        this.categoryDao = AppDatabase.getInstance(context).categoryDAO();
        this.wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
        this.categories = getCategories(true);
    }
    //endregion

    /**
     * Zwraca nazwę szystkich kategorii z bazy danych.
     * @param update Flaga oznaczająca aktualizację danych z bazy przed zwróceniem wyniku
     * @return Lista kategorii.
     */
    public List<String> getCategories(boolean update) {
        if (update || categories == null) {
            return categoryDao.getAllNames();
        }

        return this.categories;
    }

    /**
     * Pobiera jedną kategorię.
     * @param name Nazwa szukanej kategorii.
     * @return Obiekt kategorii.
     */
    public Category getCategory(String name) {
        return categoryDao.getByName(name);
    }

    /**
     * Pobiera jedno sło0wo.
     * @param word Nazwa szukanego słowa.
     * @return Obiekt słowa.
     */
    public Wordbase getWord(String word) {
        return wordbaseDao.getByName(word);
    }

    /**
     * Sprawdza czy podane słowo istnieje w bazie danych.
     * @param word Słowo w postaci tekstu, które ma zostać sprawdzone.
     * @return TRUE jeżeli słowo istnieje lub false w innym przypadku.
     * @throws IllegalArgumentException Jeżeli parametr funkcji jest NULL-em.
     */
    public boolean isWordExists(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Parametr funkcji nie może być NULL-em.");
        }

        return wordbaseDao.getByName(word) != null;
    }

    /**
     * Zapisuje słowo do bazy danych.
     * @param wordbase Obiekt słowa.
     */
    public void saveWord(Wordbase wordbase) {
        wordbaseDao.insert(wordbase);
    }

    /**
     * Sprawdza czy podana kategoria istnieje w bazie danych.
     * @param categoryName Nazwa kategorii.
     * @return TRUE jeżeli kategoria istnieje lub false w innym przypadku.
     * @throws IllegalArgumentException Jeżeli parametr funkcji jest NULL-em.
     */
    public boolean isCategoryExists(String categoryName) {
        if (categoryName == null) {
            throw new IllegalArgumentException("Parametr funkcji nie może być NULL-em.");
        }

        return categoryDao.getByName(categoryName) != null;
    }

    /**
     * Zapisuje kategorię do bazy i pobiera ją (w celu wygenerowania PrimaryKey).
     * @param category Kategoria, która ma zostać zapisana.
     * @return Zapisaną kategorię.
     */
    public Category saveCategoryAndGet(Category category) {
        categoryDao.insert(category);
        return categoryDao.getByName(category.getCategoryName());
    }

}
