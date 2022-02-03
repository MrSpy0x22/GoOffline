package pl.gooffline.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pl.gooffline.database.entity.Log;

@Dao
public interface LogDao {
    @Query("SELECT * FROM logging l WHERE l.log_date >= :timestampStart AND l.log_date <= :timestampEnd")
    List<Log> getBetween(long timestampStart , long timestampEnd);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Log log);
    @Query("DELETE FROM logging WHERE log_date <= :timestampMs")
    void deleteOlderThan(long timestampMs);
    @Query("DELETE FROM logging")
    void deleteAll();
}
