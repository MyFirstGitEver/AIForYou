package com.example.aiforyou.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aiforyou.R;
import com.example.aiforyou.custom.QuanHeDTO;
import com.example.aiforyou.entities.ShareEntity;
import com.example.aiforyou.services.UserService;
import com.example.aiforyou.viewmodels.MainActivityViewModel;
import com.example.aiforyou.viewmodels.ShareDialogViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareDialog extends DialogFragment {
    private Spinner shareList;
    private ShareDialogViewModel shareDialogViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareDialogViewModel = new ViewModelProvider(this).get(ShareDialogViewModel.class);
        shareDialogViewModel.setProjectId(getArguments().getInt("projectId"));

        shareList = view.findViewById(R.id.shareList);

        if(shareDialogViewModel.getQuanHes() == null) {
            UserService.service.fetchShareList(
                    new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUser().getTenDn())
                    .enqueue(new Callback<QuanHeDTO[]>() {
                        @Override
                        public void onResponse(Call<QuanHeDTO[]> call, Response<QuanHeDTO[]> response) {
                            shareDialogViewModel.setQuanHes(response.body());
                            loadSpinner();
                        }

                        @Override
                        public void onFailure(Call<QuanHeDTO[]> call, Throwable t) {

                        }
                    });
        }
        else {
            loadSpinner();
        }

        Button shareBtn = view.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(v -> {
            share();
            dismiss();
        });
    }

    private void share() {
        ShareEntity share = new ShareEntity(
                shareDialogViewModel.getQuanHes()[shareList.getSelectedItemPosition()].getIdNguoiNhan(),
                new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUser().getId(),
                shareDialogViewModel.getProjectId(), new Date());

        Bundle bundle = new Bundle();
        bundle.putParcelable("share", share);
        getParentFragmentManager().setFragmentResult("share", bundle);
    }

    private void loadSpinner() {
        List<String> data = new ArrayList<>(shareDialogViewModel.getQuanHes().length);

        for(QuanHeDTO quanHe : shareDialogViewModel.getQuanHes()) {
            data.add(quanHe.getTenNguoiNhan()); // TODO: Share!!
        }

        shareList.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, data));
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