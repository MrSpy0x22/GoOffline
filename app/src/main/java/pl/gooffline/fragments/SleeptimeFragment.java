package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import pl.gooffline.R;
import pl.gooffline.presenters.SleeptimePresenter;
import pl.gooffline.utils.ConfigUtil;

public class SleeptimeFragment extends Fragment implements SleeptimePresenter.View {
    private SleeptimePresenter presenter;
    private SwitchMaterial sleeptimeEnableSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sleeptime, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new SleeptimePresenter(requireContext());

        LinearLayout layoutRowEnableSleeptime = view.findViewById(R.id.sleeptime_row_enable);
        layoutRowEnableSleeptime.setOnClickListener(
                e -> this.onSleeptimeStateChanged(sleeptimeEnableSwitch.isChecked())
        );

        sleeptimeEnableSwitch = view.findViewById(R.id.sleeptime_row_enable_switch);
        sleeptimeEnableSwitch.setChecked(
                presenter.getConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE).equals("true")
        );

        Float start = Float.parseFloat(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_START));
        Float stop = Float.parseFloat(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP));
        RangeSlider rangeSlider = view.findViewById(R.id.slider_sleeptime);
        rangeSlider.setValues(start , stop);
        rangeSlider.setLabelFormatter(value -> String.format(getString(R.string.fragment_sleeptime_range_tooltip) , value));
        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                int start = Math.round(values.get(0));
                int stop = Math.round(values.get(1));
                onSleeptimeRangeChanged(start , stop);
            }
        });
    }

    @Override
    public void onSleeptimeStateChanged(boolean state) {
        boolean newState = !state;
        sleeptimeEnableSwitch.setChecked(newState);
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE , newState ? "true" : "false");
    }

    @Override
    public void onSleeptimeRangeChanged(int start, int stop) {
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_START , String.valueOf(start));
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP , String.valueOf(stop));
    }
}
