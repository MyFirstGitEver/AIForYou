package com.example.aiforyou.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.DialogFragment;

import com.example.aiforyou.R;
import com.example.aiforyou.mytools.MyTimer;

public class StartUpFragment extends DialogFragment {
    private TextView animatingTxt;
    private MotionLayout startUpAnimator;
    private MyTimer timer;

    private int dots = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_up, container, false);

        animatingTxt = view.findViewById(R.id.animatingTxt);
        startUpAnimator = view.findViewById(R.id.startUpAnimator);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.startUpAnimator.transitionToEnd();

        this.timer = new MyTimer((mils) -> {
            dots = (dots + 1) % 4;

            String dotString;

            switch (dots) {
                case 0:
                    dotString = "";
                    break;
                case 1:
                    dotString = ".";
                    break;
                case 2:
                    dotString = "..";
                    break;
                default:
                    dotString = "...";
            }

            animatingTxt.setText(getString(R.string.login_title, dotString));

            if(mils == 600) {
                timer.stop();
                dismiss();
            }
        }).start(100);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setWindowAnimations(R.style.slideStyle);

        return dialog;
    }
}