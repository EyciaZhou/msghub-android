package me.eycia

import java.util.*

/**
 * Created by eycia on 16/5/12.
 */
class Notifier {
    private val onDataChangeListeners = ArrayList<() -> Unit>()

    fun addOnDataChangeListener(listener: () -> Unit) {
        synchronized (onDataChangeListeners) {
            if (!onDataChangeListeners.contains(listener)) {
                onDataChangeListeners.add(listener)
            }
        }
    }

    fun removeOnDataChangeListener(listener: () -> Unit) {
        synchronized (onDataChangeListeners) {
            if (onDataChangeListeners.contains(listener)) {
                onDataChangeListeners.remove(listener)
            }
        }
    }

    fun ChangeData() {
        for (listener in onDataChangeListeners) {
            listener()
        }
    }
}
