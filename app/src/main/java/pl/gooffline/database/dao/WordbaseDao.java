package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

@Dao
public interface WordbaseDao {
    @Query("SELECT * FROM word_base wb")
    List<Wordbase> getAll();
    @Query("SELECT * FROM word_base wb WHERE wb.word = :name LIMIT 1")
    Wordbase getByName(String name);
    @Query("SELECT * FROM word_base wb JOIN word_category wc ON wb.wordCategoryId = wc.category_id AND wb.word_id = :id LIMIT 1")
    Map<Wordbase , Category> getWordAndCategoryByWordId(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Wordbase... wordbaseList);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Wordbase wordbase);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Wordbase> data);
    @Delete
    void delete(Wordbase wordbase);
    @Query("DELETE FROM word_base")
    void deleteAll();
}
