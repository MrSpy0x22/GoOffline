package pl.gooffline.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.presenters.PlayPresenter;
import pl.gooffline.utils.ConfigUtil;

public class PlayFragment extends Fragment implements PlayPresenter.View {
    private final char hiddenLetterCharacter = '•';
    private Map<Wordbase, Category> wordAndCategory;
    private PlayPresenter presenter;
    private ProgressBar progress;
    private TextView wordPreviewText;
    private TextView categoryText;
    private TextView attemptsText;
    private Map<Character , Button> keyButtons;
    private List<Character> usedKeys;
    private int tries = 0;
    private int triesMax = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new PlayPresenter(view.getContext());
        usedKeys = new ArrayList<>();

        // Szukanie widoków
        progress = view.findViewById(R.id.play_progress);
        categoryText = view.findViewById(R.id.play_text_category);
        attemptsText = view.findViewById(R.id.play_text_attempts);
        wordPreviewText = view.findViewById(R.id.play_text_preview);

        // Szukanie widokó klawiszy
        keyButtons = new HashMap<>();
        //<editor-fold desc="Przypisywanie widoków do klawiszy">
        keyButtons.put('A' , view.findViewById(R.id.play_key_a));
        keyButtons.put('B' , view.findViewById(R.id.play_key_b));
        keyButtons.put('C' , view.findViewById(R.id.play_key_c));
        keyButtons.put('D' , view.findViewById(R.id.play_key_d));
        keyButtons.put('E' , view.findViewById(R.id.play_key_e));
        keyButtons.put('F' , view.findViewById(R.id.play_key_f));
        keyButtons.put('G' , view.findViewById(R.id.play_key_g));
        keyButtons.put('H' , view.findViewById(R.id.play_key_h));
        keyButtons.put('I' , view.findViewById(R.id.play_key_i));
        keyButtons.put('J' , view.findViewById(R.id.play_key_j));
        keyButtons.put('K' , view.findViewById(R.id.play_key_k));
        keyButtons.put('L' , view.findViewById(R.id.play_key_l));
        keyButtons.put('M' , view.findViewById(R.id.play_key_m));
        keyButtons.put('N' , view.findViewById(R.id.play_key_n));
        keyButtons.put('O' , view.findViewById(R.id.play_key_o));
        keyButtons.put('P' , view.findViewById(R.id.play_key_p));
        keyButtons.put('R' , view.findViewById(R.id.play_key_r));
        keyButtons.put('S' , view.findViewById(R.id.play_key_s));
        keyButtons.put('T' , view.findViewById(R.id.play_key_t));
        keyButtons.put('U' , view.findViewById(R.id.play_key_u));
        keyButtons.put('V' , view.findViewById(R.id.play_key_v));
        keyButtons.put('W' , view.findViewById(R.id.play_key_w));
        keyButtons.put('X' , view.findViewById(R.id.play_key_x));
        keyButtons.put('Y' , view.findViewById(R.id.play_key_y));
        keyButtons.put('Z' , view.findViewById(R.id.play_key_z));
        //</editor-fold>

        for (Map.Entry<Character , Button> b : keyButtons.entrySet()) {
            b.getValue().setOnClickListener(e -> this.onLetterKeyPressed(b.getKey() , b.getValue()));
        }

        this.onViewReady();
    }

    @Override
    public void onViewReady() {
        // Zapisywanie próby
        int attemptNum = ServiceConfigManager.getInstance().getPlayAttempts();
        ServiceConfigManager.getInstance().setPlayAttempts(++attemptNum);
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_PLAY_WORD_ATTEMPTS , String.valueOf(attemptNum));

        int wordId = ServiceConfigManager.getInstance().getPlayWordId();
        if (wordId <= -1) {
            wordAndCategory = presenter.getWordFromId(requireContext());
        }

        tries = 0;
        triesMax = (wordAndCategory.keySet().stream().findFirst().get().getWord().length() / 2);

        // Pasek ostrzeżeń
        progress.setProgress(0);
        progress.setMax(triesMax);

        wordPreviewText.setText(getWordPreviewedText(usedKeys));

        categoryText.setText(wordAndCategory.entrySet().stream().findFirst().get().getValue().getCategoryName());
        String formated =
                String.format("%d/%d", ServiceConfigManager.getInstance().getPlayAttempts() ,
                        ServiceConfigManager.getInstance().getGameAttempts());
        attemptsText.setText(formated);
    }

    private String getWordPreviewedText(List<Character> unrevealedLetters) {
        String wordText = wordAndCategory.entrySet().stream().findFirst().get().getKey().getWord().toUpperCase();
        StringBuilder result = new StringBuilder("");

        for (int i = 0 ; i < wordText.length() ; i++) {
            char ch = wordText.toUpperCase().charAt(i);
            if (!unrevealedLetters.contains(ch)) {
                result.append(hiddenLetterCharacter).append(" ");
            } else {
                result.append(ch).append(" ");
            }
        }

        return result.toString();
    }

    private boolean isAnyMatchForKey(char character) {
        return wordAndCategory.keySet().stream().anyMatch(w -> w.getWord().toUpperCase().contains(String.valueOf(character).toUpperCase()));
    }

    private boolean isWordUnrevealed(String previewText) {
        return !previewText.contains(String.valueOf(hiddenLetterCharacter));
    }

    private NavController getAppNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_nav);

        return navHostFragment.getNavController();
    }

    @Override
    public void onLetterKeyPressed(char key , Button button) {
        if (!usedKeys.contains(key)) {
            if (isAnyMatchForKey(key)) {
                if (!usedKeys.contains(key)) {
                    usedKeys.add(key);
                }

                String wp = getWordPreviewedText(usedKeys);
                wordPreviewText.setText(wp);

                if (isWordUnrevealed(wp)) {
                    this.onComplete();
                }
            } else {
                tries++;

                if (tries > triesMax) {
                    this.onAttemptsLimitReached();
                } else {
                    progress.setProgress(tries);
                }
            }
        }

        button.setEnabled(false);
    }

    @Override
    public void onSpecialKeyPressed(boolean isButtonNewGame) {

    }

    @Override
    public void onComplete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Koniec gry")
                .setMessage("Slówko odgadnięte.")
                .setCancelable(false)
                .setPositiveButton("Zamknij", (dialogInterface, i) -> getAppNavController().navigateUp()).create().show();
    }

    @Override
    public void onAttemptsLimitReached() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Koniec gry")
                .setMessage("Wykorzystano limit prob.")
                .setCancelable(false)
                .setPositiveButton("Zamknij", (dialogInterface, i) -> getAppNavController().navigateUp()).create().show();
    }
}
