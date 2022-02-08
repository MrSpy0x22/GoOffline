package pl.gooffline.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.dao.UsagesDao;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.database.entity.Usages;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.lists.UsagesList;
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
    //private TextView gameAttemptsText;
    private TextView notificationPermissionText;
    private TextView notificationCredentialsText;
    private TextView notificationServiceText;
    private TextView progressTextMax;
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

        ServiceConfigManager x = ServiceConfigManager.getInstance();

        // Obiekt prezentera
        presenter = new HomePresenter(requireContext());
        prepareServiceConfig();

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
        progressTextMax = view.findViewById(R.id.home_time_limit_text);
        progressLimitIndicator = view.findViewById(R.id.home_limit_indicator);
        sleeptimeRangeText = view.findViewById(R.id.home_sleeptime_range_text);
        //gameAttemptsText = view.findViewById(R.id.home_game_attempts_text);
        notificationLayout = view.findViewById(R.id.home_notification_layout);
        notificationPermissionText = view.findViewById(R.id.home_notification_text_permissions);
        notificationCredentialsText = view.findViewById(R.id.home_notification_text_credentials);
        notificationServiceText = view.findViewById(R.id.home_notification_text_service);

        Button policyButton = view.findViewById(R.id.home_card_policy_button);
        policyButton.setOnClickListener(e -> this.onClickPolicyButton());

        Button statsButton = view.findViewById(R.id.card_button_stats);
        statsButton.setOnClickListener(e -> this.onClickStatsButton());

        Button gameButton = view.findViewById(R.id.home_card_game_button);
        gameButton.setOnClickListener(e -> this.onClickGameButton());

        this.onViewReady();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgressBar(ServiceConfigManager.getInstance().getDailyTimeLimit());
    }

    private void updateProgressBar(Integer value) {
        if (progressLimitIndicator == null) {
            return;
        }

        int current = progressLimitIndicator.getProgress();
        int max = progressLimitIndicator.getMax();

        if (current < max) {
            progressLimitIndicator.setProgress(value > max ? max : value);
        }
    }

    @Override
    public void onViewReady() {
        // Ustawianie stanu licznika wykorzystanego czasu
        int totalLimit = ServiceConfigManager.getInstance().getDailyTimeTotal();
        int currentLimit = ServiceConfigManager.getInstance().getDailyTimeLimit();
        progressLimitIndicator.setMax(totalLimit); // Wartość maksymalna
        updateProgressBar(ServiceConfigManager.getInstance().getDailyTimeLimit());

        progressTextMax.setText(UsagesList.convertSecondsToString(totalLimit));
        // TODO: wartość obecna

        // Czas snu
        if (ServiceConfigManager.getInstance().isSleepTimeEnabled()) {
            int sleepStart = ServiceConfigManager.getInstance().getSleepTimeStart();
            int sleepStop = ServiceConfigManager.getInstance().getSleepTimeEnd();

            // Formatowanie tekstu z czasem snu
            sleeptimeRangeText.setText(
                    String.format(getString(R.string.home_sleeptime_range) , sleepStart , sleepStop)
            );

            cardSleep.setVisibility(View.VISIBLE);
        } else {
            cardSleep.setVisibility(View.GONE);
        }

        // Dodatkowy czas
        int gameAttempts = ServiceConfigManager.getInstance().getGameAttempts();

        if (ServiceConfigManager.getInstance().isGameEnabled()) {
            //gameAttemptsText.setText(String.valueOf(gameAttempts));
            cardGame.setVisibility(View.VISIBLE);
        } else {
            cardGame.setVisibility(View.GONE);
        }

        boolean istPasswordSet = ServiceConfigManager.getInstance().isSettingScreenProtected();
        if (istPasswordSet) {
            notificationCredentialsText.setVisibility(View.GONE);
        } else {
            notificationCredentialsText.setVisibility(View.VISIBLE);
        }

        boolean hasAllPermissions = PermissionsUtil.getRequiredPermissionsStatus(requireContext())
                .values()
                .stream()
                .allMatch(p -> p);

        boolean isServiceEnabled = ServiceConfigManager.getInstance().isServiceEnabled();
        if (isServiceEnabled) {
            notificationServiceText.setVisibility(View.GONE);
        } else {
            notificationServiceText.setVisibility(View.VISIBLE);
        }

        if (hasAllPermissions) {
            notificationPermissionText.setVisibility(View.GONE);
        } else {
            notificationPermissionText.setVisibility(View.VISIBLE);
        }

        if (!istPasswordSet || !hasAllPermissions || !isServiceEnabled) {
            notificationLayout.setVisibility(View.VISIBLE);
        } else {
            notificationLayout.setVisibility(View.GONE);
        }
    }

    private void prepareServiceConfig() {
        Context context = requireContext();
        LocalDateTime today = LocalDateTime.now();
        // Konfiguracji - lista wyjątków
        ServiceConfigManager.getInstance().setAllowedPackages(
                pullWhitelistFromDatabase(context)
        );

        // Dzienny limit
        ServiceConfigManager.getInstance().setDailyTimeLimit(pullDailyActivity(context , today.toLocalDate()));

        // Konfiguracja - ustawieniami
        List<Config> configList = pullConfigFromDatabase(context);
        boolean serviceEnabled = configList.stream()
                .filter(c -> c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_ACTIVE.getDefaultValue()))
                .anyMatch(c -> c.getConfigValue().equals("1"));

        ServiceConfigManager.getInstance().setServiceEnabled(serviceEnabled);
        ServiceConfigManager.getInstance().setDailyLimitDate(today);
        ServiceConfigManager.getInstance().updateServiceConfig(configList);
        ServiceConfigManager.getInstance().setThisPackageName(context.getPackageName());
        ServiceConfigManager.getInstance().setThisPackageName(context.getPackageName());
        ServiceConfigManager.getInstance().setSettingScreenProtected(
                configList.stream()
                        .filter(c -> c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD.getKeyName()))
                        .anyMatch(c -> !c.getConfigValue().isEmpty())
        );

        ServiceConfigManager.getInstance().setPlayWordId(-1);
        ServiceConfigManager.getInstance().setPlayAttempts(0);
        ServiceConfigManager.getInstance().setPlayDate(LocalDateTime.now());

        AtomicBoolean updatePlayData = new AtomicBoolean(false);
        configList.forEach(c -> {
            String key = c.getConfigKey();
            String value = c.getConfigValue();

            if (key.equals(ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName())) {
                ServiceConfigManager.getInstance().setWhitelistWhileSleeping(value.equals("1"));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_LOG_ENABLED.getKeyName())) {
                ServiceConfigManager.getInstance().setLogEnabled(value.equals("1"));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS.getKeyName())) {
                ServiceConfigManager.getInstance().setGameAttempts(Integer.parseInt(value));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_LOG_AUTHS.getKeyName())) {
                ServiceConfigManager.getInstance().setLogEventAuths(value.equals("1"));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_LOG_BLOCKING.getKeyName())) {
                ServiceConfigManager.getInstance().setLogEventBlocks(value.equals("1"));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_LOG_LIMITS.getKeyName())) {
                ServiceConfigManager.getInstance().setLogEventLimits(value.equals("1"));
            } else if (key.equals(ConfigUtil.KnownKeys.KK_PLAY_WORD_ID.getKeyName())) {
                int numValue = Integer.parseInt(value);

                if (numValue <= 0) {
                    numValue = -1;
                }

                ServiceConfigManager.getInstance().setPlayWordId(numValue);
            } else if (key.equals(ConfigUtil.KnownKeys.KK_PLAY_WORD_ATTEMPTS.getKeyName())) {
                int numValue = Integer.parseInt(value);

                ServiceConfigManager.getInstance().setPlayAttempts(numValue);
            } else if (key.equals(ConfigUtil.KnownKeys.KK_PLAY_DAY.getKeyName())) {
                LocalDateTime numDate = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(Integer.parseInt(value)) ,
                        ZoneOffset.UTC
                );

                if (ChronoUnit.DAYS.between(numDate, LocalDateTime.now()) != 0) {
                    numDate = LocalDateTime.now();
                    updatePlayData.set(true);
                }

                ServiceConfigManager.getInstance().setPlayDate(numDate);
            }
        });

        if (updatePlayData.get()) {
            ServiceConfigManager.getInstance().setPlayWordId(-1);
            ServiceConfigManager.getInstance().setPlayAttempts(0);
        }
    }

    /**
     * Pobiera listę wyjątków z bazy danych.
     *
     * @return Listę z nazwami pakietów.
     */
    private List<String> pullWhitelistFromDatabase(Context context) {
        // Kilka wyjątków, które należy uwzględnić
        PackageManager packageManager = requireContext().getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String launcher = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo
                .packageName;

        List<String> result = AppDatabase.getInstance(context).whitelistDAO()
                .getAll()
                .stream()
                .filter(w -> w.ignored)
                .map(Whitelist::getPackageName).collect(Collectors.toList());

        ServiceConfigManager.getInstance().setLauncherPackageName(launcher);

        return result;
    }

    private int pullDailyActivity(Context context , LocalDate today) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();
        int dayStamp = (int) today.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        List<Usages> data = usagesDao.getAllByDay(dayStamp);

        return data == null ? 0 : data.stream().mapToInt(Usages::getTotalSeconds).sum();
    }

    /**
     * Pobiera całą konfigurację z bazy danych.
     *
     * @return Listę z obiektami konfiguracji.
     * @see Config
     */
    private List<Config> pullConfigFromDatabase(Context context) {
        ConfigDao configDao = AppDatabase.getInstance(context).configDAO();
        return configDao.getAll();
    }

    @Override
    public void onClickPolicyButton() {
        List<Whitelist> list = AppDatabase.getInstance(requireContext()).whitelistDAO().getAll();

        if (!ServiceConfigManager.getInstance().isWhitelistWhileSleeping() || list.size() <= 0) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Wyjątki")
                    .setMessage("Administrator tego urządzenia nie pozwala na korzystanie z aplikacji poza określonym limitem czasu.")
                    .setNegativeButton("Zamknij" , null)
                    .create()
                    .show();
            return;
        }

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_nav);

        if (navHostFragment != null) {
            navHostFragment.getNavController().navigate(R.id.action_home_to_policy);
        } else {
            Log.d(this.getClass().toString() , "Cannot navigate!");
        }
    }

    @Override
    public void onClickGameButton() {
        // Brak słówek spowoduje rzucenie wyjątku dlatego...
        List<Wordbase> result = AppDatabase.getInstance(getContext()).wordbaseDAO().getAll();
        if (result == null || result.size() < 1) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Błąd")
                    .setMessage("Baza słówek jest pusta. Skontaktuj się z administratorem urządzenia.")
                    .setNegativeButton("Zamknij" , null)
                    .create()
                    .show();
            return;
        }

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_nav);

        if (navHostFragment != null) {
            navHostFragment.getNavController().navigate(R.id.action_home_to_play);
        } else {
            Log.d(this.getClass().toString() , "Cannot navigate!");
        }
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