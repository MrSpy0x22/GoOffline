package pl.gooffline.fragments;

import android.app.usage.UsageStats;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.gooffline.R;
import pl.gooffline.utils.UsageStatsUtil;

public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Map<Integer , List<UsageStats>> usageData = UsageStatsUtil.getDailyStats(requireContext());
        List<DataPoint> dataPoints = UsageStatsUtil.convertStatsMapToDataPoints(usageData)
                .stream()
                .map(dp -> new DataPoint(dp.getX() , dp.getY() / 60))
                .collect(Collectors.toList());

        LiveChart chart = view.findViewById(R.id.live_chart);
        Dataset dataset = new Dataset(dataPoints);
        chart.setDataset(dataset).drawYBounds().drawBaseline().drawFill(true).drawSmoothPath().drawDataset();
    }
}
