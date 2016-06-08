package core.base;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.commonframe.R;

import core.util.Constant;
import core.util.SingleClick.SingleClickListener;
import core.util.Utils;

/**
 * @author Tyrael
 * @version 1.0 <br>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          Represents a class for essential fragment activity to be a super
 *          class of activities in project. It includes supportive method of
 *          showing, closing dialogs, making and canceling request. Those
 *          methods can be used in any derived class. <br>
 *          This class also supports multiple fragment containers for both
 *          tablet and phone with methods of add, remove, replace, back and
 *          clear fragments on a specific container. <br>
 *          The derived classes must implement <code>onBaseCreate()</code>,
 *          <code>onBindView()</code>, <code>onResumeObject()</code>,
 *          <code>onFreeObject()</code> for the purpose of management.
 * @since May 2015
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseMultipleFragmentActivity extends BaseActivity
        implements BaseInterface, SingleClickListener {
    /**
     * Tag of BaseFragmentActivity class for Log usage
     */
    private static String TAG = BaseMultipleFragmentActivity.class.getSimpleName();
    /**
     * The flag indicating that the fragments are first initialized after the
     * activity created, this variable is only invoked once.
     */
    private boolean isFragmentsInitialized = false;
    /**
     * The identification of the main fragment container, the default is the
     * first container added. Or it can be set by
     * <code>setMainContainerId()</code>. The id is used for default
     * <code>onBackPress()</code>, <code>onDeepLinking()</code>,
     * <code>onNotification()</code>, <code>onActivityResult()</code>
     */
    private int mainContainerId = -1;
    /**
     * This method is for initializing fragments used in the activity. This
     * method is called immediately after the <code>onResumeFragments()</code>
     * method of the activity and only called once when the activity is created,
     * it depends on the <code>isFragmentsInitialized</code>. Any first
     * fragments that used inside the activity should be initialized here for
     * the purpose of management.
     */
    protected abstract void onInitializeFragments();

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // this code is to prevent using commitAllowStateLoss
        if (!isFragmentsInitialized) {
            isFragmentsInitialized = true;
            onInitializeFragments();
        }
    }

    @Override
    public void onBackPressed() {
        if (BaseProperties.getSingleBackPress().onBackPressAllowed()) {
            // super.onBackPressed();
            backStack(mainContainerId, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BaseMultipleFragment fragment = getTopFragment(mainContainerId);
        if (fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getMainContainerId() {
        return this.mainContainerId;
    }

    protected void setMainContainerId(int mainContainerId) {
        this.mainContainerId = mainContainerId;
    }

    public BaseMultipleFragment getTopFragment(int containerId) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null && fm.getBackStackEntryCount() > 0) {
            String topTag = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
            Fragment fragment = fm.findFragmentByTag(topTag);
            if (fragment instanceof BaseMultipleFragment) {
                return (BaseMultipleFragment) fragment;
            }
        }
        return null;
    }

    public void backStack(int containerId, String toTag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            if (Utils.isEmpty(toTag)) {
                fm.popBackStackImmediate();
            } else {
                fm.popBackStackImmediate(toTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    protected void addFragment(int containerId, BaseMultipleFragment fragment, String tag) {
        BaseMultipleFragment top = getTopFragment(containerId);
        if (top != null) {
            if (tag.equals(top.getTag()))
                return;
            top.onPause();
        }
        animateAddOut(containerId);
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 0) {
                boolean isExisted = fm.findFragmentByTag(tag) != null;
                if (!isExisted) {
                    doAddFragment(containerId, fragment, tag);
                } else {
                    backStack(containerId, tag);
                }
            } else {
                if (mainContainerId == -1)
                    mainContainerId = containerId;
                doAddFragment(containerId, fragment, tag);
            }
        }
    }

    private void doAddFragment(int containerId, BaseMultipleFragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        int anim = fragment.getEnterInAnimation();
        if (anim == -1) {
            anim = Constant.DEFAULT_ADD_ANIMATION[0];
        }
        transaction
                .setCustomAnimations(anim,
                        0, 0, 0) // add in animation
                .add(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    protected void replaceFragment(int containerId,
                                   BaseMultipleFragment fragment, String tag, boolean clearStack) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            if (clearStack) {
                doReplaceFragment(containerId, fragment, tag);
            } else {
                boolean isExisted = fm.findFragmentByTag(tag) != null;
                if (isExisted) {
                    if (fm.getBackStackEntryCount() > 1) {
                        BaseMultipleFragment top = getTopFragment(containerId);
                        if (!(top != null && top.getTag().equals(tag))) {
                            backStack(containerId, tag);
                        }
                    }
                } else {
                    if (fm.getBackStackEntryCount() > 1) {
                        BaseMultipleFragment top = getTopFragment(containerId);
                        addFragment(containerId, fragment, tag);
                        if (top != null && !Utils.isEmpty(top.getTag()))
                            removeFragment(containerId, top.getTag());
                    } else {
                        doReplaceFragment(containerId, fragment, tag);
                    }
                }
            }
        }
    }

    private void doReplaceFragment(int containerId, BaseMultipleFragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        int anim = fragment.getEnterInAnimation();
        if (anim == -1) {
            anim = Constant.DEFAULT_ADD_ANIMATION[0];
        }
        transaction
                .setCustomAnimations(anim,
                        0, 0, 0) // add in animation
                .replace(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    protected void removeFragment(int containerId, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            Fragment removed = fm.findFragmentByTag(tag);
            if (removed != null) {
                fm.beginTransaction()
                        .remove(removed)
                        .commit();
                fm.executePendingTransactions();
            }
        }
    }

    private void animateAddOut(int containerId) {
        BaseMultipleFragment previous = getTopFragment(containerId);
        if (previous != null) {
            final View view = previous.getView();
            if (view != null) {
                int anim = previous.getEnterOutAnimation();
                if (anim == -1) {
                    anim = Constant.DEFAULT_ADD_ANIMATION[1];
                }
                Animation animation = AnimationUtils.loadAnimation(this,
                        anim);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animation);
            }
        }
    }

    private void animateBackIn(final View view, int anim) {
        if (view != null) {
            if (anim == -1) {
                anim = Constant.DEFAULT_BACK_ANIMATION[0];
            }
            Animation animation = AnimationUtils.loadAnimation(this,
                    anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }
    }

    private void animateBackOut(View view, int anim) {
        if (view != null) {
            if (anim == -1) {
                anim = Constant.DEFAULT_BACK_ANIMATION[1];
            }
            view.startAnimation(AnimationUtils.loadAnimation(this,
                    anim));
        }
    }

    @LayoutRes
    @Override
    public int getLoadingDialogLayoutResource() {
        return R.layout.loading_dialog;
    }

    @LayoutRes
    @Override
    public int getGeneralDialogLayoutResource() {
        return R.layout.general_dialog;
    }
}
