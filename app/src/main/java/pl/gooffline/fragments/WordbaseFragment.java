package pl.gooffline.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import pl.gooffline.R;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.lists.WordbaseList;
import pl.gooffline.presenters.WordbasePresenter;

public class WordbaseFragment extends Fragment implements WordbasePresenter.View {
    private List<Object[]> wordList = new ArrayList<>();
    private RecyclerView listWordBase;
    private WordbaseList wordbaseListAdapter;
    private ItemTouchHelper.SimpleCallback touchHelperCallback;
    private FloatingActionButton floatingAddButton;
    private WordbasePresenter presenter;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wordbase , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        presenter = new WordbasePresenter(requireContext());

        floatingAddButton = view.findViewById(R.id.wordbase_add_button);
        floatingAddButton.setOnClickListener(view1 -> this.onClickAddButton());

        touchHelperCallback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) { // <---[###]
                    wordList.remove(position);
                    wordbaseListAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext() , R.color.recycler_delete_bg))
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(requireContext() , R.color.recycler_icon_tint))
                        .addSwipeLeftActionIcon(R.drawable.ic_recycler_action_delete)
                        .create()
                        .decorate();

                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        DividerItemDecoration horizontalLine = new DividerItemDecoration(requireContext() , DividerItemDecoration.VERTICAL);

        listWordBase = view.findViewById(R.id.wordbase_list);
        listWordBase.setLayoutManager(new LinearLayoutManager(requireContext()));
        listWordBase.addItemDecoration(horizontalLine);

        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(listWordBase);

        wordbaseListAdapter = new WordbaseList(wordList , this::onListItemEdit);
        listWordBase.setAdapter(wordbaseListAdapter);

        this.onViewReady();
    }

    private void showWordFormFragment() {
        showWordFormFragment(null , null);
    }

    private void showWordFormFragment(Category category , Wordbase word) {
        // Jeżeli kategoria i słowo nie jest nullem to znaczy, że przejście nastąpi w tryb edycji
        NavDirections action;
        if (category != null && word != null) {
            action = WordbaseFragmentDirections.actionWordbaseToForm(word.getWord(), category.getCategoryName());
        } else {
            action = WordbaseFragmentDirections.actionWordbaseToForm("" , "");
        }

        navController.navigate(action);
    }

    @Override
    public void onClickAddButton() {
        showWordFormFragment();
    }

    @Override
    public void onListItemEdit(Object[] wordAndCategory) {
        if (wordAndCategory.length == 2) {
            Wordbase word = (Wordbase) wordAndCategory[0];
            Category category = (Category) wordAndCategory[1];

            // Tryb edycji
            if (word != null && category != null) {
                showWordFormFragment(category , word);
            } else {
                showWordFormFragment();
            }
        }
    }

    @Override
    public void onListEditDelete() {

    }

    @Override
    public void onSearch(String text) {

    }

    @Override
    public void onListItemDeleted() {

    }

    @Override
    public void onViewReady() {
        wordList.clear();
        wordList.addAll(presenter.getWordAndCategory());
        wordbaseListAdapter.notifyDataSetChanged();
    }

    private List<String[]> convertWordAndCategoriesMapToNamesArrray(Map<Wordbase , List<Category>> categories) {
        List<String[]> result = new ArrayList<>();

        categories.forEach((word , catList) -> {
            String wordName = word.getWord();
            String categoryName = catList.stream().map(Category::getCategoryName).findFirst().orElse(null);

            result.add(new String[] { wordName , categoryName });
        });

        return result;
    }

    private Map<Wordbase , Category> convertWordAndCategoriesMapToMap(Map<Wordbase , List<Category>> categories) {
        Map<Wordbase , Category> result = new HashMap<>();

        categories.forEach((word , catList) -> {
            Category category = catList.stream().findFirst().orElse(null);
            result.put(word , category);
        });

        return result;
    }
}
