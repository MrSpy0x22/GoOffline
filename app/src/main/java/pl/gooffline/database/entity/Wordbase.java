package pl.gooffline.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_base")
public class Wordbase {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    long wordId;
    String word;
    long wordCategoryId;

    public Wordbase(String word, long wordCategoryId) {
        this.word = word;
        this.wordCategoryId = wordCategoryId;
    }

    //region Setter-y i Getter-y
    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getWordCategoryId() {
        return wordCategoryId;
    }

    public void setWordCategoryId(long wordCategoryId) {
        this.wordCategoryId = wordCategoryId;
    }
    //endregion
}
