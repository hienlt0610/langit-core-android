package core.helper.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import core.util.Utils;

/**
 * <b>Class Overview</b>
 * <p/>
 * A generic, customizable Android Button implementation that automatically adds
 * the <code>focused</code> and <code>pressed</code> states to the back ground
 * of the button.
 * <p/>
 * This Button can be used in place of the normal Android android.widget.Button
 * class. It adds the tintable back ground (darkening the current back ground)
 * as <code>focused</code> and <code>pressed</code> states without any
 * modifications in code or xml.
 * <p/>
 * Users are encouraged to use the android.widget.Button with a defined state
 * list background (for better performance) instead of this class. However, in
 * some case of lacking the resources or to keep the clean code without messing
 * up the xmls, this class will come for the help.
 * <p/>
 *
 * @author Tyrael
 * @version 1.0
 * @since October 2014
 */
public class TintableButton extends Button {

    public TintableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TintableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintableButton(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getBackground() != null)
            setBackgroundDrawable(Utils.makeTintableStateDrawable(getContext(),
                    getBackground()));
    }

}
