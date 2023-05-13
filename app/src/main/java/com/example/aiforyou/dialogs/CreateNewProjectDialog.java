package com.example.aiforyou.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aiforyou.R;
import com.example.aiforyou.adapters.pageradapters.CreateProjectPagerAdapter;
import com.example.aiforyou.custom.ProjectDTO;

public class CreateNewProjectDialog extends DialogFragment {
    public interface OnFillingFormListener {
        void onFillingForm(ProjectDTO form, int step);
    }

    private ViewPager2 infoPager;

    private final OnFillingFormListener onFillingFormListener = (form, step) -> {
        if(step == 1) {
            Bundle result = new Bundle();
            result.putParcelable("form", form);
            getParentFragmentManager().setFragmentResult("filled", result);

            dismiss();
        }
        else {
            infoPager.setCurrentItem(1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_new_project_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backBtn = view.findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            if(infoPager.getCurrentItem() == 0) {
                dismiss();
            }
            else {
                infoPager.setCurrentItem(0);
            }
        });

        infoPager = view.findViewById(R.id.infoPager);
        infoPager.setAdapter(new CreateProjectPagerAdapter(onFillingFormListener));
        infoPager.setUserInputEnabled(false);
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

        dialog.getWindow().setWindowAnimations(R.style.slideStyle);

        return dialog;
    }
}