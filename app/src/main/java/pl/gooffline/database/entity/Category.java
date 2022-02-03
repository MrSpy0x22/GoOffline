package pl.gooffline.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    long categoryId;
    @ColumnInfo(name = "category_name")
    String categoryName;

    //region Konstruktor/y
    public Category(String categoryName) {
        this.categoryName = categoryName.toLowerCase();
    }
    //endregion

    //region Setter-y & Getter-y
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName.toLowerCase();
    }
    //endregion
}
