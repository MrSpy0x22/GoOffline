package pl.gooffline.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.entity.Log;
import pl.gooffline.lists.JournalList;
import pl.gooffline.presenters.LogPresenter;
import pl.gooffline.utils.ConfigUtil;

public class JournalFragment extends Fragment implements LogPresenter.View {
    private LogPresenter presenter;
    private List<Log> eventDataList;
    private JournalList eventListAdapter;
    private SwitchMaterial switchView;
    private LocalDateTime usedDateStamp;
    private TextView textCalendarDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new LogPresenter(view.getContext());

        // Zmienne pomocnicze
        usedDateStamp = LocalDateTime.now();

        // Widoki
        LinearLayout enableRow = view.findViewById(R.id.journal_row_enable);
        enableRow.setOnClickListener(e -> this.onLoggingEnabled());

        switchView = view.findViewById(R.id.sleeptime_row_enable_switch);

        textCalendarDate = view.findViewById(R.id.text_date_selector);
        textCalendarDate.setOnClickListener(view1 -> this.onClickCalendar(usedDateStamp));

        ImageButton prev = view.findViewById(R.id.journal_btn_back);
        prev.setOnClickListener(e -> this.onClickPreviousPage());

        ImageButton next = view.findViewById(R.id.journal_btn_next);
        next.setOnClickListener(e -> this.onClickPreviousnext());

        ImageButton btnDialogSelect = view.findViewById(R.id.journal_btn_dialog);
        btnDialogSelect.setOnClickListener(e -> createSelectDialog());

        ImageButton btnDialogDelete = view.findViewById(R.id.journal_btn_delete);
        btnDialogDelete.setOnClickListener(e -> createDeleteDialog());

        eventDataList = new ArrayList<>();
        eventListAdapter = new JournalList(view.getContext() , eventDataList);

        ListView listView = view.findViewById(R.id.journal_event_list);
        listView.setAdapter(eventListAdapter);

        this.onViewReady();
    }

    private void createSelectDialog() {
        Context context = requireContext();
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_journal_select, null);

        CheckBox auths = view.findViewById(R.id.dialog_logactions_auth);
        CheckBox blocks = view.findViewById(R.id.dialog_logactions_blocks);
        CheckBox limits = view.findViewById(R.id.dialog_logactions_limits);

        auths.setChecked(ServiceConfigManager.getInstance().isLogEventAuths());
        blocks.setChecked(ServiceConfigManager.getInstance().isLogEventBlocks());
        limits.setChecked(ServiceConfigManager.getInstance().isLogEventLimits());

        new AlertDialog.Builder(context)
                .setTitle("Ustawienia")
                .setView(view)
                .setNegativeButton("Zamknij", null)
                .setPositiveButton("Zapisz", (d , i) -> {
                    ServiceConfigManager.getInstance().setLogEventAuths(auths.isChecked());
                    ServiceConfigManager.getInstance().setLogEventLimits(limits.isChecked());
                    ServiceConfigManager.getInstance().setLogEventBlocks(blocks.isChecked());

                    presenter.updateConfigInDatabase(context , ConfigUtil.KnownKeys.KK_LOG_AUTHS.getKeyName() , auths.isChecked() ? "1" : "0");
                    presenter.updateConfigInDatabase(context , ConfigUtil.KnownKeys.KK_LOG_BLOCKING.getKeyName() , blocks.isChecked() ? "1" : "0");
                    presenter.updateConfigInDatabase(context , ConfigUtil.KnownKeys.KK_LOG_LIMITS.getKeyName() , limits.isChecked() ? "1" : "0");
                }).create().show();
    }

    private void createDeleteDialog() {
        Context context = requireContext();
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_journal_delete, null);

        RadioButton all = view.findViewById(R.id.dialog_journal_delete_all);
        RadioButton notTodays = view.findViewById(R.id.dialog_journal_delete_others_than_today);
        RadioButton olderThanWeek = view.findViewById(R.id.dialog_journal_delete_older_than_7_days);
        RadioButton olderThanMonth = view.findViewById(R.id.dialog_journal_delete_older_than_30_days);

        new AlertDialog.Builder(context)
                .setTitle("Ustawienia")
                .setView(view)
                .setNegativeButton("Zamknij", null)
                .setPositiveButton("Zapisz", (d , i) -> {

                    if (all.isChecked()) {
                        presenter.deleteALl();
                    } else if (notTodays.isSelected()) {
                        presenter.deleteOlderThan(
                                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                        );
                    } else if (olderThanWeek.isSelected()) {
                        presenter.deleteOlderThan(
                                LocalDateTime.now().minus(7 , ChronoUnit.DAYS)
                                        .toEpochSecond(ZoneOffset.UTC)
                        );
                    } else if (olderThanMonth.isSelected()) {
                        presenter.deleteOlderThan(
                                LocalDateTime.now().minus(30 , ChronoUnit.DAYS)
                                        .toEpochSecond(ZoneOffset.UTC));
                    }

                }).create().show();
    }

    private String formatUsedDate() {
        return String.format(getString(R.string.date_format) ,
                usedDateStamp.getDayOfMonth() , usedDateStamp.getMonthValue() , usedDateStamp.getYear());
    }

    @Override
    public void onViewReady() {
        switchView.setChecked(ServiceConfigManager.getInstance().isLogEnabled());
        textCalendarDate.setText(formatUsedDate());
        this.onLoadingLogs();
    }

    @Override
    public void onLoadingLogs() {
        long[] range = ConfigUtil.getDayRangrFromMili(usedDateStamp);
        eventDataList.clear();
        eventDataList.addAll(presenter.getAllBetween(range[0] , range[1]));
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoggingEnabled() {
        boolean state = ServiceConfigManager.getInstance().isLogEnabled();
        ServiceConfigManager.getInstance().setLogEnabled(!state);
        presenter.updateConfigEnabledInDatabase(requireContext(), !state);
        switchView.setChecked(!state);
    }

    @Override
    public void onClickPreviousPage() {
        usedDateStamp = usedDateStamp.minus(1, ChronoUnit.DAYS);
        textCalendarDate.setText(formatUsedDate());
        this.onLoadingLogs();
    }

    @Override
    public void onClickPreviousnext() {
        usedDateStamp = usedDateStamp.plus(1, ChronoUnit.DAYS);
        textCalendarDate.setText(formatUsedDate());
        this.onLoadingLogs();
    }

    @Override
    public void onClickCalendar(LocalDateTime dateTime) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(requireContext(),
                (dialog , y , m , d) -> {
                    usedDateStamp = LocalDateTime.of(y, m, d, 0, 0);
                    textCalendarDate.setText(String.format(getString(R.string.date_format) ,
                            usedDateStamp.getDayOfMonth() , usedDateStamp.getMonthValue() , usedDateStamp.getYear()));
                    this.onLoadingLogs();
                } , year , month , day);

        dateDialog.show();
    }

    @Override
    public void onClickDelete() {
        createDeleteDialog();
    }

    @Override
    public void onClickOptions() {
        createSelectDialog();
    }
}
