package me.eycia.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.eycia.api.MsgLine;

/**
 * Created by eycia on 16/5/11.
 */
public abstract class BaseView extends RecyclerView.ViewHolder {
    public BaseView(View itemView) {
        super(itemView);
    }

    abstract public int ViewType();

    abstract public void UpdateInfo(MsgLine msgLine);
}
