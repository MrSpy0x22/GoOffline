package pl.gooffline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import pl.gooffline.R;
import pl.gooffline.database.entity.Config;
import pl.gooffline.presenters.HomePresenter;
import pl.gooffline.utils.ConfigUtil;
import pl.gooffline.utils.PermissionsUtil;

public class HomeFragment extends Fragment implements HomePresenter.View {
    private HomePresenter presenter;
    private MaterialCardView cardLimit;
    private MaterialCardView cardSleep;
    private MaterialCardView cardGame;
    private MaterialCardView cardPolicy;
    private MaterialCardView cardStats;
    private TextView sleeptimeRangeText;
    private TextView gameAttemptsText;
    private TextView notificationPermissionText;
    private TextView notificationCredentialsText;
    private LinearLayout notificationLayout;

    private ProgressBar progressLimitIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obiekt prezentera
        presenter = new HomePresenter(requireContext());

        // Swipe Layout
        SwipeRefreshLayout swipeLayout = view.findViewById(R.id.home_swipe_layout);
        swipeLayout.setOnRefreshListener(() -> {
            onViewReady();
            swipeLayout.setRefreshing(false);
        });

        // Szukanie kart
        cardLimit = view.findViewById(R.id.home_card_limit);
        cardSleep = view.findViewById(R.id.home_card_sleep);
        cardGame = view.findViewById(R.id.home_card_game);
        cardPolicy = view.findViewById(R.id.home_card_policy);
        cardStats = view.findViewById(R.id.home_card_stats);

        // Szukania innych kontrolek
        progressLimitIndicator = view.findViewById(R.id.home_limit_indicator);
        sleeptimeRangeText = view.findViewById(R.id.home_sleeptime_range_text);
        gameAttemptsText = view.findViewById(R.id.home_game_attempts_text);
        notificationLayout = view.findViewById(R.id.home_notification_layout);
        notificationPermissionText = view.findViewById(R.id.home_notification_text_permissions);
        notificationCredentialsText = view.findViewById(R.id.home_notification_text_credentials);

        Button policyButton = view.findViewById(R.id.home_card_policy_button);
        policyButton.setOnClickListener(e -> this.onClickPolicyButton());

        Button statsButton = view.findViewById(R.id.card_button_stats);
        statsButton.setOnClickListener(e -> this.onClickStatsButton());

        Button gameButton = view.findViewById(R.id.home_card_game_button);
        gameButton.setOnClickListener(e -> this.onClickGameButton());

        this.onViewReady();
    }

    @Override
    public void onViewReady() {
        // Pobieranie potrzebnych danych z konfiguracji
        List<Config> config = presenter.pullConfigFrom(Arrays.asList(
                ConfigUtil.KnownKeys.KK_ALLOWED_TIME.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName() ,
                ConfigUtil.KnownKeys.KK_GAME_ENABLE.getKeyName() ,
                ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS.getKeyName() ,
                ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD.getKeyName()
        ));

        // Funkcja pomocnicza wyciągająca wartość klucza z listy
        Function<ConfigUtil.KnownKeys, String> getValue = knownKey -> config.stream()
                .filter(c -> c.getConfigKey().equals(knownKey.getKeyName()))
                .map(Config::getConfigValue)
                .findFirst()
                .orElse(knownKey.getDefaultValue());

        // Ustawianie stanu licznika wykorzystanego czasu
        String configValue = getValue.apply(ConfigUtil.KnownKeys.KK_ALLOWED_TIME);
        int limit = configValue == null ? 0 : Integer.parseInt(configValue);

        progressLimitIndicator.setMax(limit); // Wartość maksymalna
        // TODO: wartość obecna

        // Czas snu
        configValue = getValue.apply(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE);
        boolean sleepTimeEnabled = configValue != null && configValue.equals("true");
        if (sleepTimeEnabled) {
            int sleepStart = Integer.parseInt(getValue.apply(ConfigUtil.KnownKeys.KK_SLEEPTIME_START));
            int sleepStop = Integer.parseInt(getValue.apply(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP));

            // Formatowanie tekstu z czasem snu
            sleeptimeRangeText.setText(
                    String.format(getString(R.string.home_sleeptime_range) , sleepStart , sleepStop)
            );

            cardSleep.setVisibility(View.VISIBLE);
        } else {
            cardSleep.setVisibility(View.GONE);
        }

        // Dodatkowy czas
        boolean gameEnabled = getValue.apply(ConfigUtil.KnownKeys.KK_GAME_ENABLE).equals("true");
        int gameAttempts = Integer.parseInt(getValue.apply(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS));

        if (gameEnabled) {
            gameAttemptsText.setText(String.valueOf(gameAttempts));
            cardGame.setVisibility(View.VISIBLE);
        } else {
            cardGame.setVisibility(View.GONE);
        }

        String passwordHash = getValue.apply(ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD);
        boolean isNotPasswordSet = passwordHash == null || passwordHash.length() <= 0;
        if (isNotPasswordSet) {
            notificationCredentialsText.setVisibility(View.VISIBLE);
        } else {
            notificationCredentialsText.setVisibility(View.GONE);
        }

        boolean hasAllPermissions = PermissionsUtil.getRequiredPermissionsStatus(requireContext())
                .values()
                .stream()
                .allMatch(p -> p);

        if (hasAllPermissions) {
            notificationPermissionText.setVisibility(View.GONE);
        } else {
            notificationPermissionText.setVisibility(View.VISIBLE);
        }

        if (isNotPasswordSet || !hasAllPermissions) {
            notificationLayout.setVisibility(View.VISIBLE);
        } else {
            notificationLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickPolicyButton() {
        new AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setTitle("Error")
                .setMessage("Not implemented!")
                .create()
                .show();
    }

    @Override
    public void onClickGameButton() {
        new AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setTitle("Error")
                .setMessage("Not implemented!")
                .create()
                .show();
    }

    @Override
    public void onClickStatsButton() {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_nav);

        if (navHostFragment != null) {
            navHostFragment.getNavController().navigate(R.id.action_home_to_stats);
        } else {
            Log.d(this.getClass().toString() , "Cannot navigate!");
        }
    }

    @Override
    public void onViewUpdated() {

    }
}