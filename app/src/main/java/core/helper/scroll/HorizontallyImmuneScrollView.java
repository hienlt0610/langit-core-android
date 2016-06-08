package core.helper.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * <b>Class Overview</b>
 * <p/>
 * A generic, customizable Android ScrollView implementation that allows the
 * nesting horizontal scrolling views.
 * <p/>
 * This ScrollView can be used in place of the normal Android
 * android.widget.ScrollView class.
 * <p/>
 *
 * @author Tyrael
 * @version 1.0
 * @since October 2014
 */
public class HorizontallyImmuneScrollView extends ScrollView {

    private float xDistance, yDistance, lastX, lastY;

    public HorizontallyImmuneScrollView(Context context, AttributeSet attrs,
                                        int defStyle) {
        super(context, attrs, defStyle);
    }

    public HorizontallyImmuneScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontallyImmuneScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if (xDistance > yDistance)
                    return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

}
