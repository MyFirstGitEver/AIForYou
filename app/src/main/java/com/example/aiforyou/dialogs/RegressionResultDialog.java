package com.example.aiforyou.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.RegressionParams;

public class RegressionResultDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.regression_result_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView wTxt = view.findViewById(R.id.wTxt);
        TextView bTxt = view.findViewById(R.id.bTxt);
        TextView accuracyTxt = view.findViewById(R.id.errorTxt);

        RegressionParams params = getArguments().getParcelable("params");

        wTxt.setText(String.format("Vector w: \n%s", params.getW().toString()));
        bTxt.setText(String.format("bias: %s", params.getB()));
        accuracyTxt.setText(String.format("error estimated: %s", params.getError()));
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setWindowAnimations(R.style.slideStyle2);

        return dialog;
    }
}