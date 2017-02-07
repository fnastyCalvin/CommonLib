package com.example.calvin.myapplication.common.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * 通用ViewHolder for RecyclerView
 * Created by jiangtao on 2015/5/22.
 * @author jiangtao
 * @version 1.0
 */
public abstract class AbsRecyclerViewHolder<T> extends RecyclerView.ViewHolder{

    protected View itemView;

    public AbsRecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    /**
     * when {@link SuperRecyclerAdapter.onBindViewHolder} called , set viewholder data
     * @param position
     * @param itemData
     */
    protected abstract void setViewHolderData(int position, T itemData);

    public Context getContext() {
        return itemView.getContext();
    }

    public <K extends View> K getViewFromHolder( int id) {
        return ViewHolderHelper.getView(itemView, id);
    }

    public <K extends View> K getViewFromHolder(int id, int width, int height) {
        return ViewHolderHelper.getView(itemView, id, width, height);
    }
}
