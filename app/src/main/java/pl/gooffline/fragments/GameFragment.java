package pl.gooffline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.slider.Slider;

import pl.gooffline.R;
import pl.gooffline.presenters.GamePresenter;
import pl.gooffline.utils.ConfigUtil;

public class GameFragment extends Fragment implements GamePresenter.View {
    private GamePresenter presenter;
    private SwitchCompat gameEnableSwitch;
    private LinearLayout rowOptionEnable;
    private LinearLayout rowOptionWords;
    private Slider attemptsSlider;
    private TextView attemptsSliderText;
    private Slider bonusTimeSlider;
    private TextView bonusTimeSliderText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obiekt prezentera
        presenter = new GamePresenter(requireContext());

        rowOptionEnable = view.findViewById(R.id.game_row_enable);
        rowOptionEnable.setOnClickListener(e -> this.onGameEnabled(!gameEnableSwitch.isChecked()));

        // Szukanie widoków
        gameEnableSwitch = view.findViewById(R.id.game_enable_switch);

        rowOptionWords = view.findViewById(R.id.game_row_words);
        rowOptionWords.setOnClickListener(e -> this.onWordsOptionClick());

        attemptsSliderText = view.findViewById(R.id.game_attempts_slider_val);
        attemptsSlider = view.findViewById(R.id.game_attempts_slider);
        attemptsSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                onAttemptsSliderUpdated();
            }
        });

        bonusTimeSliderText = view.findViewById(R.id.game_bonus_time_slider_val);
        bonusTimeSlider = view.findViewById(R.id.game_bonus_time_slider);
        bonusTimeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                onBonusTimeSliderUpdated();
            }
        });

        this.onViewReady();
    }

    /**
     * Blokuje możliwość interakcji z widokami.
     * @param state Określa możliwość interakcji.
     */
    private void applyDisabledEffect(boolean state) {
        rowOptionWords.setEnabled(state);
        attemptsSlider.setEnabled(state);
        bonusTimeSlider.setEnabled(state);
    }

    @Override
    public void onViewReady() {
        gameEnableSwitch.setChecked(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_GAME_ENABLE).equals("true"));
        attemptsSlider.setValue(Float.parseFloat(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS)));
        attemptsSliderText.setText(String.valueOf(Math.round(attemptsSlider.getValue())));
        bonusTimeSlider.setValue(Float.parseFloat(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_GAME_BONUS_TIME)));
        bonusTimeSliderText.setText(String.format(getString(R.string.bonus_time_format) , Math.round(bonusTimeSlider.getValue())));

        applyDisabledEffect(gameEnableSwitch.isChecked());
    }

    @Override
    public void onGameEnabled(boolean newState) {
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_GAME_ENABLE , newState ? "true" : "false");
        gameEnableSwitch.setChecked(newState);
        applyDisabledEffect(newState);
    }

    @Override
    public void onWordsOptionClick() {
        NavHostFragment fragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);

        if (fragment != null) {
            NavController navController = fragment.getNavController();
            navController.navigate(R.id.action_game_to_wordbase);
        } else {
            Log.d(this.getClass().toString() , "fragment = null");
        }
    }

    @Override
    public void onAttemptsSliderUpdated() {
        int value = Math.round(attemptsSlider.getValue());
        String stringValue = String.valueOf(value);

        attemptsSliderText.setText(stringValue);
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS , stringValue);
    }

    @Override
    public void onBonusTimeSliderUpdated() {
        int value = Math.round(bonusTimeSlider.getValue());

        bonusTimeSliderText.setText(String.format(getString(R.string.bonus_time_format) , value));
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS , String.valueOf(value));
    }
}
