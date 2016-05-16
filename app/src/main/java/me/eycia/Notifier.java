package me.eycia;

import java.util.ArrayList;

/**
 * Created by eycia on 16/5/12.
 */
public class Notifier {
    private final ArrayList<OnDataChangeListener> onDataChangeListeners = new ArrayList<>();

    public interface OnDataChangeListener {
        void OnDataChange();
    }

    public void addOnDataChangeListener(OnDataChangeListener listener) {
        synchronized (onDataChangeListeners) {
            if (!onDataChangeListeners.contains(listener)) {
                onDataChangeListeners.add(listener);
            }
        }
    }

    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        synchronized (onDataChangeListeners) {
            if (onDataChangeListeners.contains(listener)) {
                onDataChangeListeners.remove(listener);
            }
        }
    }

    public void ChangeData() {
        for (OnDataChangeListener listener : onDataChangeListeners) {
            listener.OnDataChange();
        }
    }
}
