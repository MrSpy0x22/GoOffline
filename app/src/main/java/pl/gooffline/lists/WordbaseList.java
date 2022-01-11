package pl.gooffline.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import pl.gooffline.R;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

public class WordbaseList extends RecyclerView.Adapter<WordbaseList.ViewHolder> {
    private final List<Object[]> wordsList;
    private final RecyclerViewItemClick<Object[]> itemClickListener;

    //region ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView word;
        private TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.wordbase_list_word);
            category = itemView.findViewById(R.id.wordbase_list_category);

            itemView.setOnClickListener(this);
        }

        public TextView getWord() {
            return word;
        }

        public TextView getCategory() {
            return category;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
        }
    }
    //endregion

    public WordbaseList(List<Object[]> wordList, RecyclerViewItemClick<Object[]> itemClickListener) {
        this.wordsList = wordList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_wordbase_item , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object[] wordAndCategory = wordsList.get(position);
        holder.word.setText(((Wordbase) wordAndCategory[0]).getWord());
        holder.category.setText(((Category) wordAndCategory[1]).getCategoryName());
        holder.itemView.setOnClickListener(e -> itemClickListener.onItemClick(wordAndCategory));
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }
}
