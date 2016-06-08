package core.helper.fetchable.recycle;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import core.util.SingleClick;
import core.util.SingleTouch;


public class RecycleAdapter<T> extends RecyclerView.Adapter implements SingleClick.SingleClickListener {

    public static final int HEADER_TYPE = 1;
    public static final int NORMAL_TYPE = 2;

    private RecycleInterface.OnItemClickListener<T> listener;
    private ArrayList<T> items;
    private SingleClick singleClick;
    private SingleTouch singleTouch;
    private LayoutInflater inflater;

    public RecycleAdapter(LayoutInflater inflater, ArrayList<T> items, RecycleInterface.OnItemClickListener<T> listener, SingleTouch singleTouch) {
        this.inflater = inflater;
        this.listener = listener;
        this.items = items;
        this.singleClick = new SingleClick();
        this.singleClick.setListener(this);
        this.singleTouch = singleTouch;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 5 == 0) ? HEADER_TYPE : NORMAL_TYPE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder view = null;
        switch (viewType) {
            case HEADER_TYPE:
                view = new HeaderViewHolder<String>(inflater.inflate(0, parent, false));
                break;
            case NORMAL_TYPE:
                view = new NormalViewHolder<String>(inflater.inflate(0, parent, false));
                break;
        }
        if (view != null && view.itemView != null) {
            view.setIsRecyclable(true);
            view.itemView.setOnClickListener(singleClick);
            view.itemView.setOnTouchListener(singleTouch);
            view.itemView.setTag(view);
        }
        return view;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder != null) {
            switch (holder.getItemViewType()) {
                case HEADER_TYPE:
                    // bind header
                    HeaderViewHolder header = (HeaderViewHolder) holder;
                    header.itemView.setBackgroundColor(Color.RED);
                    header.setData(items.get(position));
                    break;
                case NORMAL_TYPE:
                    // bind normal
                    NormalViewHolder normal = (NormalViewHolder) holder;
                    normal.itemView.setBackgroundColor(Color.GREEN);
                    normal.setData(items.get(position));
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onSingleClick(View v) {
        Object holder = v.getTag();
        if (holder instanceof HeaderViewHolder) {
            listener.onItemClick(v, (T) ((HeaderViewHolder) holder).getData(), ((HeaderViewHolder) holder).getAdapterPosition(), HEADER_TYPE);
        } else if (holder instanceof NormalViewHolder) {
            listener.onItemClick(v, (T) ((NormalViewHolder) holder).getData(), ((NormalViewHolder) holder).getAdapterPosition(), HEADER_TYPE);
        }
    }

    private class HeaderViewHolder<T> extends RecyclerView.ViewHolder {

        private T data;

        public HeaderViewHolder(View view) {
            super(view);
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public View findViewById(int id) {
            return itemView.findViewById(id);
        }
    }

    private class NormalViewHolder<T> extends RecyclerView.ViewHolder {

        private T data;

        public NormalViewHolder(View view) {
            super(view);
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public View findViewById(int id) {
            return itemView.findViewById(id);
        }
    }
}
