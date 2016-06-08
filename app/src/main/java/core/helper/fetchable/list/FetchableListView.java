package core.helper.fetchable.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.commonframe.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import core.helper.fetchable.list.FetchableInterface.OnLoadMoreListener;
import core.helper.fetchable.list.FetchableInterface.OnRefreshListener;
import core.helper.fetchable.list.FetchableInterface.State;

/**
 * <b>Class Overview</b>
 * <p/>
 * A generic, customizable Android ListView implementation that has 'Pull to
 * Refresh' and 'Load more' functionalities.
 * <p/>
 * This ListView can be used in place of the normal Android
 * android.widget.ListView class.
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

@SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility",
        "InflateParams"})
public class FetchableListView extends ListView implements OnScrollListener {

    private static final float PULL_RESISTANCE = 1.7f;
    private static final int ROTATE_ARROW_ANIMATION_DURATION = 150;
    /**
     * The measured height of the height used for showing refresh view
     */
    private static int measuredHeaderHeight;
    private final Handler mScrollbarHandler = new Handler();
    /**
     * Rectangle bound for the touch event of the pinned section.
     */
    private final Rect touchRect = new Rect();
    /**
     * Touch point event of the pinned section.
     */
    private final PointF touchPoint = new PointF();
    /**
     * Number of idle pixel of the pull to refresh functionality
     */
    private final int IDLE_DISTANCE = 5;
    /* SCROLL BAR POSITION */
    private boolean isScrollBarIndicatorDisplayable = false;
    private int mWidthMeasureSpecScrollBar;
    private int mHeightMeasureSpecScrollBar;
    private View mScrollBarPanel = null;
    private int mScrollBarPanelPosition = 0;
    private int mLastPosition = -1;
    /* END SCROLL BAR POSITION */

    /* PINNED SECTION VARIABLES */
    private Animation mInAnimation = null;
    private Animation mOutAnimation = null;
    private final Runnable mScrollBarPanelFadeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mOutAnimation != null) {
                if (mScrollBarPanel != null
                        && mScrollBarPanel.getVisibility() == View.VISIBLE)
                    mScrollBarPanel.startAnimation(mOutAnimation);
            }
        }
    };
    private OnPositionChangedListener onPositionChangedListener;
    /**
     * The boolean value to indicate the 'pinned section' functionality
     */
    private boolean isPinable = false;
    /**
     * Shadow for being recycled, can be null.
     */
    private PinableSection recyleSection;
    /**
     * shadow instance with a pinned view, can be null.
     */
    private PinableSection pinnedSection;
    /**
     * Pinned view Y-translation. We use it to stick pinned view to the next
     * section.
     */
    private int pinnedTranslateY;
    /**
     * Touch scaled slop.
     */
    private int touchSlop;
    /**
     * Pinned section has been touched.
     */
    private View touchTarget;
    /**
     * Touch event of the pinned section.
     */
    private MotionEvent touchEvent;
    /**
     * Shadow of the pinned section.
     */
    private GradientDrawable shadow;
    /**
     * Shadow distance from the top of the pinned section.
     */
    private int sectionDistanceY;
    /**
     * The observer in case the adapter changed.
     */
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            recreatePinnedShadow();
        }

        @Override
        public void onInvalidated() {
            recreatePinnedShadow();
        }
    };

	/* END PINNED SECTION VARIABLES */

	/* HEADER VARIABLES */
    /**
     * Shadow height of the pinned section.
     */
    private int shadowHeight;
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
    /**
     * The local limit of items for this list view (if the limit is reached, no
     * loading more will be operate). Default is -1 means NO LIMIT
     */
    private int limit = -1;
    /* END HEADER VARIABLES */

	/* FOOTER VARIABLES */
    /**
     * The reference to the adapter of this list view, used to calculate the
     * height of all items to change the load more footer
     */
    private ListAdapter adapter;
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
    /**
     * The disabler of the header and foot click function
     */

    private OnTouchListener ignoreTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return true;
        }
    };
    /* END FOOTER VARIABLES */

    public FetchableListView(Context context) {
        super(context);
        init(context);
    }

    public FetchableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
        init(context);
    }

    public FetchableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
        init(context);

    }

    public static boolean isItemViewTypePinned(ListAdapter adapter, int viewType) {
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof PinableSectionInterface) {

            return ((PinableSectionInterface) adapter)
                    .isItemViewTypePinned(viewType);
        }
        return false;
    }

	/* GENERAL */

    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray attrsFetchableArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.FetchableView, 0,
                        defStyle);

        TypedArray attrsPinableArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.PinableListView, 0,
                        defStyle);

        TypedArray attrsIndicableArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ScrollBarIndicator,
                        0, defStyle);
        try {
            int animationId = -1;
            if ((animationId = attrsIndicableArray.getResourceId(
                    R.styleable.ScrollBarIndicator_indicatorInAnimation, -1)) != -1)
                mInAnimation = AnimationUtils.loadAnimation(getContext(),
                        animationId);

            animationId = -1;
            if ((animationId = attrsIndicableArray.getResourceId(
                    R.styleable.ScrollBarIndicator_indicatorOutAnimation, -1)) != -1)
                mOutAnimation = AnimationUtils.loadAnimation(getContext(),
                        animationId);

            int indicatorViewId = -1;
            if ((indicatorViewId = attrsIndicableArray.getResourceId(
                    R.styleable.ScrollBarIndicator_indicatorView, -1)) != -1)
                setScrollBarPanel(indicatorViewId);

            isScrollBarIndicatorDisplayable = attrsIndicableArray.getBoolean(
                    R.styleable.ScrollBarIndicator_isIndicable, false);
            isPinable = attrsPinableArray.getBoolean(
                    R.styleable.PinableListView_isPinable, false);
            isFetchable = attrsFetchableArray.getBoolean(
                    R.styleable.FetchableView_isFetchable, false);
            isRefreshable = attrsFetchableArray.getBoolean(
                    R.styleable.FetchableView_isRefreshable, false);
            limit = attrsFetchableArray.getInteger(
                    R.styleable.FetchableView_limit, -1);
            setCacheColorHint(Color.TRANSPARENT);
            setDivider(null);
            setDividerHeight(0);
        } finally {
            attrsIndicableArray.recycle();
            attrsPinableArray.recycle();
            attrsFetchableArray.recycle();
        }
    }

    private void init(Context context) {
        super.setOnScrollListener(this);
        setVerticalFadingEdgeEnabled(false);
        initScrollBarPosition();
        initPinnedSection();
        initHeader();
        initFooter();

    }

    private void initScrollBarPosition() {
        if (mInAnimation == null)
            mInAnimation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_in_right);
        if (mOutAnimation == null)
            mOutAnimation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_right);
        mInAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mScrollBarPanel != null) {
                    mScrollBarPanel.setVisibility(View.VISIBLE);
                }
            }
        });
        mOutAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mScrollBarPanel != null) {
                    mScrollBarPanel.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateTotalItemsHeight() {
        if (adapter != null) {
            totalItemsHeight = 0;
            for (int i = 0; i < adapter.getCount(); ++i) {
                View item = adapter.getView(i, null, this);
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

    @Override
    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        updateTotalItemsHeight();
        if (adapter != null && adapter instanceof PinableSectionInterface
                && adapter.getViewTypeCount() >= 2 && isPinable)
            configPinnedSectionAdapter(adapter);
        super.setAdapter(adapter);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // To prevent random IndexOutOfBoundsException
        try {
            super.dispatchDraw(canvas);
            dispatchDrawPinnedSection(canvas);
            dispatchDrawScrollBarPosition(canvas);
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

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
        onDetachedFromWindowScrollBarPosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        onMeasureScrollBarPosition(widthMeasureSpec, heightMeasureSpec);
    }

	/* END GENERAL */

	/* PINNED SECTION */

    private void initPinnedSection() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initShadow(true);
    }

    public void initShadow(boolean visible) {
        if (visible) {
            if (shadow == null) {
                shadow = new GradientDrawable(Orientation.TOP_BOTTOM,
                        new int[]{Color.parseColor("#ffa0a0a0"),
                                Color.parseColor("#50a0a0a0"),
                                Color.parseColor("#00a0a0a0")});
                shadowHeight = (int) (8 * getResources().getDisplayMetrics().density);
            }
        } else {
            if (shadow != null) {
                shadow = null;
                shadowHeight = 0;
            }
        }
    }

    public void setShadowVisible(boolean visible) {
        initShadow(visible);
        if (pinnedSection != null) {
            View v = pinnedSection.view;
            invalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom()
                    + shadowHeight);
        }
    }

    /**
     * Create shadow wrapper with a pinned view for a view at given position
     */
    private void createPinnedShadow(int position) {

        // try to recycle shadow
        PinableSection pinnedShadow = recyleSection;
        recyleSection = null;

        // create new shadow, if needed
        if (pinnedShadow == null)
            pinnedShadow = new PinableSection();
        // request new view using recycled view, if such
        View pinnedView = getAdapter().getView(position, pinnedShadow.view,
                FetchableListView.this);

        // read layout parameters
        LayoutParams layoutParams = (LayoutParams) pinnedView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (LayoutParams) generateDefaultLayoutParams();
            pinnedView.setLayoutParams(layoutParams);
        }

        int heightMode = MeasureSpec.getMode(layoutParams.height);
        int heightSize = MeasureSpec.getSize(layoutParams.height);

        if (heightMode == MeasureSpec.UNSPECIFIED)
            heightMode = MeasureSpec.EXACTLY;

        int maxHeight = getHeight() - getListPaddingTop()
                - getListPaddingBottom();
        if (heightSize > maxHeight)
            heightSize = maxHeight;

        // measure & layout
        int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft()
                - getListPaddingRight(), MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        pinnedView.measure(ws, hs);
        pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(),
                pinnedView.getMeasuredHeight());
        pinnedTranslateY = 0;

        // initialize pinned shadow
        pinnedShadow.view = pinnedView;
        pinnedShadow.position = position;
        pinnedShadow.id = getAdapter().getItemId(position);

        // store pinned shadow
        pinnedSection = pinnedShadow;
    }

    /**
     * Destroy shadow wrapper for currently pinned view
     */
    private void destroyPinnedShadow() {
        if (pinnedSection != null) {
            // keep shadow for being recycled later
            recyleSection = pinnedSection;
            pinnedSection = null;
        }
    }

    private void ensureShadowForPosition(int sectionPosition,
                                         int firstVisibleItem, int visibleItemCount) {
        if (visibleItemCount < 2) { // no need for creating shadow at all, we
            // have a single visible item
            destroyPinnedShadow();
            return;
        }

        if (pinnedSection != null && pinnedSection.position != sectionPosition) { // invalidate
            // shadow,
            // if
            // required
            destroyPinnedShadow();
        }

        if (pinnedSection == null) { // create shadow, if empty
            createPinnedShadow(sectionPosition);
        }

        // align shadow according to next section position, if needed
        int nextPosition = sectionPosition + 1;
        if (nextPosition < getCount()) {
            int nextSectionPosition = findFirstVisibleSectionPosition(
                    nextPosition, visibleItemCount
                            - (nextPosition - firstVisibleItem));
            if (nextSectionPosition > -1) {
                View nextSectionView = getChildAt(nextSectionPosition
                        - firstVisibleItem);
                final int bottom = pinnedSection.view.getBottom()
                        + getPaddingTop();
                sectionDistanceY = nextSectionView.getTop() - bottom;
                if (sectionDistanceY < 0) {
                    // next section overlaps pinned shadow, move it up
                    pinnedTranslateY = sectionDistanceY;
                } else {
                    // next section does not overlap with pinned, stick to top
                    pinnedTranslateY = 0;
                }
            } else {
                // no other sections are visible, stick to top
                pinnedTranslateY = 0;
                sectionDistanceY = Integer.MAX_VALUE;
            }
        }

    }

    private int findFirstVisibleSectionPosition(int firstVisibleItem,
                                                int visibleItemCount) {
        ListAdapter adapter = getAdapter();

        if (firstVisibleItem + visibleItemCount >= adapter.getCount())
            return -1; // dataset has changed, no candidate

        for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
            int position = firstVisibleItem + childIndex;
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType))
                return position;
        }
        return -1;
    }

    private int findCurrentSectionPosition(int fromPosition) {
        ListAdapter adapter = getAdapter();

        if (fromPosition >= adapter.getCount())
            return -1; // dataset has changed, no candidate

        if (adapter instanceof SectionIndexer) {
            // try fast way by asking section indexer
            SectionIndexer indexer = (SectionIndexer) adapter;
            int sectionPosition = indexer.getSectionForPosition(fromPosition);
            int itemPosition = indexer.getPositionForSection(sectionPosition);
            int typeView = adapter.getItemViewType(itemPosition);
            if (isItemViewTypePinned(adapter, typeView)) {
                return itemPosition;
            } // else, no luck
        }

        // try slow way by looking through to the next section item above
        for (int position = fromPosition; position >= 0; position--) {
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType))
                return position;
        }
        return -1; // no candidate found
    }

    private void recreatePinnedShadow() {
        destroyPinnedShadow();
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            int firstVisiblePosition = getFirstVisiblePosition();
            int sectionPosition = findCurrentSectionPosition(firstVisiblePosition);
            if (sectionPosition == -1)
                return; // no views to pin, exit
            ensureShadowForPosition(sectionPosition, firstVisiblePosition,
                    getLastVisiblePosition() - firstVisiblePosition);
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        post(new Runnable() {
            @Override
            public void run() { // restore pinned view after configuration
                // change
                recreatePinnedShadow();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (pinnedSection != null) {
            int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
            int shadowWidth = pinnedSection.view.getWidth();
            if (parentWidth != shadowWidth) {
                recreatePinnedShadow();
            }
        }

        if (isScrollBarIndicationEnable())
            onLayoutScrollBarPosition();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN && touchTarget == null
                && pinnedSection != null
                && isPinnedViewTouched(pinnedSection.view, x, y)) { // create
            // touch
            // target

            // user touched pinned view
            touchTarget = pinnedSection.view;
            touchPoint.x = x;
            touchPoint.y = y;

            // copy down event for eventually be used later
            touchEvent = MotionEvent.obtain(ev);
        }

        if (touchTarget != null) {
            if (isPinnedViewTouched(touchTarget, x, y)) { // forward event to
                // pinned view
                touchTarget.dispatchTouchEvent(ev);
            }

            if (action == MotionEvent.ACTION_UP) { // perform onClick on pinned
                // view
                super.dispatchTouchEvent(ev);
                performPinnedItemClick();
                clearTouchTarget();

            } else if (action == MotionEvent.ACTION_CANCEL) { // cancel
                clearTouchTarget();

            } else if (action == MotionEvent.ACTION_MOVE) {
                if (Math.abs(y - touchPoint.y) > touchSlop) {

                    // cancel sequence on touch target
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    touchTarget.dispatchTouchEvent(event);
                    event.recycle();

                    // provide correct sequence to super class for further
                    // handling
                    super.dispatchTouchEvent(touchEvent);
                    super.dispatchTouchEvent(ev);
                    clearTouchTarget();

                }
            }

            return true;
        }

        // call super if this was not our pinned view
        return super.dispatchTouchEvent(ev);
    }

    private boolean isPinnedViewTouched(View view, float x, float y) {
        view.getHitRect(touchRect);

        // by taping top or bottom padding, the list performs on click on a
        // border item.
        // we don't add top padding here to keep behavior consistent.
        touchRect.top += pinnedTranslateY;

        touchRect.bottom += pinnedTranslateY + getPaddingTop();
        touchRect.left += getPaddingLeft();
        touchRect.right -= getPaddingRight();
        return touchRect.contains((int) x, (int) y);
    }

    private void clearTouchTarget() {
        touchTarget = null;
        if (touchEvent != null) {
            touchEvent.recycle();
            touchEvent = null;
        }
    }

    private boolean performPinnedItemClick() {
        if (pinnedSection == null)
            return false;

        OnItemClickListener listener = getOnItemClickListener();
        if (listener != null && getAdapter().isEnabled(pinnedSection.position)) {
            View view = pinnedSection.view;
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            listener.onItemClick(this, view, pinnedSection.position,
                    pinnedSection.id);
            return true;
        }
        return false;
    }

    private void configPinnedSectionAdapter(ListAdapter adapter) {
        // if (adapter != null) {
        // if(isPinable) {
        // if (!(adapter instanceof PinableSectionInterface))
        // throw new IllegalArgumentException(
        // "Does your adapter implement PinnedSectionListAdapter?");
        // if (adapter.getViewTypeCount() < 2)
        // throw new IllegalArgumentException(
        // "Does your adapter handle at least two types"
        // + " of views in getViewTypeCount() method: items and sections?");
        // }
        // }

        // unregister observer at old adapter and register on new one
        ListAdapter oldAdapter = getAdapter();
        if (oldAdapter != null)
            oldAdapter.unregisterDataSetObserver(mDataSetObserver);
        if (adapter != null)
            adapter.registerDataSetObserver(mDataSetObserver);

        // destroy pinned shadow, if new adapter is not same as old one
        if (oldAdapter != adapter)
            destroyPinnedShadow();
    }

    private void dispatchDrawPinnedSection(Canvas canvas) {
        if (pinnedSection != null) {

            // prepare variables
            int pLeft = getListPaddingLeft();
            int pTop = getListPaddingTop();
            View view = pinnedSection.view;

            // draw child
            canvas.save();

            int clipHeight = view.getHeight()
                    + (shadow == null ? 0 : Math.min(shadowHeight,
                    sectionDistanceY));
            canvas.clipRect(pLeft, pTop, pLeft + view.getWidth(), pTop
                    + clipHeight);

            canvas.translate(pLeft, pTop + pinnedTranslateY);
            drawChild(canvas, pinnedSection.view, getDrawingTime());

            if (shadow != null && sectionDistanceY > 0) {
                shadow.setBounds(pinnedSection.view.getLeft(),
                        pinnedSection.view.getBottom(),
                        pinnedSection.view.getRight(),
                        pinnedSection.view.getBottom() + shadowHeight);
                shadow.draw(canvas);
            }
            canvas.restore();
        }
    }

    /**
     * @return the isScrollBarPositionDisplayable
     */
    public boolean isScrollBarIndicatorDisplayable() {
        return isScrollBarIndicatorDisplayable;
    }

	/* END PINNED SECTION */

	/* SCROLL BAR POSITION */

    /**
     * @param isScrollBarIndicatorDisplayable the isScrollBarIndicatorDisplayable to set
     */
    public void setScrollBarIndicatorDisplayable(
            boolean isScrollBarIndicatorDisplayable) {
        this.isScrollBarIndicatorDisplayable = isScrollBarIndicatorDisplayable;
    }

    private boolean isScrollBarIndicationEnable() {
        return isScrollBarIndicatorDisplayable && null != mScrollBarPanel;
    }

    /**
     * @return the onPositionChangedListener
     */
    public OnPositionChangedListener getOnPositionChangedListener() {
        return onPositionChangedListener;
    }

    /**
     * @param onPositionChangedListener the onPositionChangedListener to set
     */
    public void setOnPositionChangedListener(
            OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    public void setScrollBarPanel(View scrollBarPanel) {
        mScrollBarPanel = scrollBarPanel;
        mScrollBarPanel.setVisibility(View.GONE);
        requestLayout();
    }

    public View getScrollBarPanel() {
        return mScrollBarPanel;
    }

    public void setScrollBarPanel(int resId) {
        setScrollBarPanel(LayoutInflater.from(getContext()).inflate(resId,
                this, false));
    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        final boolean isAnimationPlayed = super.awakenScrollBars(startDelay,
                invalidate);

        if (isAnimationPlayed == true && mScrollBarPanel != null) {
            if (mScrollBarPanel.getVisibility() == View.GONE) {
                mScrollBarPanel.setVisibility(View.VISIBLE);
                if (mInAnimation != null) {
                    mScrollBarPanel.startAnimation(mInAnimation);
                }
            }

            mScrollbarHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
            mScrollbarHandler.postAtTime(mScrollBarPanelFadeRunnable,
                    AnimationUtils.currentAnimationTimeMillis() + startDelay);
        }

        return isAnimationPlayed;
    }

    private void checkScrollBarPosition(int firstVisibleItem, int totalItemCount) {

        // Don't do anything if there is no itemviews
        if (totalItemCount > 0) {
			/*
			 * from android source code (ScrollBarDrawable.java)
			 */
            final int thickness = getVerticalScrollbarWidth();
            int height = Math.round((float) getMeasuredHeight()
                    * computeVerticalScrollExtent()
                    / computeVerticalScrollRange());
            int thumbOffset = Math
                    .round((float) (getMeasuredHeight() - height)
                            * computeVerticalScrollOffset()
                            / (computeVerticalScrollRange() - computeVerticalScrollExtent()));
            final int minLength = thickness * 2;
            if (height < minLength) {
                height = minLength;
            }
            thumbOffset += height / 2;

			/*
			 * find out which itemviews the center of thumb is on
			 */
            final int count = getChildCount();
            for (int i = 0; i < count; ++i) {
                final View childView = getChildAt(i);
                if (childView != null) {
                    if (thumbOffset > childView.getTop()
                            && thumbOffset < childView.getBottom()) {
						/*
						 * we have our candidate
						 */
                        if (mLastPosition != firstVisibleItem + i) {
                            mLastPosition = firstVisibleItem + i;

							/*
							 * inform the position of the panel has changed
							 */
                            if (onPositionChangedListener != null)
                                onPositionChangedListener.onPositionChanged(
                                        this, mLastPosition, mScrollBarPanel);

							/*
							 * measure panel right now since it has just changed
							 *
							 * INFO: quick hack to handle TextView has
							 * ScrollBarPanel (to wrap text in case TextView's
							 * content has changed)
							 */
                            measureChild(mScrollBarPanel,
                                    mWidthMeasureSpecScrollBar,
                                    mHeightMeasureSpecScrollBar);
                        }
                        break;
                    }
                }
            }

			/*
			 * update panel position
			 */
            mScrollBarPanelPosition = thumbOffset
                    - mScrollBarPanel.getMeasuredHeight() / 2;
            final int x = getMeasuredWidth()
                    - mScrollBarPanel.getMeasuredWidth()
                    - getVerticalScrollbarWidth();

            mScrollBarPanel.layout(
                    x,
                    mScrollBarPanelPosition,
                    x + mScrollBarPanel.getMeasuredWidth(),
                    mScrollBarPanelPosition
                            + mScrollBarPanel.getMeasuredHeight());

        }

    }

    private void dispatchDrawScrollBarPosition(Canvas canvas) {
        if (mScrollBarPanel != null
                && mScrollBarPanel.getVisibility() == View.VISIBLE) {
            drawChild(canvas, mScrollBarPanel, getDrawingTime());
        }
    }

    private void onDetachedFromWindowScrollBarPosition() {
        mScrollbarHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
    }

    private void onLayoutScrollBarPosition() {
        if (mScrollBarPanel != null) {
            final int x = getMeasuredWidth()
                    - mScrollBarPanel.getMeasuredWidth()
                    - getVerticalScrollbarWidth();
            mScrollBarPanel.layout(
                    x,
                    mScrollBarPanelPosition,
                    x + mScrollBarPanel.getMeasuredWidth(),
                    mScrollBarPanelPosition
                            + mScrollBarPanel.getMeasuredHeight());
        }
    }

    private void onMeasureScrollBarPosition(int widthMeasureSpec,
                                            int heightMeasureSpec) {
        if (mScrollBarPanel != null && getAdapter() != null) {
            mWidthMeasureSpecScrollBar = widthMeasureSpec;
            mHeightMeasureSpecScrollBar = heightMeasureSpec;
            measureChild(mScrollBarPanel, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void initHeader() {

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

	/* END SCROLL BAR POSITION */

	/* HEADER */

    private void setHeaderPadding(int padding) {
        if (header != null) {
            headerPadding = padding;

            MarginLayoutParams mlp = (MarginLayoutParams) header
                    .getLayoutParams();
            mlp.setMargins(0, Math.round(padding), 0, 0);
            header.setLayoutParams(mlp);
        }
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

                    } else {
                        onRefreshListener.onRefresh();
                    }
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

    private boolean isPinable() {
        return adapter != null && adapter instanceof PinableSectionInterface
                && adapter.getCount() > 0 && adapter.getViewTypeCount() >= 2
                && isPinable;
    }

    public void setPinable(boolean isPinable) {
        this.isPinable = isPinable;
    }

    private void displayPinnedSection(AbsListView view, int firstVisibleItem,
                                      int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) { // delegate
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }

        // get expected adapter or fail fast
        ListAdapter adapter = getAdapter();
        if (adapter == null || visibleItemCount == 0)
            return; // nothing to do

        final boolean isFirstVisibleItemSection = isItemViewTypePinned(adapter,
                adapter.getItemViewType(firstVisibleItem));

        if (isFirstVisibleItemSection) {
            View sectionView = getChildAt(0);
            if (sectionView.getTop() == getPaddingTop()) { // view sticks to the
                // top, no need for
                // pinned shadow
                destroyPinnedShadow();
            } else { // section doesn't stick to the top, make sure we have a
                // pinned shadow
                ensureShadowForPosition(firstVisibleItem, firstVisibleItem,
                        visibleItemCount);
            }

        } else { // section is not at the first visible position
            int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
            if (sectionPosition > -1) { // we have section position
                ensureShadowForPosition(sectionPosition, firstVisibleItem,
                        visibleItemCount);
            } else { // there is no section for the first visible item, destroy
                // shadow
                destroyPinnedShadow();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (isScrollBarIndicationEnable())
            checkScrollBarPosition(firstVisibleItem, totalItemCount);

        if (isPinable())
            displayPinnedSection(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);

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

    /* FOOTER */
    private void initFooter() {
        if (!isFetchable)
            return;

        mFooterView = (FrameLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.fetchable_footer, this, false);
        mFooterView.setOnTouchListener(ignoreTouch);
        addFooterView(mFooterView);

    }

	/* END HEADER */

    /**
     * Method to change footer state as the first load, if the height of all
     * items less than the height of the list view itself, the loading more
     * footer should be invisible.
     */
    private void changeLoadingFooterState() {
        if (adapter != null) {
            if (adapter.getCount() >= limit && limit != -1) {
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
    public void setFooterVisible(boolean isVisible) {
        if (!isFetchable)
            return;

        if (isVisible) {
            mFooterView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            mFooterView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
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

    /**
     * Wrapper class for pinned section view and its position in the list.
     */
    private static class PinableSection {
        public View view;
        public int position;
        public long id;
    }

	/* END FOOTER */

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
