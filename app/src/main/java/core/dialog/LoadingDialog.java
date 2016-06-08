package core.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import com.example.commonframe.R;

import core.base.BaseDialog;
import core.util.DLog;
import core.util.Utils;


public class LoadingDialog extends BaseDialog {

    public static final String TAG = LoadingDialog.class.getName();
    private String loading;
    private int layout;

    public LoadingDialog(Context context, @LayoutRes int layout, String loading) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.loading = loading;
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
    }

    @Override
    protected void onBaseCreate() {

    }

    @Override
    protected void onBindView() {
        TextView loading_dialog_tv_loading = (TextView) findViewById(R.id.loading_dialog_tv_loading);
        View loading_dialog_progress = findViewById(R.id.loading_dialog_progress);
        try {
            if (loading_dialog_tv_loading == null)
                DLog.d(TAG, "Missing @id/loading_dialog_tv_loading in layout xml");
            if (loading_dialog_progress == null)
                DLog.d(TAG, "Missing @id/loading_dialog_progress in layout xml");

            if (loading_dialog_tv_loading != null) {
                if (!Utils.isEmpty(loading))
                    loading_dialog_tv_loading.setText(loading);
            }

        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
        }
    }

}
