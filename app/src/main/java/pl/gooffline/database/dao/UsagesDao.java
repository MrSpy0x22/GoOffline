package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.gooffline.database.entity.Usages;

@Dao
public interface UsagesDao {
    @Query("SELECT * FROM usages u WHERE u.dayTimestamp >= :start AND u.dayTimestamp <= :end")
    List<Usages> getAllBetween(int start , int end);
    @Query("SELECT * FROM usages u WHERE u.dayTimestamp = :day")
    List<Usages> getAllByDay(int day);
    @Query("SELECT * FROM usages u WHERE u.packageName = :packageName AND u.dayTimestamp = :dayStamp")
    Usages getSpecific(String packageName , long dayStamp);
    @Insert
    void insertNew(Usages usages);
    @Update
    void updateExisting(Usages usages);
    @Delete
    void deleteExisting(Usages usages);
}
