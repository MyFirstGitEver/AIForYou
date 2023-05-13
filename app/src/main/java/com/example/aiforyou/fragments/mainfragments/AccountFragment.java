package com.example.aiforyou.fragments.mainfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiforyou.R;
import com.example.aiforyou.mytools.MyTimer;
import com.example.aiforyou.adapters.listadapters.DetailsListAdapter;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ReceiptDTO;
import com.example.aiforyou.dialogs.ErrorDialog;

import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.fragments.ServiceFragment;
import com.example.aiforyou.interfaces.DetailsItem;
import com.example.aiforyou.services.AIService;

import com.example.aiforyou.viewmodels.AccountFragmentViewModel;
import com.example.aiforyou.viewmodels.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    public interface OnDeleteProjectListener {
        void onDeleteProjectListener(int position);
    }

    private AccountFragmentViewModel accountFragmentViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView detailsList;
    private final View.OnClickListener seeMoreClickListener = (v) -> {
        ServiceFragment serviceFragment = new ServiceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userName", getUser().getTenDn());

        serviceFragment.setArguments(bundle);
        serviceFragment.show(getParentFragmentManager(), "service");
    };

    private final OnDeleteProjectListener onDeleteProjectListener = position -> {
        ProjectDTO project = mainActivityViewModel.deleteAt(position);
        ((DetailsListAdapter)detailsList.getAdapter()).deleteAt(position);

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        getParentFragmentManager().setFragmentResult("deleted", bundle);

        AIService.service.deleteProject(project.getId(), getUser().getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.code() != HttpURLConnection.HTTP_OK) {
                    openErrorDialog("This project is not yours or already corrupted!");
                }
                else {
                    Toast.makeText(getContext(), "Deleted your project!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                openErrorDialog("This project is not yours or already corrupted!");
            }
        });
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountFragmentViewModel = new ViewModelProvider(this).get(AccountFragmentViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        TextView tenDnTxt = view.findViewById(R.id.tenDnTxt);
        ImageView userImg = view.findViewById(R.id.userImg);
        TabLayout tabContainer = view.findViewById(R.id.colContainer);

        detailsList = view.findViewById(R.id.detailsList);

        tabContainer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() != accountFragmentViewModel.getCurrentTabIndex()) {
                    if(tab.getPosition() == 0) {
                        loadReceipts();
                    }
                    else {
                        DetailsListAdapter adapter = (DetailsListAdapter) detailsList.getAdapter();
                        adapter.loadProjects(getUser().getProjects());
                    }

                    accountFragmentViewModel.setCurrentTabIndex(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tenDnTxt.setText(getUser().getTenDn());
        Glide.with(this).load(getUser().getAvatar()).into(userImg);

        if(accountFragmentViewModel.getCurrentTabIndex() == 0) {
            loadReceipts();
        }
        else {
            List<DetailsItem> details = new ArrayList<>();
            details.addAll(mainActivityViewModel.getUser().getProjects());

            detailsList.setAdapter(new DetailsListAdapter(details, seeMoreClickListener, onDeleteProjectListener));
        }
    }

    private void loadReceipts() {
        if(detailsList.getAdapter() == null) {
            List<DetailsItem> details = new ArrayList<>();
            details.add(null);

            detailsList.setAdapter(new DetailsListAdapter(details, seeMoreClickListener, onDeleteProjectListener));
            detailsList.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else {
            ((DetailsListAdapter)detailsList.getAdapter()).showLoad();
        }

        new MyTimer((mils) -> {
            AIService.service.purchaseHistory(getUser().getTenDn()).enqueue(new Callback<ReceiptDTO[]>() {
                @Override
                public void onResponse(Call<ReceiptDTO[]> call, Response<ReceiptDTO[]> response) {
                    DetailsListAdapter adapter = (DetailsListAdapter) detailsList.getAdapter();
                    accountFragmentViewModel.loadReceipts(response.body()); // save it to viewModel for later use
                    adapter.loadReceipts(response.body());
                }

                @Override
                public void onFailure(Call<ReceiptDTO[]> call, Throwable t) {
                    openErrorDialog("Failed to load your purchase history! :(");
                }
            });
        }).tick(600);
    }

    private UserDTO getUser() {
        return mainActivityViewModel.getUser();
    }

    private void openErrorDialog(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);

        ErrorDialog dialog = new ErrorDialog();
        dialog.setArguments(bundle);
        dialog.show(getParentFragmentManager(), "error");
    }
}