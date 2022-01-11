package pl.gooffline.database.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;

public class WordsCategory {
    @Embedded
    Category wordCategory;
    @Relation(parentColumn = "categoryId" , entityColumn = "wordCategoryId")
    List<Wordbase> wordsInCategory;
}
