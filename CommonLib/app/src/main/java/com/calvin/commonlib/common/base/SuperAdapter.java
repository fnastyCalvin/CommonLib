package com.calvin.commonlib.common.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用适配器 抽象类 for ListView GridView
 * @author jiangtao
 * @version 1.0
 */
public abstract class SuperAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mData;
	protected int mLayoutRes;
	protected AbsListView mView;//如listView,gridView


	public SuperAdapter(Context context, List<T> data, @LayoutRes int layoutRes) {
		this(context, data, layoutRes,null);
	}

	public SuperAdapter(Context context, List<T> data, @LayoutRes int layoutRes, AbsListView view) {
		mContext = context;
		mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data); ;
		mLayoutRes = layoutRes;
		mView = view;
	}

	@Override
	public int getCount() {
		if(mData != null && !mData.isEmpty())
			return mData.size();
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (position >= mData.size()) return null;
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, mLayoutRes, null);
		}

		bindView(position, convertView, getItem(position));

		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		return position < mData.size();
	}

	protected abstract void bindView(int pos, View convertView, T itemData);

	/**
	 * 获取需要单独更新的某个item
	 * @param position
	 * @return 需要更新的convertView
	 */
	public View updateView(int position){
		if(position < 0) throw new IllegalArgumentException("position need > 0");
		int visiblePosition = mView.getFirstVisiblePosition();
		if(position - visiblePosition < 0){
			throw new IllegalArgumentException("position not proper correct");
		}
		View containerView = mView.getChildAt(position - visiblePosition);
		if(containerView != null){
			return containerView;
		}
		return mView;
	}

	public void add(T item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addAll(List<T> item) {
		mData.addAll(item);
		notifyDataSetChanged();
	}

	/**replace**/
	public void set(T oldItem, T newItem) {
		set(mData.indexOf(oldItem), newItem);
	}

	/**replace**/
	public void set(int index, T item) {
		mData.set(index, item);
		notifyDataSetChanged();
	}

	public void remove(T item) {
		int index = mData.indexOf(item);
		if(index > -1) {
			mData.remove(item);
			notifyDataSetChanged();
		}
	}

	public void remove(int index) {
		if(index < 0 || index > mData.size()) throw new IllegalArgumentException("wrong index");
		mData.remove(index);
		notifyDataSetChanged();
	}

	public void replaceAll(List<T> newList) {
		mData.clear();
		mData.addAll(newList);
		notifyDataSetChanged();
	}

	public boolean contains(T item) {
		return mData.contains(item);
	}

	/** Clear data list */
	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	public <K extends View> K getViewFromHolder(View convertView, int id) {
		return getView(convertView, id);
	}

	public Context getContext() {
		return mContext;
	}

	public List<T> getData() {
		return mData;
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T getView(View convertView, int id) {

		SparseArray<View> holder = (SparseArray<View>) convertView.getTag();
		if (holder == null) {
			holder = new SparseArray<View>();
			convertView.setTag(holder);
		}

		View view = holder.get(id);
		if (view == null) {
			view = convertView.findViewById(id);
			holder.put(id, view);
		}
		return (T) view;
	}

}
