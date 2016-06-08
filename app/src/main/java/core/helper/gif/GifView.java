package core.helper.gif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class GifView extends View {

    private Movie movie;
    private InputStream in;
    private long duration;
    private long start;
    private int width;
    private int height;


    public GifView(Context context) {
        super(context);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setClickable(false);
//        in = context.getResources().openRawResource(R.raw.loading_2);
        movie = Movie.decodeStream(in);
        width = movie.width();
        height = movie.height();
        duration = movie.duration();
    }

    public void setGif(InputStream in) {
        if (in != null)
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        this.in = in;
        start = 0;
        movie = Movie.decodeStream(in);
        width = movie.width();
        height = movie.height();
        duration = movie.duration();
    }

    public int getGifWidth() {
        return width;
    }

    public int getGifHeight() {
        return height;
    }

    public long getGifDuration() {
        return duration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();

        if (start == 0)
            start = now;

        if (movie != null) {

            if (duration == 0)
                duration = 1000;

            final int relTime = (int) ((now - start) % duration);
            movie.setTime(relTime);
            movie.draw(canvas, 0, 0);
            invalidate();
        }
    }
}
