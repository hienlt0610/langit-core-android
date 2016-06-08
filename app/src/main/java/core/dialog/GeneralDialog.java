package core.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commonframe.R;

import core.base.BaseDialog;
import core.util.DLog;
import core.util.SingleClick.SingleClickListener;
import core.util.Utils;

@SuppressWarnings("unused")
public class GeneralDialog extends BaseDialog implements SingleClickListener {

    public static final String TAG = GeneralDialog.class.getName();
    private final OnTouchListener DISABLER = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };
    private final String title;
    private final String message;
    private final String yes;
    private final String no;
    private final String cancel;
    private final int id;
    private final int icon;
    private DecisionListener decision_listener;
    private ConfirmListener confirm_listener;
    private Object onWhat;
    private int layout;

    public GeneralDialog(Context context, int id, @LayoutRes int layout, @DrawableRes int icon, String title,
                         String message, String yes, String no, String cancel,
                         DecisionListener listener, Object onWhat) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog);
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.message = message;
        this.yes = yes;
        this.no = no;
        this.cancel = cancel;
        this.decision_listener = listener;
        this.onWhat = onWhat;
        this.layout = layout;
    }

    public GeneralDialog(Context context, int id, @LayoutRes int layout, @DrawableRes int icon, String title,
                         String message, String confirm,
                         ConfirmListener listener, Object onWhat) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog);
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.message = message;
        this.yes = confirm;
        this.no = null;
        this.cancel = null;
        this.confirm_listener = listener;
        this.onWhat = onWhat;
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
    }

    @Override
    protected void onBaseCreate() {
        getSingleClick().setListener(this);
    }

    @Override
    protected void onBindView() {
        Button general_dialog_bt_yes = (Button) findViewById(R.id.general_dialog_bt_yes);
        Button general_dialog_bt_no = (Button) findViewById(R.id.general_dialog_bt_no);
        Button general_dialog_bt_cancel = (Button) findViewById(R.id.general_dialog_bt_cancel);
        TextView general_dialog_tv_title = (TextView) findViewById(R.id.general_dialog_tv_title);
        TextView general_dialog_tv_message = (TextView) findViewById(R.id.general_dialog_tv_message);
        ImageView general_dialog_img_icon = (ImageView) findViewById(R.id.general_dialog_img_icon);

        try {
            if (general_dialog_bt_yes == null)
                DLog.d(TAG, "Missing @id/general_dialog_bt_yes in layout xml");

            if (general_dialog_bt_no == null)
                DLog.d(TAG, "Missing @id/general_dialog_bt_no in layout xml");

            if (general_dialog_bt_cancel == null)
                DLog.d(TAG, "Missing @id/general_dialog_bt_cancel in layout xml");

            if (general_dialog_tv_title == null)
                DLog.d(TAG, "Missing @id/general_dialog_tv_title in layout xml");

            if (general_dialog_img_icon == null)
                DLog.d(TAG, "Missing @id/general_dialog_img_icon in layout xml");

            if (general_dialog_tv_message == null)
                DLog.d(TAG, "Missing @id/general_dialog_tv_message in layout xml");

            if (general_dialog_tv_title != null)
                general_dialog_tv_title.setOnTouchListener(DISABLER);

            if (general_dialog_tv_message != null)
                general_dialog_tv_message.setOnTouchListener(DISABLER);

            if (general_dialog_img_icon != null)
                general_dialog_img_icon.setOnTouchListener(DISABLER);

            if (general_dialog_img_icon != null) {
                if (icon > 0)
                    general_dialog_img_icon.setImageResource(icon);
                else
                    general_dialog_img_icon.setVisibility(View.GONE);
            }

            if (general_dialog_tv_title != null) {
                if (!Utils.isEmpty(title))
                    general_dialog_tv_title.setText(title);
                else
                    general_dialog_tv_title.setVisibility(View.GONE);
            }

            if (general_dialog_tv_message != null) {
                if (!Utils.isEmpty(message))
                    general_dialog_tv_message.setText(message);
                else
                    general_dialog_tv_message.setVisibility(View.GONE);
            }

            if (general_dialog_bt_yes != null) {
                if (!Utils.isEmpty(yes))
                    general_dialog_bt_yes.setText(yes);
                else
                    general_dialog_bt_yes.setVisibility(View.GONE);
            }

            if (general_dialog_bt_no != null) {
                if (!Utils.isEmpty(no))
                    general_dialog_bt_no.setText(no);
                else
                    general_dialog_bt_no.setVisibility(View.GONE);
            }

            if (general_dialog_bt_cancel != null) {
                if (!Utils.isEmpty(cancel))
                    general_dialog_bt_cancel.setText(cancel);
                else
                    general_dialog_bt_cancel.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
        }
    }

    @Override
    public void onSingleClick(View v) {

        switch (v.getId()) {
            case R.id.general_dialog_bt_no:
                dismiss();
                if (decision_listener != null)
                    decision_listener.onDisAgreed(id, onWhat);
                break;
            case R.id.general_dialog_bt_yes:
                dismiss();
                if (decision_listener != null)
                    decision_listener.onAgreed(id, onWhat);
                if (confirm_listener != null)
                    confirm_listener.onConfirmed(id, onWhat);
                break;
            case R.id.general_dialog_bt_cancel:
                dismiss();
                if (decision_listener != null)
                    decision_listener.onNeutral(id, onWhat);
                break;
            default:
                break;
        }
    }

    public interface DecisionListener {
        void onAgreed(int id, Object onWhat);

        void onDisAgreed(int id, Object onWhat);

        void onNeutral(int id, Object onWhat);
    }

    public interface ConfirmListener {

        void onConfirmed(int id, Object onWhat);
    }
}
