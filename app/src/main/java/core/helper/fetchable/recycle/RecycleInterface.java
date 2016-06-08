package core.helper.fetchable.recycle;

import android.view.View;

public class RecycleInterface {

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position, int type);
    }

}
