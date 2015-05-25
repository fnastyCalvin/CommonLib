package com.example.calvin.myapplication.test;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calvin.myapplication.R;
import com.example.calvin.myapplication.common.base.AbsRecyclerViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BookViewHolder extends AbsRecyclerViewHolder<Book> {

    public BookViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setViewHolderData(int position, Book itemData) {
        TextView textView1 = getViewFromHolder(R.id.author_textview);
        TextView textView2 = getViewFromHolder(R.id.title_textview);
        ImageView textView3 = getViewFromHolder(R.id.summary_textview);
        textView1.setText(itemData.getTitle());
        textView2.setText(itemData.getAuthor());
//        textView3.setText(itemData.getSummary());
        ImageLoader.getInstance().displayImage(itemData.getSummary(),textView3);

    }


}
