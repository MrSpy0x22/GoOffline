package pl.gooffline.presenters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.CategoryDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

public class WordbasePresenter {
    //region Interfejs widoku
    public interface View {
        void onClickAddButton();
        void onListItemEdit(Object[] wordAndCategory);
        void onListEditDelete();
        void onSearch(String text);
        void onListItemDeleted();
        void onViewReady();
    }
    //endregion

    //region Pola klasy
    private final CategoryDao categoryDao;
    private final WordbaseDao wordbaseDao;
    //endregion

    //region Konstruktor/y
    public WordbasePresenter(Context context) {
        this.categoryDao = AppDatabase.getInstance(context).categoryDAO();
        this.wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
    }
    //endregion

    /**
     * Zwraca losową kategorie na podstawie danych w bazie.
     * @return Obiekt kategorii.
     */
    private Category getRandomCategory() {
        List<Category> categories = categoryDao.getAll();
        int size = categories.size();
        Random random = new Random();

        return categories.get(random.nextInt(size));
    }

    /**
     * Zwraca losowe słowo o podanej kategorii na podstawie danych z bazy.
     * @param category Kategoria, z której ma zostać wylosowane słowo.
     * @return Obiekt słowa/
     */
    public Wordbase getRandomWordFromCategory(Category category) {
        if (category != null) {
            Map<Category, List<Wordbase>> categoryWithWords = categoryDao.getAllByWithWordsCategory(category.getCategoryId());

            if (categoryWithWords.containsKey(category)) {
                List<Wordbase> words = categoryWithWords.get(category);
                int size = words != null ? words.size() : 0;
                Random random = new Random();

                return size == 0 ? null : words.get(random.nextInt(size));
            }
        }

        return null;
    }

    /**
     * Dodaje nowe słowo do bazy danych.
     * @param category Kategoria słowa.
     * @param wordbase Słowo.
     * @return TRUE jeżeli słowo zostało dodane lub FALSE w innym przypadku.
     */
    public boolean addNewWord(Category category , Wordbase wordbase) {
        if (category == null || wordbase == null) {
            throw new IllegalArgumentException("Kategoria i/lub słowo nie może mieć wartości NULL.");
        }

        Wordbase existingWord = wordbaseDao.getByName(wordbase.getWord());

        if (existingWord == null) {
            categoryDao.insert(category);
            wordbaseDao.insert(wordbase);

            return true;
        }

        return false;
    }

    /**
     * Usuwa istniejące słowo z bazy danych
     * @param wordbase Słowo, które ma zostać usunięte.
     * @return TRUE jueżeli słowo zostało usunięte lub FALSE w innym przypadku.
     */
    public boolean removeExistingWord(Wordbase wordbase) {
        if (wordbase == null) {
            throw new IllegalArgumentException("Słowo nie może mieć wartości NULL.");
        }

        Wordbase existingWord = wordbaseDao.getByName(wordbase.getWord());

        if (existingWord != null) {
            wordbaseDao.delete(wordbase);

            return true;
        }

        return false;
    }

    /**
     * Szuka obiektu słowa w bazie danych.
     * @param name Nazwa.
     * @return Obiekt słowa.
     */
    public Wordbase findWord(String name) {
        return wordbaseDao.getByName(name);
    }

    /**
     * Szuka obiektu kategorii w bazie danych
     * @param name Nazwa lategorii.
     * @return Obiekt kategorii.
     */
    public Category findCategory(String name) {
        return categoryDao.getByName(name);
    }

    /**
     * Przetwarza dane z bazy danych do postaci tablicy obiektu słowa i kategorii tak, aby dane
     *  były zgodne z danymi, jakie obsługuje lista słów.
     * @return Lista z tablicą Obiektu słowa (#0) i kategorii (#1).
     */
    public List<Object[]> getWordAndCategory() {
        Map<Category , List<Wordbase>> data = categoryDao.getAllWithWords();
        List<Object[]> result = new ArrayList<>();

        if (data != null) {
            for (Map.Entry<Category , List<Wordbase>> m : data.entrySet()) {
                m.getValue().forEach(w -> result.add(new Object[] { w , m.getKey() }));
            }
        }

        return result;
    }
}
