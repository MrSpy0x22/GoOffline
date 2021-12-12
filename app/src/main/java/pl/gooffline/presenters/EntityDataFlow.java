package pl.gooffline.presenters;

import java.util.List;

public interface EntityDataFlow<T> {
    void pullData();
    void pushData();
    void pushData(List<T> dataList);
}
