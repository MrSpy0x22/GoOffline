package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM word_category c")
    List<Category> getAll();
    @Query("SELECT * FROM word_category c WHERE c.category_name = :name LIMIT 1")
    Category getByName(String name);
    @Query("SELECT c.category_name FROM word_category c")
    List<String> getAllNames();
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Category... categories);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category... categories);
    @Query("DELETE FROM word_category")
    void deleteAll();

    @Transaction
    @Query("SELECT * from word_category wc JOIN word_base wb ON wc.category_id = wb.wordCategoryId")
    Map<Category, List<Wordbase>> getAllWithWords();
    @Transaction
    @Query("SELECT * from word_category wc JOIN word_base wb ON wc.category_id = wb.wordCategoryId WHERE wc.category_id = :category")
    Map<Category, List<Wordbase>> getAllByWithWordsCategory(long category);
}
