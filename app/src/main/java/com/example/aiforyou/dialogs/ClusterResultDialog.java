package com.example.aiforyou.dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.adapters.listadapters.DataListAdapter;
import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;
import com.example.aiforyou.viewmodels.ClusterResultViewModel;
import com.example.aiforyou.viewmodels.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClusterResultDialog extends DialogFragment {
    private RecyclerView dataList;
    private TabLayout colContainer;

    private ClusterResultViewModel viewModel;
    private MainActivityViewModel mainActivityViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cluster_result_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ClusterResultViewModel.class);

        if(viewModel.notInitialised()) {
            mainActivityViewModel = new ViewModelProvider(requireActivity())
                    .get(MainActivityViewModel.class);
            initModel(); // async
        }

        ImageButton closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> dismiss());

        colContainer = view.findViewById(R.id.colContainer);
        colContainer.setTabMode(TabLayout.MODE_SCROLLABLE);

        Spinner groupSpinner = view.findViewById(R.id.groupSpinner);
        groupSpinner.setAdapter(buildSpinnerAdapter());

        dataList = view.findViewById(R.id.dataList);
        dataList.setLayoutManager(new LinearLayoutManager(getContext()));

        colContainer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == viewModel.getCurrentTabIndex()) {
                    return;
                }

                viewModel.setCurrentTabIndex(tab.getPosition());

                if(viewModel.notInitialised()) {
                    return;
                }

                dataList.setAdapter(new DataListAdapter(viewModel.getGroupData()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCurrentGroupIndex(position);

                if(viewModel.notInitialised()) {
                    return;
                }

                dataList.setAdapter(new DataListAdapter(viewModel.getGroupData()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayAdapter<String> buildSpinnerAdapter() {
        List<String> dropdown = new ArrayList<>();

        for(int i=0;i<getArguments().getInt("group");i++) {
            dropdown.add("Group " + (i + 1));
        }

        return new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, dropdown);
    }

    private void initModel() {
        DialogFragment loadingDialog = new LoadingDatasetDialog();
        loadingDialog.show(getParentFragmentManager(), "loading");

        viewModel.setIndexes(getArguments().getIntArray("params"));
        viewModel.setProjectPosition(getArguments().getInt("projectPosition"));

        new Thread(() -> {
            Uri excelUri = mainActivityViewModel.getProject(viewModel.getProjectPosition()).getUri();

            try {
                if(getActivity() == null) {
                    failToInitialise();
                    return;
                }

                InputStream inputStream = getActivity().getContentResolver().openInputStream(excelUri);
                viewModel.setReader(new ExcelReader(inputStream));
                inputStream.close();

                getActivity().runOnUiThread(() -> {
                    dataList.setAdapter(new DataListAdapter(viewModel.getGroupData()));

                    String[] headers = viewModel.getReader().getHeaders(0);
                    for (String header : headers) {
                        colContainer.addTab(colContainer.newTab().setText(header));
                    }

                    getParentFragmentManager().setFragmentResult("close", null); // close loading dialog
                });
            } catch (IOException e) {
                failToInitialise();
            }
        }).start();
    }

    private void failToInitialise() {
        getParentFragmentManager().setFragmentResult("close", null);
        dismiss();
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