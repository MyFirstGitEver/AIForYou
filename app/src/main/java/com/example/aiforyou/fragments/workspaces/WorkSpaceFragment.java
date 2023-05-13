package com.example.aiforyou.fragments.workspaces;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.aiforyou.R;
import com.example.aiforyou.custom.RegressionParams;
import com.example.aiforyou.dialogs.ClusterResultDialog;
import com.example.aiforyou.dialogs.LoadingDatasetDialog;
import com.example.aiforyou.dialogs.RegressionResultDialog;
import com.example.aiforyou.dialogs.ShareDialog;
import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;

import com.example.aiforyou.adapters.pageradapters.DataViewPagerAdapter;

import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.dialogs.ErrorDialog;

import com.example.aiforyou.entities.ProjectEntity;
import com.example.aiforyou.services.AIService;
import com.example.aiforyou.services.UserService;
import com.example.aiforyou.viewmodels.MainActivityViewModel;
import com.example.aiforyou.viewmodels.WorkSpaceFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkSpaceFragment extends DialogFragment {
    public interface QueryBarListener {
        void showQueryBar(boolean hide);
        void invalidQuery();
    }

    public interface  ExcelChooser {
        void choose();
    }

    private MainActivityViewModel mainActivityViewModel;
    private WorkSpaceFragmentViewModel workSpaceFragmentViewModel;
    private FrameLayout workSpace;
    private TextView projectNameTxt;
    private final ExcelChooser chooser = () ->
            getParentFragmentManager().setFragmentResult("picker", null);

    private final UploadCallback uploadCallback = new UploadCallback() {
        @Override
        public void onStart(String requestId) {

        }

        @Override
        public void onProgress(String requestId, long bytes, long totalBytes) {

        }

        @Override
        public void onSuccess(String requestId, Map resultData) {
            UserService.service.save(new ProjectEntity(
                    mainActivityViewModel.getUser().getId(),
                    getProject().getName(),
                    (String) resultData.get("url"),
                    new Date())).enqueue(new Callback<ProjectEntity>() {
                @Override
                public void onResponse(Call<ProjectEntity> call, Response<ProjectEntity> response) {
                    if(response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                        openErrorDialog("This project already saved to cloud!");
                        return;
                    }

                    mainActivityViewModel.save(workSpaceFragmentViewModel.getProjectPosition(), response.body());
                    Toast.makeText(getContext(), "Great! Your project is saved!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ProjectEntity> call, Throwable t) {
                    openErrorDialog("Failed to store your project! We're sorry! :(");
                }
            });
        }

        @Override
        public void onError(String requestId, ErrorInfo error) {
            int m = 3;
        }

        @Override
        public void onReschedule(String requestId, ErrorInfo error) {
        }
    };

    private final View.OnClickListener saveToCloudListener = (v) -> {
        if(getProject().getUri() == null) {
            openErrorDialog("No dataset found!");
            return; // won't upload if no dataset found!
        }

        if(getProject().getId() != null) {
            openErrorDialog("This project already saved to cloud");
            return;
        }

        MediaManager.get().upload(getProject().getUri()).option("resource_type", "raw").
                unsigned("v6qn7z7n").callback(uploadCallback).dispatch();
    };

    private final View.OnClickListener shareListener = (v) -> {
        if(getProject().getUri() == null) {
            openErrorDialog("Can't share an empty project :(");
            return;
        }

        if(getProject().getId() == null) {
            openErrorDialog("This project not saved to cloud yet!");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("projectId", getProject().getId());

        ShareDialog dialog = new ShareDialog();
        dialog.setArguments(bundle);
        dialog.show(getParentFragmentManager(), "share");
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_space, container, false);

        workSpace = view.findViewById(R.id.workSpace);
        projectNameTxt = view.findViewById(R.id.projectNameTxt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        workSpaceFragmentViewModel = new ViewModelProvider(this).get(WorkSpaceFragmentViewModel.class);
        workSpaceFragmentViewModel.setProjectId(getArguments().getInt("projectPosition"));

        projectNameTxt.setText(getProject().getName());

        if(getProject().getUri() == null) {
            if(getProject().getDiaChiExcel() != null) {
                saveToWorkspaceAndLoadAsync();
                return;
            }

            workSpace.addView(bindNoDataset());
        }
        else {
            loadDatasetAsync();
        }

        getParentFragmentManager().setFragmentResultListener("done picking", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                mainActivityViewModel.saveUriAt(
                        workSpaceFragmentViewModel.getProjectPosition(), result.getParcelable("uri"));
                loadDatasetAsync();
            }
        });

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> dismiss());

        FloatingActionButton saveToCloudBtn = view.findViewById(R.id.saveToCloudBtn);
        saveToCloudBtn.setOnClickListener(saveToCloudListener);

        ImageButton shareBtn = view.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(shareListener);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ViewPager2 dataPager = workSpace.findViewById(R.id.dataPager);

        if(dataPager != null) {
            workSpaceFragmentViewModel.setQuery(((DataViewPagerAdapter)dataPager.getAdapter()).getCurrentQuery());
        }
    }

    public ProjectDTO getProject() {
        return mainActivityViewModel.getProject(workSpaceFragmentViewModel.getProjectPosition());
    }

    private void loadDatasetAsync() {
        new Thread(() -> {
            DialogFragment dialog = new LoadingDatasetDialog();
            dialog.show(getParentFragmentManager(), "loading");

            ExcelReader reader = buildReader(getProject().getUri());

            if(getActivity() == null) {
                failToInitialise();
                return;
            }

            getActivity().runOnUiThread(() -> onDoneLoading(reader));
        }).start();
    }

    // TODO: JUST FOR TESTING!
    private void saveToWorkspaceAndLoadAsync() {
        DialogFragment dialog = new LoadingDatasetDialog();
        dialog.show(getParentFragmentManager(), "loading");

        new Thread(() -> {
            if(getActivity() == null) {
                failToInitialise();
                return;
            }

            File dir = new File(getActivity().getFilesDir(), "workspace");

            int index = 0;
            if(!dir.exists()) {
                dir.mkdir();
            }
            else {
                index = dir.listFiles().length;
            }

            File f = new File(dir, index + ".xlsx");
            try {
                if(getActivity() == null) {
                    failToInitialise();
                    return;
                }

                URL url = new URL(getProject().getDiaChiExcel());
                FileUtils.copyInputStreamToFile(url.openStream(), f);

                Uri uri = Uri.fromFile(f);
                mainActivityViewModel.saveUriAt(workSpaceFragmentViewModel.getProjectPosition(), uri);
                ExcelReader reader = buildReader(uri);

                getActivity().runOnUiThread(() -> onDoneLoading(reader));
            } catch (MalformedURLException e) {
                openErrorDialog("Check your internet connection! We can't load your dataset :(");
            } catch (IOException e) {
                openErrorDialog("Can't save dataset to your disk!");
            }
        }).start();
    }

    private void onDoneLoading(ExcelReader reader) {
        if(reader == null) {
            failToInitialise();
            dismiss();
            return;
        }

        workSpace.removeAllViews();
        workSpace.addView(bindDataset(reader));
        getParentFragmentManager().setFragmentResult("close", null);
    }

    private void failToInitialise() {
        getParentFragmentManager().setFragmentResult("close", null);
        dismiss();
    }

    private View bindNoDataset() {
        View view = getLayoutInflater().inflate(R.layout.no_dataset_layout, workSpace, false);

        Button loadDatasetBtn = view.findViewById(R.id.loadDatasetBtn);
        loadDatasetBtn.setOnClickListener((button) -> chooser.choose());

        return view;
    }

    private View bindDataset(ExcelReader reader) {
        View itemView = getLayoutInflater().inflate(R.layout.dataset_layout, workSpace, false);

        TabLayout tabContainer = itemView.findViewById(R.id.colContainer);
        ViewPager2 dataPager = itemView.findViewById(R.id.dataPager);
        ImageButton queryBtn = itemView.findViewById(R.id.queryBtn);

        MotionLayout queryAnimator = itemView.findViewById(R.id.queryAnimator);

        TextView fileNameTxt = itemView.findViewById(R.id.fileNameTxt);
        TextView dateTxt = itemView.findViewById(R.id.dateTxt);

        fileNameTxt.setText(getProject().getUri().getLastPathSegment());
        dateTxt.setText(new SimpleDateFormat("dd-MM-yyyy").format(getProject().getDate()));

        tabContainer.setTabMode(TabLayout.MODE_SCROLLABLE);
        String[] headers = reader.getHeaders(0);

        dataPager.setAdapter(new DataViewPagerAdapter(reader, new QueryBarListener() {
            @Override
            public void showQueryBar(boolean hide) {
                if (hide) {
                    queryAnimator.transitionToStart();
                } else {
                    queryAnimator.transitionToEnd();
                }
            }

            @Override
            public void invalidQuery() {
                openErrorDialog("Your query is invalid!");
            }
        }, workSpaceFragmentViewModel.getQuery(), headers.length));

        // Sync tabs with ViewPager2
        new TabLayoutMediator(tabContainer, dataPager, (tab, pos) -> {
            tab.setText(headers[pos]);
        }).attach();

        queryBtn.setOnClickListener(v -> {
            EditText queryEditTxt = itemView.findViewById(R.id.queryEditTxt);

            String query = queryEditTxt.getText().toString();

            if(query.length() == 0 || query.charAt(0) != '\\') {
                ((DataViewPagerAdapter)dataPager.getAdapter()).query(query);
                queryAnimator.transitionToStart(); // hide it
            }
            else {
                if(getProject().getId() == null) {
                    openErrorDialog("Can't start your model! Please save your project to cloud first!");
                    return;
                }

                Pair<ProjectDTO.ProjectType, String[]> args;
                try {
                    args = reader.toolQuery(query);

                    switch (args.first) {
                        case PIE:
                        case HIST1:
                        case HIST2:
                        case SCATTER:
                        case SEGMENTS:
                            StatisticsFragment statisticsFragment = new StatisticsFragment();
                            Bundle bundle = new Bundle();

                            bundle.putStringArray("args", args.second);
                            bundle.putInt("type", args.first.ordinal());
                            bundle.putParcelable("uri", getProject().getUri());

                            statisticsFragment.setArguments(bundle);
                            statisticsFragment.show(getParentFragmentManager(),"stats");

                            break;
                        default:
                            train(args, reader);
                    }
                } catch (Exception e) {
                    openErrorDialog("Your query is invalid");
                }
            }
        });

        return itemView;
    }

    private void train(Pair<ProjectDTO.ProjectType, String[]> args, ExcelReader reader) {
        List<String> features;

        if(args.second.length == 1) {
            features = new ArrayList<>(Arrays.asList(reader.getHeaders(0)));
            features.remove(args.second[0].toLowerCase()); // remove predict column
        }
        else {
            features = Arrays.asList(Arrays.copyOfRange(args.second, 1, args.second.length));
        }

        switch (args.first) {
            case LR:
                AIService.service.getLRTrainParams(
                        mainActivityViewModel.getUser().getTenDn(),
                        reader.getColId(0, args.second[0]),
                        features,
                        getProject().getDiaChiExcel()
                        ).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                            openErrorDialog("Something wrong happens :(");
                            return;
                        }

                        showRegressionResult(response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        openErrorDialog("Can't connect to server! :(");
                    }
                });
                break;
            case LOR:
                AIService.service.getLoRTrainParams(
                        mainActivityViewModel.getUser().getTenDn(),
                        reader.getColId(0, args.second[0]),
                        features,
                        getProject().getDiaChiExcel()
                ).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                            openErrorDialog("Something wrong happens :(");
                            return;
                        }
                        else if(response.code() == HttpURLConnection.HTTP_BAD_METHOD) {
                            openErrorDialog("Predict column is not binary!");
                            return;
                        }
                        else if(response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                            openErrorDialog("Your model usage has exceeded the limit(five times per unpurchased model)");
                            return;
                        }

                        showRegressionResult(response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        openErrorDialog("Can't connect to server! :(");
                    }
                });
                break;
            default:
                AIService.service.cluster(
                        mainActivityViewModel.getUser().getTenDn(),
                        Integer.parseInt(args.second[0]), getProject().getDiaChiExcel(), features).enqueue(
                        new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                    openErrorDialog("Something wrong happens :(");
                                    return;
                                }

                                Gson gson = new Gson();
                                int[] indexes = gson.fromJson(response.body(), int[].class);

                                Bundle bundle = new Bundle();
                                bundle.putIntArray("params", indexes);
                                bundle.putInt("group", Integer.parseInt(args.second[0]));
                                bundle.putInt("projectPosition", workSpaceFragmentViewModel.getProjectPosition());

                                ClusterResultDialog dialog = new ClusterResultDialog();
                                dialog.setArguments(bundle);
                                dialog.show(getParentFragmentManager(), "result");
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                openErrorDialog("Can't connect to server! :(");
                            }
                        }
                );
        }
    }

    private void showRegressionResult(String json) {
        Gson gson = new Gson();
        RegressionParams params = gson.fromJson(json, RegressionParams.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("params", params);

        RegressionResultDialog dialog = new RegressionResultDialog();
        dialog.setArguments(bundle);
        dialog.show(getParentFragmentManager(), "result");
    }

    private ExcelReader buildReader(Uri uri) {
        if(getActivity() == null) {
            openErrorDialog("Something goes wrong with the system :(");
            return null;
        }

        try {
            InputStream stream = getActivity().getContentResolver().openInputStream(uri);
            ExcelReader reader = new ExcelReader(stream);

            mainActivityViewModel.saveUriAt(workSpaceFragmentViewModel.getProjectPosition(), uri);
            stream.close();

            return reader;
        } catch (IOException e) {
            openErrorDialog("Something goes wrong with the system");
            return null;
        }
        catch (NotOfficeXmlFileException e) {
            openErrorDialog("Can't recognise this file format! Sorry :(");
            return null;
        }
    }

    private void openErrorDialog(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);

        DialogFragment dialog = new ErrorDialog();
        dialog.setArguments(bundle);

        dialog.show(getParentFragmentManager(), "error");
    }
}