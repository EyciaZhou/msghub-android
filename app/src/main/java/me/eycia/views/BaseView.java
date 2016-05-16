package me.eycia.views;

import me.eycia.api.MsgLine;

/**
 * Created by eycia on 16/5/11.
 */
public interface BaseView {
    int ViewType();

    void UpdateInfo(MsgLine msgLine);
}
