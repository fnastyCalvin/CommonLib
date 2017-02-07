package com.example.calvin.myapplication.common.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用适配器 for RecyclerView
 * @author jiangtao
 * @version 1.0
 */
public class SuperRecyclerAdapter<T> extends RecyclerView.Adapter<AbsRecyclerViewHolder> {

	protected Context mContext;
	protected List<T> mData;
	protected int mLayoutRes;
	protected Class<? extends AbsRecyclerViewHolder> mViewHolderClass;

	private OnItemClickListener<T> mOnItemClickListener;
	private OnItemLongClickListener<T> mOnItemLongClickListener;

	public SuperRecyclerAdapter(Context context, List<T> data,@LayoutRes int layoutRes,Class viewHolderClass) {
		mContext = context;
		mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data); ;
		mLayoutRes = layoutRes;
		mViewHolderClass = viewHolderClass;
	}

	@Override
	public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view =View.inflate(parent.getContext(),mLayoutRes,null);
		return createViewHolder(view,mViewHolderClass);
	}

	@Override
	public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
		holder.setViewHolderData(position, mData.get(position));
		setItemClickListener(holder);
		setItemLongClickListener(holder);
	}

	private void setItemClickListener(AbsRecyclerViewHolder holder) {
		if(mOnItemClickListener != null){
			View view = holder.itemView;
			final int pos = holder.getAdapterPosition();
//			final int pos = holder.getPosition();
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickListener.onItemClick(v,mData.get(pos),pos);
				}
			});
		}
	}

	private void setItemLongClickListener(AbsRecyclerViewHolder holder) {
		if(mOnItemLongClickListener != null){
			View view = holder.itemView;
			final int pos = holder.getAdapterPosition();
//			final int pos = holder.getPosition();
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					mOnItemLongClickListener.onLongItemClick(v,mData.get(pos),pos);
					return true;
				}
			});
		}
	}

	public AbsRecyclerViewHolder createViewHolder(View view, Class<? extends AbsRecyclerViewHolder> viewHolderClass) {
		try {
			Constructor<? extends AbsRecyclerViewHolder> constructor = viewHolderClass.getConstructor(View.class);
			return constructor.newInstance(view);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getItemCount() {
		if(mData != null && !mData.isEmpty())
			return mData.size();
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}


	public void add(T item) {
		mData.add(item);
		notifyItemInserted(mData.indexOf(item));
	}

	public void addAll(List<T> item) {
		if(item == null || item.isEmpty()) return;
		mData.addAll(item);
		notifyItemRangeChanged(mData.indexOf(item.get(0)), item.size());
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
			notifyItemRemoved(index);
		}
	}

	public void remove(int index) {
		if(index < 0 || index > mData.size()) throw new IllegalArgumentException("wrong index");
		mData.remove(index);
		notifyItemRemoved(index);
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

	public Context getContext() {
		return mContext;
	}

	public List<T> getData() {
		return mData;
	}

	public void setOnItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener<T> mOnItemLongClickListener) {
		this.mOnItemLongClickListener = mOnItemLongClickListener;
	}

	public interface OnItemClickListener<T> {
		void onItemClick(View view, T item, int position);
	}

	public interface OnItemLongClickListener<T> {
		void onLongItemClick(View view, T item,int position);
	}
}
