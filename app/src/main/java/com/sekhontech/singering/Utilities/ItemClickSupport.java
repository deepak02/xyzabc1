package com.sekhontech.singering.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sekhontech.singering.R;


public class ItemClickSupport
    {

    private final RecyclerView mRecyclerView3;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {

                RecyclerView.ViewHolder FeedListRowHolder1 = mRecyclerView3.getChildViewHolder(v);

                mOnItemClickListener.onItemClicked(mRecyclerView3, FeedListRowHolder1.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder FeedListRowHolder1 = mRecyclerView3.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView3, FeedListRowHolder1.getAdapterPosition(), v);
            }
            return false;
        }
    };


    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                                                = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListener != null)
            {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null)
            {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView3 = recyclerView;
        mRecyclerView3.setTag(R.id.item_click_support, this);
        mRecyclerView3.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }



}
