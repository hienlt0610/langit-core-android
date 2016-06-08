package core.helper.fetchable.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonframe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.helper.fetchable.list.FetchableInterface.OnLoadMoreListener;
import core.helper.fetchable.list.FetchableInterface.OnRefreshListener;
import core.helper.fetchable.list.FetchableInterface.State;

/**
 * <b>Class Overview</b>
 * <p/>
 * A generic, customizable Android ExpandableListView implementation that has
 * 'Pull to Refresh' and 'Load more' functionalities.
 * <p/>
 * This ExpandableListView can be used in place of the normal Android
 * android.widget.ExpandableListView class.
 * <p/>
 * Users of this class should implement <b>OnRefreshListener</b> and call
 * <b>setOnRefreshListener(..)</b> to get notified on refresh events. The using
 * class should call onRefreshComplete() when refreshing is finished. Users
 * should also use shouldOverrideLoadMore(..) to indicate whether the 'load
 * more' event should be overridden by 'refresh' event while both events are
 * fired in parallel.
 * <p/>
 * Users of this class should implement <b>OnLoadMoreListener</b> and call
 * <b>setOnLoadMoreListener(..)</b> to get notified on load more events. The
 * using class should call onLoadMoreComplete() when load more is finished.
 * Users should also use shouldOverrideRefresh(..) to indicate whether the
 * 'refresh' event should be overridden by 'load more' event while both events
 * are fired in parallel.
 * <p/>
 * The using class can call setRefreshing() to set the state explicitly to
 * refreshing. This is useful when you want to show the spinner and 'Refreshing'
 * text when the refresh was not triggered by 'Pull to Refresh', for example on
 * start.
 * <p/>
 *
 * @author Tyrael
 * @version 1.0
 * @since April 2014
 */

@SuppressLint("SimpleDateFormat")
public class FetchableExpandableListView extends ExpandableListView implements
        OnScrollListener {

    /**
     * The duration of the expand/collapse animations
     */
    private static final int ANIMATION_DURATION = 150;
    private static final float PULL_RESISTANCE = 1.7f;
    private static final int ROTATE_ARROW_ANIMATION_DURATION = 150;

	/* HEADER VARIABLES */
    /**
     * The measured height of the height used for showing refresh view
     */
    private static int measuredHeaderHeight;
    /**
     * Number of idle pixel of the pull to refresh functionality
     */
    private final int IDLE_DISTANCE = 5;
    /**
     * The boolean value to indicate the 'pull to refresh' functionality
     */
    private boolean isRefreshable;
    /**
     * The boolean value to indicate list view can be scrolled while refreshing
     */
    private boolean lockScrollWhileRefreshing;
    /**
     * The boolean value to indicate the refresh view should show last update
     * time
     */
    private boolean showLastUpdatedText;
    /**
     * Pull to refresh text
     */
    private String pullToRefreshText;
    /**
     * Release to refresh text
     */
    private String releaseToRefreshText;
    /**
     * Refreshing text
     */
    private String refreshingText;
    /**
     * Last update text
     */
    private String lastUpdatedText;
    /**
     * Last update time format
     */
    private SimpleDateFormat lastUpdatedDateFormat = new SimpleDateFormat(
            "dd/MM HH:mm");
    /**
     * Previous y coordination used for pull to refresh functionality
     */
    private float previousY;
    /**
     * Header padding value used for pull to refresh functionality
     */
    private int headerPadding;
    /**
     * The boolean value to indicate that the header has been reset or not, used
     * in item click
     */
    private boolean hasResetHeader;
    /**
     * Time value of the last update
     */
    private long lastUpdated = -1;
    /**
     * The refreshing state used for pull to refresh functionality
     */
    private State state;
    /**
     * The header container for calculating the padding and applying to UI
     */
    private LinearLayout headerContainer;
    /**
     * The header layout, contains the arrow animation, status text
     */
    private RelativeLayout header;
    /**
     * The arrow rotating animation
     */
    private RotateAnimation flipAnimation;
    /**
     * The arrow rotating back animation
     */
    private RotateAnimation reverseFlipAnimation;
    /**
     * Start y coordination used for pull to refresh functionality
     */
    private float mScrollStartY;
    /**
     * The refreshing image view
     */
    private ImageView image;

    /**
     * The progress bar of the refreshing process
     */
    private ProgressBar spinner;

    /**
     * The text view displaying status of the refreshing process
     */
    private TextView textStatus;

    /**
     * The text view displaying last update time
     */
    private TextView lastUpdatedTextView;

    /**
     * The default on item click to be customized (removed the header click and
     * footer click)
     */
    private OnItemClickListener onItemClickListener;

    /**
     * The default on item long click to be customized (removed the header click
     * and footer click)
     */
    private OnItemLongClickListener onItemLongClickListener;

    /**
     * The pull to refresh listener
     */
    private OnRefreshListener onRefreshListener;
    /* END HEADER VARIABLES */

	/* FOOTER VARIABLES */

    /**
     * The local limit of items for this list view (if the limit is reached, no
     * loading more will be operate). Default is -1 means NO LIMIT
     */
    private int limit = -1;

    /**
     * The reference to the adapter of this list view, used to calculate the
     * height of all items to change the load more footer
     */
    private AnimatedExpandableListAdapter adapter;

    /**
     * The boolean value to indicate the 'load more' functionality
     */
    private boolean isFetchable;

    /**
     * The footer view reference
     */
    private FrameLayout mFooterView;

    /**
     * The load more event listener
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * The scroll listener to implement the load more functionality
     */
    private OnScrollListener mOnScrollListener;

    /**
     * The boolean value to indicate that the list is in loading more state
     */
    private boolean mIsLoadingMore;

    /**
     * The current scroll state used for the load more functionality
     */
    private int mCurrentScrollState;

    /**
     * The boolean value to indicate that the footer is removed from the list
     * view (in case of no more data)
     */
    private boolean isFooterRemoved = false;

    /**
     * The visible height of the list view
     */
    private int mHeight;

    /**
     * The total height of all items used to indicate showing footer at the
     * first time set adapter (only once)
     */
    private int totalItemsHeight;
    /* END FOOTER VARIABLES */

    /**
     * The disabler of the header and foot click function
     */

    private OnTouchListener ignoreTouch = new OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return true;
        }
    };

    public FetchableExpandableListView(Context context) {
        super(context);
        init(context);
    }

    public FetchableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
        init(context);
    }

    public FetchableExpandableListView(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
        init(context);
    }

	/* GENERAL */

    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray attrsArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FetchableView, 0, defStyle);
        try {
            isFetchable = attrsArray.getBoolean(
                    R.styleable.FetchableView_isFetchable, true);
            isRefreshable = attrsArray.getBoolean(
                    R.styleable.FetchableView_isRefreshable, true);
            limit = attrsArray.getInteger(R.styleable.FetchableView_limit, -1);
            setCacheColorHint(Color.TRANSPARENT);
            setDivider(null);
            setDividerHeight(0);
        } finally {
            attrsArray.recycle();
        }
    }

    private void init(Context context) {
        setVerticalFadingEdgeEnabled(false);
        initHeader();
        initFooter();
    }

    private void updateTotalItemsHeight() {
        if (adapter != null) {
            totalItemsHeight = 0;
            for (int i = 0; i < adapter.getGroupCount(); ++i) {
                View item = adapter.getGroupView(i, false, null, this);
                if (item != null) {
                    item.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                    item.measure(MeasureSpec.makeMeasureSpec(0,
                            MeasureSpec.UNSPECIFIED), MeasureSpec
                            .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    totalItemsHeight += item.getMeasuredHeight();
                }
            }
        }
    }

    public void setAdapter(AnimatedExpandableListAdapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);
        // Make sure that the adapter extends AnimatedExpandableListAdapter
        if (adapter instanceof AnimatedExpandableListAdapter) {
            this.adapter = adapter;
            this.adapter.setParent(this);
            updateTotalItemsHeight();
        } else {
            throw new ClassCastException(adapter.toString()
                    + " must implement AnimatedExpandableListAdapter");
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // To prevent random IndexOutOfBoundsException
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {

        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void setOnItemLongClickListener(
            OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

	/* END GENERAL */

	/* HEADER */

    @SuppressLint("InflateParams")
    private void initHeader() {
        if (!isRefreshable)
            return;

        headerContainer = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.fetchable_header, null);
        headerContainer.setOnTouchListener(ignoreTouch);
        header = (RelativeLayout) headerContainer
                .findViewById(R.id.fetchable_id_header);
        textStatus = (TextView) header.findViewById(R.id.fetchable_id_text);
        lastUpdatedTextView = (TextView) header
                .findViewById(R.id.fetchable_id_last_updated);
        image = (ImageView) header.findViewById(R.id.fetchable_id_image);
        spinner = (ProgressBar) header.findViewById(R.id.fetchable_id_spinner);

        pullToRefreshText = getContext().getString(
                R.string.fetchable_pull_to_refresh);
        releaseToRefreshText = getContext().getString(
                R.string.fetchable_release_to_refresh);
        refreshingText = getContext().getString(R.string.fetchable_refreshing);
        lastUpdatedText = getContext().getString(
                R.string.fetchable_last_updated);

        flipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        flipAnimation.setInterpolator(new LinearInterpolator());
        flipAnimation.setDuration(ROTATE_ARROW_ANIMATION_DURATION);
        flipAnimation.setFillAfter(true);

        reverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseFlipAnimation.setInterpolator(new LinearInterpolator());
        reverseFlipAnimation.setDuration(ROTATE_ARROW_ANIMATION_DURATION);
        reverseFlipAnimation.setFillAfter(true);

        addHeaderView(headerContainer);
        setState(State.PULL_TO_REFRESH);

        ViewTreeObserver vto = header.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new PTROnGlobalLayoutListener());

        super.setOnItemClickListener(new PTROnItemClickListener());
        super.setOnItemLongClickListener(new PTROnItemLongClickListener());
    }

    private void setHeaderPadding(int padding) {
        headerPadding = padding;

        MarginLayoutParams mlp = (MarginLayoutParams) header
                .getLayoutParams();
        mlp.setMargins(0, Math.round(padding), 0, 0);
        header.setLayoutParams(mlp);
    }

    private void resetHeader() {
        setHeaderPadding(-header.getHeight());
        setState(State.PULL_TO_REFRESH);
    }

    private void setUiRefreshing() {
        spinner.setVisibility(View.VISIBLE);
        image.clearAnimation();
        image.setVisibility(View.INVISIBLE);
        textStatus.setText(refreshingText);
    }

    private void setState(State state) {
        this.state = state;
        switch (state) {
            case PULL_TO_REFRESH:
                spinner.setVisibility(View.INVISIBLE);
                image.setVisibility(View.VISIBLE);
                textStatus.setText(pullToRefreshText);

                if (showLastUpdatedText && lastUpdated != -1) {
                    lastUpdatedTextView.setVisibility(View.VISIBLE);
                    lastUpdatedTextView.setText(String.format(lastUpdatedText,
                            lastUpdatedDateFormat.format(new Date(lastUpdated))));
                }

                break;

            case RELEASE_TO_REFRESH:
                spinner.setVisibility(View.INVISIBLE);
                image.setVisibility(View.VISIBLE);
                textStatus.setText(releaseToRefreshText);
                break;

            case REFRESHING:
                setUiRefreshing();

                lastUpdated = System.currentTimeMillis();
                if (onRefreshListener == null) {
                    setState(State.PULL_TO_REFRESH);
                } else {
                    if (mIsLoadingMore) {
                        if (onRefreshListener.shouldOverrideLoadMore()) {
                            onLoadMoreComplete();
                            onRefreshListener.onRefresh();
                        } else {
                            // onRefreshComplete();
                            onRefreshOverriden();
                        }

                    } else
                        onRefreshListener.onRefresh();
                }

                break;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!hasResetHeader) {
            if (measuredHeaderHeight > 0 && state != State.REFRESHING) {
                setHeaderPadding(-measuredHeaderHeight);
            }

            hasResetHeader = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
        if (mOnLoadMoreListener != null) {
            if (visibleItemCount == totalItemCount) {
                return;
            }

            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
            if (!mIsLoadingMore && loadMore
                    && mCurrentScrollState != SCROLL_STATE_IDLE) {
                if (state == State.REFRESHING) {
                    if (mOnLoadMoreListener.shouldOverrideRefresh()) {
                        // onRefreshComplete();
                        onRefreshOverriden();
                        onLoadMore();
                    } else
                        onLoadMoreComplete();
                } else {
                    onLoadMore();
                }

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isRefreshable)
            return super.onTouchEvent(event);

        if (lockScrollWhileRefreshing && state == State.REFRESHING) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getFirstVisiblePosition() == 0) {
                    previousY = event.getY();
                } else {
                    previousY = -1;
                }

                // Remember where have we started
                mScrollStartY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (previousY != -1
                        && (state == State.RELEASE_TO_REFRESH || getFirstVisiblePosition() == 0)) {
                    switch (state) {
                        case RELEASE_TO_REFRESH:
                            setState(State.REFRESHING);
                            break;
                        case PULL_TO_REFRESH:
                            resetHeader();
                            break;
                        default:
                            break;
                    }
                } else if (previousY == -1 && state == State.PULL_TO_REFRESH
                        && getFirstVisiblePosition() == 0) {
                    resetHeader();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (previousY != -1 && getFirstVisiblePosition() == 0
                        && Math.abs(mScrollStartY - event.getY()) > IDLE_DISTANCE) {
                    float y = event.getY();
                    float diff = y - previousY;
                    if (diff > 0)
                        diff /= PULL_RESISTANCE;
                    previousY = y;

                    int newHeaderPadding = Math.max(
                            Math.round(headerPadding + diff), -header.getHeight());

                    if (newHeaderPadding != headerPadding
                            && state != State.REFRESHING) {
                        if (newHeaderPadding >= (measuredHeaderHeight / 4))
                            return true;

                        setHeaderPadding(newHeaderPadding);

                        if (state == State.PULL_TO_REFRESH && headerPadding > 0) {
                            setState(State.RELEASE_TO_REFRESH);

                            image.clearAnimation();
                            image.startAnimation(flipAnimation);

                        } else if (state == State.RELEASE_TO_REFRESH
                                && headerPadding < 0) {
                            setState(State.PULL_TO_REFRESH);

                            image.clearAnimation();
                            image.startAnimation(reverseFlipAnimation);
                        }
                    }
                }

                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Activate an OnRefreshListener to get notified on 'pull to refresh'
     * events.
     *
     * @param onRefreshListener The OnRefreshListener to get notified
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        if (!isRefreshable)
            return;
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * @return If the list is in 'Refreshing' state
     */
    public boolean isRefreshing() {
        return state == State.REFRESHING;
    }

    /**
     * Default is false. When lockScrollWhileRefreshing is set to true, the list
     * cannot scroll when in 'refreshing' mode. It's 'locked' on refreshing.
     *
     * @param lockScrollWhileRefreshing
     */
    public void setLockScrollWhileRefreshing(boolean lockScrollWhileRefreshing) {
        this.lockScrollWhileRefreshing = lockScrollWhileRefreshing;
    }

    /**
     * Default is false. Show the last-updated date/time in the 'Pull ro
     * Refresh' header. See 'setLastUpdatedDateFormat' to set the date/time
     * formatting.
     *
     * @param showLastUpdatedText
     */
    public void setShowLastUpdatedText(boolean showLastUpdatedText) {
        this.showLastUpdatedText = showLastUpdatedText;
        if (!showLastUpdatedText)
            lastUpdatedTextView.setVisibility(View.GONE);
    }

    /**
     * Default: "dd/MM HH:mm". Set the format in which the last-updated
     * date/time is shown. Meaningless if 'showLastUpdatedText == false
     * (default)'. See 'setShowLastUpdatedText'.
     *
     * @param lastUpdatedDateFormat
     */
    public void setLastUpdatedDateFormat(SimpleDateFormat lastUpdatedDateFormat) {
        this.lastUpdatedDateFormat = lastUpdatedDateFormat;
    }

    /**
     * Explicitly set the state to refreshing. This is useful when you want to
     * show the spinner and 'Refreshing' text when the refresh was not triggered
     * by 'pull to refresh', for example on start.
     */
    public void setRefreshing() {
        state = State.REFRESHING;
        scrollTo(0, 0);
        setUiRefreshing();
        setHeaderPadding(0);
    }

    private void onRefreshOverriden() {
        updateTotalItemsHeight();
        changeLoadingFooterState();
        state = State.PULL_TO_REFRESH;
        resetHeader();
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * Set the state back to 'pull to refresh'. Call this method when refreshing
     * the data is finished.
     */
    public void onRefreshComplete() {
        post(new Runnable() {

            @Override
            public void run() {
                setSelection(0);
                if (adapter != null) {
                    for (int i = 0; i < adapter.getGroupCount(); ++i) {
                        collapseGroup(i);
                    }
                }
            }
        });
        updateTotalItemsHeight();
        changeLoadingFooterState();
        state = State.PULL_TO_REFRESH;
        resetHeader();
        lastUpdated = System.currentTimeMillis();

    }

    /**
     * Change the label text on state 'Pull to Refresh'
     *
     * @param pullToRefreshText Text
     */
    public void setTextPullToRefresh(String pullToRefreshText) {
        this.pullToRefreshText = pullToRefreshText;
        if (state == State.PULL_TO_REFRESH) {
            textStatus.setText(pullToRefreshText);
        }
    }

    /**
     * Change the label text on state 'Release to Refresh'
     *
     * @param releaseToRefreshText Text
     */
    public void setTextReleaseToRefresh(String releaseToRefreshText) {
        this.releaseToRefreshText = releaseToRefreshText;
        if (state == State.RELEASE_TO_REFRESH) {
            textStatus.setText(releaseToRefreshText);
        }
    }

    /**
     * Change the label text on state 'Refreshing'
     *
     * @param refreshingText Text
     */
    public void setTextRefreshing(String refreshingText) {
        this.refreshingText = refreshingText;
        if (state == State.REFRESHING) {
            textStatus.setText(refreshingText);
        }
    }

	/* END HEADER */

    /* FOOTER */
    private void initFooter() {
        if (!isFetchable)
            return;

        mFooterView = (FrameLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.fetchable_footer, this, false);
        mFooterView.setOnTouchListener(ignoreTouch);
        addFooterView(mFooterView);
        super.setOnScrollListener(this);
    }

    /**
     * Method to change footer state as the first load, if the height of all
     * items less than the height of the list view itself, the loading more
     * footer should be invisible.
     */
    private void changeLoadingFooterState() {
        if (adapter != null) {
            if (adapter.getGroupCount() >= limit && limit != -1) {
                setFooterVisible(false);
                return;
            }
        }
        setFooterVisible(totalItemsHeight > mHeight);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (mOnScrollListener == null)
            mOnScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * Activate an OnLoadMoreListener to get notified on 'load more' events.
     *
     * @param onLoadMoreListener The OnLoadMoreListener to get notified
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        if (!isFetchable)
            return;
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void onLoadMore() {
        if (mOnLoadMoreListener != null && isFetchable && !isFooterRemoved) {
            mIsLoadingMore = true;
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * @return If the list is in 'Loading more' state
     */
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    /**
     * Call this method when loading more the data is finished.
     */
    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        changeLoadingFooterState();
    }

    /**
     * Method to indicate whether the loading more footer should be visible
     *
     * @param isVisible isVisible true if the loading more footer should be visible,
     *                  false otherwise.
     */
    @SuppressWarnings("deprecation")
    public void setFooterVisible(boolean isVisible) {
        if (!isFetchable)
            return;

        if (isVisible) {
            mFooterView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            mFooterView.getLayoutParams().width = FrameLayout.LayoutParams.FILL_PARENT;
            isFooterRemoved = false;
        } else {
            isFooterRemoved = true;
            mFooterView.getLayoutParams().height = 1;
            mFooterView.getLayoutParams().width = 1;
        }
        invalidate();
    }

    /**
     * @return If the loading more footer is removed.
     */
    public boolean isFooterRemoved() {
        return isFooterRemoved;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

	/* END FOOTER */

    /**
     * Expands the given group with an animation.
     *
     * @param groupPos The position of the group to expand
     * @return Returns true if the group was expanded. False if the group was
     * already expanded.
     */
    public boolean expandGroupWithAnimation(int groupPos) {
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if (childIndex < getChildCount()) {
                // Get the view for the group is it is on screen...
                View v = getChildAt(childIndex);
                if (v.getBottom() >= getBottom()) {
                    // If the user is not going to be able to see the animation
                    // we just expand the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen

                    // We need to notify the adapter that the group was expanded
                    // without it's knowledge
                    adapter.notifyGroupExpanded(groupPos);
                    return expandGroup(groupPos);
                }
            }
        }

        // Let the adapter know that we are starting the animation...
        adapter.startExpandAnimation(groupPos, 0);
        // Finally call expandGroup (note that expandGroup will call
        // notifyDataSetChanged so we don't need to)
        return expandGroup(groupPos);
    }

    /**
     * Collapses the given group with an animation.
     *
     * @param groupPos The position of the group to collapse
     * @return Returns true if the group was collapsed. False if the group was
     * already collapsed.
     */
    public boolean collapseGroupWithAnimation(int groupPos) {
        int groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos));
        if (groupFlatPos != -1) {
            int childIndex = groupFlatPos - getFirstVisiblePosition();
            if (childIndex >= 0 && childIndex < getChildCount()) {
                // Get the view for the group is it is on screen...
                View v = getChildAt(childIndex);
                if (v.getBottom() >= getBottom()) {
                    // If the user is not going to be able to see the animation
                    // we just collapse the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen
                    return collapseGroup(groupPos);
                }
            } else {
                // If the group is offscreen, we can just collapse it without an
                // animation...
                return collapseGroup(groupPos);
            }
        }

        // Get the position of the firstChild visible from the top of the screen
        long packedPos = getExpandableListPosition(getFirstVisiblePosition());
        int firstChildPos = getPackedPositionChild(packedPos);
        int firstGroupPos = getPackedPositionGroup(packedPos);

        // If the first visible view on the screen is a child view AND it's a
        // child of the group we are trying to collapse, then set that
        // as the first child position of the group... see
        // {@link #startCollapseAnimation(int, int)} for why this is necessary
        firstChildPos = firstChildPos == -1 || firstGroupPos != groupPos ? 0
                : firstChildPos;

        // Let the adapter know that we are going to start animating the
        // collapse animation.
        adapter.startCollapseAnimation(groupPos, firstChildPos);

        // Force the listview to refresh it's views
        adapter.notifyDataSetChanged();
        return isGroupExpanded(groupPos);
    }

    private int getAnimationDuration() {
        return ANIMATION_DURATION;
    }

    /**
     * A specialized adapter for use with the AnimatedExpandableListView. All
     * adapters used with AnimatedExpandableListView MUST extend this class.
     */
    public static abstract class AnimatedExpandableListAdapter extends
            BaseExpandableListAdapter {
        private static final int STATE_IDLE = 0;
        private static final int STATE_EXPANDING = 1;
        private static final int STATE_COLLAPSING = 2;
        private SparseArray<GroupInfo> groupInfo = new SparseArray<GroupInfo>();
        private FetchableExpandableListView parent;

        private void setParent(FetchableExpandableListView parent) {
            this.parent = parent;
        }

        public int getRealChildType(int groupPosition, int childPosition) {
            return 0;
        }

        public int getRealChildTypeCount() {
            return 1;
        }

        public abstract View getRealChildView(int groupPosition,
                                              int childPosition, boolean isLastChild, View convertView,
                                              ViewGroup parent);

        public abstract int getRealChildrenCount(int groupPosition);

        private GroupInfo getGroupInfo(int groupPosition) {
            GroupInfo info = groupInfo.get(groupPosition);
            if (info == null) {
                info = new GroupInfo();
                groupInfo.put(groupPosition, info);
            }
            return info;
        }

        public void notifyGroupExpanded(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.dummyHeight = -1;
        }

        private void startExpandAnimation(int groupPosition,
                                          int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = true;
        }

        private void startCollapseAnimation(int groupPosition,
                                            int firstChildPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = true;
            info.firstChildPosition = firstChildPosition;
            info.expanding = false;
        }

        private void stopAnimation(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            info.animating = false;
        }

        /**
         * Override {@link #getRealChildType(int, int)} instead.
         */
        @Override
        public final int getChildType(int groupPosition, int childPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                // If we are animating this group, then all of it's children
                // are going to be dummy views which we will say is type 0.
                return 0;
            } else {
                // If we are not animating this group, then we will add 1 to
                // the type it has so that no type id conflicts will occur
                // unless getRealChildType() returns MAX_INT
                return getRealChildType(groupPosition, childPosition) + 1;
            }
        }

        /**
         * Override {@link #getRealChildTypeCount()} instead.
         */
        @Override
        public final int getChildTypeCount() {
            // Return 1 more than the childTypeCount to account for DummyView
            return getRealChildTypeCount() + 1;
        }

        /**
         * Override {@link #getChildView(int, int, boolean, View, ViewGroup)}
         * instead.
         */
        @Override
        public final View getChildView(final int groupPosition,
                                       int childPosition, boolean isLastChild, View convertView,
                                       final ViewGroup parent) {
            final GroupInfo info = getGroupInfo(groupPosition);

            if (info.animating) {
                // If this group is animating, return the a DummyView...
                if (convertView instanceof DummyView == false) {
                    convertView = new DummyView(parent.getContext());
                    convertView.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT, 0));
                }

                if (childPosition < info.firstChildPosition) {
                    // The reason why we do this is to support the collapse
                    // this group when the group view is not visible but the
                    // children of this group are. When notifyDataSetChanged
                    // is called, the ExpandableListView tries to keep the
                    // list position the same by saving the first visible item
                    // and jumping back to that item after the views have been
                    // refreshed. Now the problem is, if a group has 2 items
                    // and the first visible item is the 2nd child of the group
                    // and this group is collapsed, then the dummy view will be
                    // used for the group. But now the group only has 1 item
                    // which is the dummy view, thus when the ListView is trying
                    // to restore the scroll position, it will try to jump to
                    // the second item of the group. But this group no longer
                    // has a second item, so it is forced to jump to the next
                    // group. This will cause a very ugly visual glitch. So
                    // the way that we counteract this is by creating as many
                    // dummy views as we need to maintain the scroll position
                    // of the ListView after notifyDataSetChanged has been
                    // called.
                    convertView.getLayoutParams().height = 0;
                    return convertView;
                }

                final ExpandableListView listView = (ExpandableListView) parent;

                final DummyView dummyView = (DummyView) convertView;

                // Clear the views that the dummy view draws.
                dummyView.clearViews();

                // Set the style of the divider
                dummyView.setDivider(listView.getDivider(),
                        parent.getMeasuredWidth(), listView.getDividerHeight());

                // Make measure specs to measure child views
                final int measureSpecW = MeasureSpec.makeMeasureSpec(
                        parent.getWidth(), MeasureSpec.EXACTLY);
                final int measureSpecH = MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED);

                int totalHeight = 0;
                int clipHeight = parent.getHeight();

                final int len = getRealChildrenCount(groupPosition);
                for (int i = info.firstChildPosition; i < len; i++) {
                    View childView = getRealChildView(groupPosition, i,
                            (i == len - 1), null, parent);
                    childView.measure(measureSpecW, measureSpecH);
                    totalHeight += childView.getMeasuredHeight();

                    if (totalHeight < clipHeight) {
                        // we only need to draw enough views to fool the user...
                        dummyView.addFakeView(childView);
                    } else {
                        dummyView.addFakeView(childView);

                        // if this group has too many views, we don't want to
                        // calculate the height of everything... just do a light
                        // approximation and break
                        int averageHeight = totalHeight / (i + 1);
                        totalHeight += (len - i - 1) * averageHeight;
                        break;
                    }
                }

                Object o;
                int state = (o = dummyView.getTag()) == null ? STATE_IDLE
                        : (Integer) o;

                if (info.expanding && state != STATE_EXPANDING) {
                    ExpandAnimation ani = new ExpandAnimation(dummyView, 0,
                            totalHeight, info);
                    ani.setDuration(this.parent.getAnimationDuration());
                    ani.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stopAnimation(groupPosition);
                            notifyDataSetChanged();
                            dummyView.setTag(STATE_IDLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                    });
                    dummyView.startAnimation(ani);
                    dummyView.setTag(STATE_EXPANDING);
                } else if (!info.expanding && state != STATE_COLLAPSING) {
                    if (info.dummyHeight == -1) {
                        info.dummyHeight = totalHeight;
                    }

                    ExpandAnimation ani = new ExpandAnimation(dummyView,
                            info.dummyHeight, 0, info);
                    ani.setDuration(this.parent.getAnimationDuration());
                    ani.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stopAnimation(groupPosition);
                            listView.collapseGroup(groupPosition);
                            notifyDataSetChanged();
                            info.dummyHeight = -1;
                            dummyView.setTag(STATE_IDLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                    });
                    dummyView.startAnimation(ani);
                    dummyView.setTag(STATE_COLLAPSING);
                }

                return convertView;
            } else {
                return getRealChildView(groupPosition, childPosition,
                        isLastChild, convertView, parent);
            }
        }

        @Override
        public final int getChildrenCount(int groupPosition) {
            GroupInfo info = getGroupInfo(groupPosition);
            if (info.animating) {
                return info.firstChildPosition + 1;
            } else {
                return getRealChildrenCount(groupPosition);
            }
        }

    }

    private static class DummyView extends View {
        private List<View> views = new ArrayList<View>();
        private Drawable divider;
        private int dividerWidth;
        private int dividerHeight;

        public DummyView(Context context) {
            super(context);
        }

        public void setDivider(Drawable divider, int dividerWidth,
                               int dividerHeight) {
            if (divider != null) {
                this.divider = divider;
                this.dividerWidth = dividerWidth;
                this.dividerHeight = dividerHeight;

                divider.setBounds(0, 0, dividerWidth, dividerHeight);
            }
        }

        /**
         * Add a view for the DummyView to draw.
         *
         * @param childView View to draw
         */
        public void addFakeView(View childView) {
            childView.layout(0, 0, getWidth(), getHeight());
            views.add(childView);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right,
                                int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            final int len = views.size();
            for (int i = 0; i < len; i++) {
                View v = views.get(i);
                v.layout(left, top, right, bottom);
            }
        }

        public void clearViews() {
            views.clear();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            if (divider != null) {
                divider.setBounds(0, 0, dividerWidth, dividerHeight);
            }

            final int len = views.size();
            for (int i = 0; i < len; i++) {
                View v = views.get(i);
                v.draw(canvas);
                canvas.translate(0, v.getMeasuredHeight());
                if (divider != null) {
                    divider.draw(canvas);
                    canvas.translate(0, dividerHeight);
                }
            }

            canvas.restore();
        }
    }

    /**
     * Used for holding information regarding the group.
     */
    private static class GroupInfo {
        boolean animating = false;
        boolean expanding = false;
        int firstChildPosition;

        /**
         * This variable contains the last known height value of the dummy view.
         * We save this information so that if the user collapses a group before
         * it fully expands, the collapse animation will start from the CURRENT
         * height of the dummy view and not from the full expanded height.
         */
        int dummyHeight = -1;
    }

    private static class ExpandAnimation extends Animation {
        private int baseHeight;
        private int delta;
        private View view;
        private GroupInfo groupInfo;

        private ExpandAnimation(View v, int startHeight, int endHeight,
                                GroupInfo info) {
            baseHeight = startHeight;
            delta = endHeight - startHeight;
            view = v;
            groupInfo = info;

            view.getLayoutParams().height = startHeight;
            view.requestLayout();
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int val = baseHeight + (int) (delta * interpolatedTime);
                view.getLayoutParams().height = val;
                groupInfo.dummyHeight = val;
                view.requestLayout();
            } else {
                int val = baseHeight + delta;
                view.getLayoutParams().height = val;
                groupInfo.dummyHeight = val;
                view.requestLayout();
            }
        }
    }

    private class PTROnGlobalLayoutListener implements OnGlobalLayoutListener {

        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            int initialHeaderHeight = header.getHeight();

            mHeight = getHeight();

            if (initialHeaderHeight > 0) {
                measuredHeaderHeight = initialHeaderHeight;

                if (measuredHeaderHeight > 0 && state != State.REFRESHING) {
                    setHeaderPadding(-measuredHeaderHeight);
                    requestLayout();
                }
            }
            changeLoadingFooterState();

            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private class PTROnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long id) {
            // hasResetHeader = false;

            if (onItemClickListener != null && state == State.PULL_TO_REFRESH
                    && !isLoadingMore()) {
                // Passing up onItemClick. Correct position with the number of
                // header views
                onItemClickListener.onItemClick(adapterView, view, position
                        - getHeaderViewsCount(), id);
            }
        }
    }

    private class PTROnItemLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                       int position, long id) {
            // hasResetHeader = false;

            if (onItemLongClickListener != null
                    && state == State.PULL_TO_REFRESH && !isLoadingMore()) {
                // Passing up onItemLongClick. Correct position with the number
                // of header views
                return onItemLongClickListener.onItemLongClick(adapterView,
                        view, position - getHeaderViewsCount(), id);
            }

            return false;
        }
    }

}
