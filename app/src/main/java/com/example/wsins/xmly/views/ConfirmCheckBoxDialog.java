package com.example.wsins.xmly.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;

import com.example.wsins.xmly.R;

/**
 * Created by Sin on 2019/7/4
 */
public class ConfirmCheckBoxDialog extends Dialog {

    private View cancel;
    private View confirm;
    private OnDialogActionClickListener clickListener = null;
    private CheckBox checkBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        super(context, R.style.ConfirmDialog);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onCancelClick();
                    dismiss();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    boolean isChecked = checkBox.isChecked();
                    clickListener.onConfirmClick(isChecked);
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        cancel = this.findViewById(R.id.dialog_check_box_cancel);
        confirm = this.findViewById(R.id.dialog_check_box_confirm);
        checkBox = this.findViewById(R.id.dialog_check_box);

    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnDialogActionClickListener {
        void onCancelClick();

        void onConfirmClick(boolean isChecked);
    }
}
