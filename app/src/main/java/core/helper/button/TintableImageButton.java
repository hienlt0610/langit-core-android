package core.helper.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import core.util.Utils;

/**
 * <b>Class Overview</b>
 * <p/>
 * A generic, customizable Android ImageButton implementation that automatically
 * adds the <code>focused</code> and <code>pressed</code> states to the image
 * source or back ground of the button.
 * <p/>
 * This ImageButton can be used in place of the normal Android
 * android.widget.ImageButton class. It adds the tintable image source or back ground
 * (darkening the current image source or back ground) as <code>focused</code>
 * and <code>pressed</code> states without any modifications in code or xml.
 * <p/>
 * Users are encouraged to use the android.widget.ImageButton with a defined
 * state list image source or background (for better performance) instead of
 * this class. However, in some case of lacking the resources or to keep the
 * clean code without messing up the xmls, this class will come for the help.
 * <p/>
 *
 * @author Tyrael
 * @version 1.0
 * @since October 2014
 */
public class TintableImageButton extends ImageButton {

    public TintableImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TintableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintableImageButton(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getDrawable() != null) {
            setImageDrawable(Utils.makeTintableStateDrawable(getContext(),
                    getDrawable()));
            setBackgroundDrawable(null);
            setBackgroundResource(0);
        } else {
            if (getBackground() != null)
                setBackgroundDrawable(Utils.makeTintableStateDrawable(
                        getContext(), getBackground()));
        }
    }

}
