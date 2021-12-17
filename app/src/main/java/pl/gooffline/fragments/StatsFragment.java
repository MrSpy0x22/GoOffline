package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.gooffline.R;

public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        LiveChart chart = view.findViewById(R.id.live_chart);
//        Dataset dataset = new Dataset(Arrays.asList(new DataPoint(0f , 2f) , new DataPoint(1f , 3f) , new DataPoint(2f , 6f)));
//        chart.setDataset(dataset).drawYBounds().drawBaseline().drawFill(true).drawDataset();
    }
}
