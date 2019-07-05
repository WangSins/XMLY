package com.example.wsins.xmly.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.wsins.xmly.R;

/**
 * Created by Sin on 2019/7/4
 */
public class ConfirmDialog extends Dialog {

    private View cancelSub;
    private View giveUp;
    private OnDialogActionClickListener clickListener = null;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.ConfirmDialog);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        cancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
        giveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onGiveUpClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        cancelSub = this.findViewById(R.id.dialog_cancel_sub_tv);
        giveUp = this.findViewById(R.id.dialog_give_up_tv);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnDialogActionClickListener {
        void onCancelSubClick();

        void onGiveUpClick();
    }
}
