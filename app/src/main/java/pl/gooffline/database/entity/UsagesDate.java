package pl.gooffline.database.entity;

import androidx.room.PrimaryKey;

import java.time.LocalDate;

public class UsagesDate {
    @PrimaryKey(autoGenerate = true)
    int id;
    LocalDate date;

    // fg
}
