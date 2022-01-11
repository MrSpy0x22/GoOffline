package pl.gooffline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pl.gooffline.R;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.presenters.WordbaseFormPresenter;

public class WordbaseFormFragment extends Fragment implements WordbaseFormPresenter.View {
    private final int WORD_LIMIT_MIN = 3;
    private final int WORD_LIMIT_MAX = 16;
    private final int CATEGORY_LIMIT_MIN = 3;
    private final int CATEGORY_LIMIT_MAX = 16;

    private WordbaseFormPresenter presenter;
    private EditText wordEdit;
    private AutoCompleteTextView categoryEdit;
    private FloatingActionButton floatingSave;

    private ArrayAdapter<String> categoriesAutocompleteAdapter;
    private List<String> categories;

    private String argCategory;
    private String argWord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wordbase_form , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        argCategory = WordbaseFormFragmentArgs.fromBundle(getArguments()).getCategory();
        argWord = WordbaseFormFragmentArgs.fromBundle(getArguments()).getWord();

        // Tworzenie obiektu prezewntera
        presenter = new WordbaseFormPresenter(requireContext());

        // Szukanie kontrolek
        wordEdit = view.findViewById(R.id.wordbase_form_word_edit);
        categoryEdit = view.findViewById(R.id.wordbase_form_category_edit);
        floatingSave = view.findViewById(R.id.wordbase_form_save);

        floatingSave.setOnClickListener(e -> this.onClickSave());

        // Tryb edycji
        if (argCategory != null && argWord != null) {
            wordEdit.setText(argWord);
            categoryEdit.setText(argCategory);
        }

        this.onViewReady();
    }

    //region Funkcje interfejsu widoku.
    @Override
    public void onClickSave() {
        String message = this.onValidate();
        String categoryName = categoryEdit.getText().toString();

        if (message.length() == 0) { // Udana walidacja
            Category category;
            Wordbase word = null;

            // Jeżeeli kategoria nie istnieje to zostanie najpierw utworzona w celu pobrania unikalnego ID
            if (presenter.isCategoryExists(categoryName)) {
                category = presenter.getCategory(categoryName);
            } else {
                category = presenter.saveCategoryAndGet(new Category(categoryName));
            }

            // Szukanie słowa w bazie danych jeżeli jest to tryb edycji
            if (argCategory != null && argWord != null) {
                word = presenter.getWord(argWord);
            }

            // Jeżeli nie jest to tryb edycji lub wystąpoł problem przy ładowaniu słowa z bazy
            if (word == null) {
                word = new Wordbase(wordEdit.getText().toString() , category.getCategoryId());
            }

            presenter.saveWord(word);

            // Powrót do poprzedniego fragmentu
            NavHostFragment fragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);

            if (fragment != null) {
                NavController navController = fragment.getNavController();
                navController.navigateUp();
            } else {
                Log.d(this.getClass().toString() , "fragment = null");
            }
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Walidacja")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton("OK" , null)
                    .create()
                    .show();
        }
    }

    @Override
    public void onClickNavigationBack() {

    }

    @Override
    public String onValidate() {
        String word = wordEdit.getText().toString();
        int wordLen = word.length();
        String category = categoryEdit.getText().toString();
        int categoryLen = category.length();

        // Limit znaków słowa
        if (wordLen < WORD_LIMIT_MIN || wordLen > WORD_LIMIT_MAX) {
            return "Słowo musi składać się z " + WORD_LIMIT_MIN + "-" + WORD_LIMIT_MAX + " znaków.";
        }

        // Limit znaków kategorii
        if (categoryLen < CATEGORY_LIMIT_MIN || categoryLen > CATEGORY_LIMIT_MAX) {
            return "Nazwa kategorii musi składać się z " + CATEGORY_LIMIT_MIN + "-" + CATEGORY_LIMIT_MAX + " znaków.";
        }

        // Jeżeli słowo już istnieje
        if (presenter.isWordExists(word)) {
            return "Podane słowo juz istnieje.";
        }

        // Pusty string oznacza udaną walidację
        return "";
    }

    @Override
    public void onViewReady() {
        // Wczytywanie kategorii z prezentera i tworzenie adaptera
        categories = presenter.getCategories(true);
        categoriesAutocompleteAdapter = new ArrayAdapter<>(
                requireContext() , android.R.layout.simple_list_item_1 ,  categories
        );

        // Dodawanie adaptera do pola tekstowego
        categoryEdit.setAdapter(categoriesAutocompleteAdapter);
    }
    //endregion
}
