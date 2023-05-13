package com.example.aiforyou.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.adapters.listadapters.ServiceListAdapter;
import com.example.aiforyou.custom.ServiceDTO;
import com.example.aiforyou.dialogs.ErrorDialog;
import com.example.aiforyou.services.AIService;
import com.example.aiforyou.services.UserService;
import com.example.aiforyou.viewmodels.ServiceFragmentViewModel;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFragment extends DialogFragment {
    public interface OnServiceClickListener {
        void onServiceClick(ServiceDTO service, int position);
    }

    private ServiceFragmentViewModel serviceFragmentViewModel;

    private RecyclerView serviceList;

    private final OnServiceClickListener onServiceClickListener = (service, position) -> {
        serviceFragmentViewModel.setConsideringServiceHolder(service);
        serviceFragmentViewModel.setLastClickedService(position);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Service purchase");
        alertDialog.setMessage("Do you want to purchase this service?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UserService.service.buy(
                                serviceFragmentViewModel.getConsideringServiceHolder().getId(),
                                getArguments().getString("userName")).enqueue(
                                new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                Toast.makeText(getContext(), "Bought successfully", Toast.LENGTH_SHORT).show();

                                ((ServiceListAdapter)serviceList.getAdapter())
                                        .removeAt(serviceFragmentViewModel.getLastClickedService());

                                serviceFragmentViewModel.setConsideringServiceHolder(null); // discard this service
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                openErrorDialog("Something wrong with our system! Please try again!");

                                serviceFragmentViewModel.setConsideringServiceHolder(null); // discard this service
                            }
                        });
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No, I want to cancel this", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceFragmentViewModel = new ViewModelProvider(this).get(ServiceFragmentViewModel.class);

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> dismiss());

        serviceList = view.findViewById(R.id.serviceList);
        serviceList.setLayoutManager(new LinearLayoutManager(getContext()));

        if(serviceFragmentViewModel.getServices() == null) {
            AIService.service.getUnpurchasedServices(getArguments().getString("userName")).enqueue(new Callback<ServiceDTO[]>() {
                @Override
                public void onResponse(Call<ServiceDTO[]> call, Response<ServiceDTO[]> response) {
                    serviceFragmentViewModel.setServices(response.body());

                    serviceList.setAdapter(new ServiceListAdapter(
                            serviceFragmentViewModel.getServices(),
                            onServiceClickListener));
                }

                @Override
                public void onFailure(Call<ServiceDTO[]> call, Throwable t) {
                    openErrorDialog("Failed to load available services! Please try again! :(");
                }
            });
        }
        else {
            serviceList.setAdapter(new ServiceListAdapter(
                    serviceFragmentViewModel.getServices(),
                    onServiceClickListener));
        }
    }

    private void openErrorDialog(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);

        DialogFragment dialog = new ErrorDialog();
        dialog.setArguments(bundle);

        dialog.show(getParentFragmentManager(), "error");
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
        dialog.getWindow().setWindowAnimations(R.style.slideStyle2);

        return dialog;
    }
}