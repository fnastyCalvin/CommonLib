package com.calvin.commonlib.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义listview
 * 功能:
 * 自适应内容高度
 * Created by apple on 2/25/15.
 */
public class AutoListView extends ListView {

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	  * 设置不滚动
	  */
	 public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	 {
	  int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
	  super.onMeasure(widthMeasureSpec, expandSpec);
	 }
}
