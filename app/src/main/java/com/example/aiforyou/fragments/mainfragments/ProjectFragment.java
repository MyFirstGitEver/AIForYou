package com.example.aiforyou.fragments.mainfragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.adapters.listadapters.NotificationListAdapter;
import com.example.aiforyou.adapters.listadapters.ProjectListAdapter;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ShareDTO;
import com.example.aiforyou.dialogs.CreateNewProjectDialog;
import com.example.aiforyou.fragments.workspaces.WorkSpaceFragment;
import com.example.aiforyou.services.AIService;
import com.example.aiforyou.services.UserService;
import com.example.aiforyou.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectFragment extends Fragment {
    public interface ProjectClickListener {
        void projectClick(@Nullable  ProjectDTO project, int position);
    }

    private RecyclerView projectList;

    private MainActivityViewModel mainActivityViewModel;

    private final ProjectClickListener projectClickListener = (project, position) -> {
        if(project == null) {
            new CreateNewProjectDialog().show(getParentFragmentManager(), "create new");
        }
        else if(project.getType() == ProjectDTO.ProjectType.STATS) {
            opensWorkSpace(position);
        }
    };
    private View notificationWindow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationWindow =
                LayoutInflater.from(getContext()).inflate(R.layout.notification_window, container, false);
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        ImageButton drawerBtn = view.findViewById(R.id.backBtn);
        ImageButton notificationBtn = view.findViewById(R.id.notificationBtn);

        projectList = view.findViewById(R.id.projectList);

        drawerBtn.setOnClickListener((button) -> getParentFragmentManager().setFragmentResult("drawer", null));
        notificationBtn.setOnClickListener((button) -> {
            notificationWindowInit();

            PopupWindow window = new PopupWindow(notificationWindow,
                    ViewGroup.LayoutParams.MATCH_PARENT, 600);
            window.setOutsideTouchable(true);
            window.showAsDropDown(button);
        });

        projectList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        projectList.setAdapter(new ProjectListAdapter(mainActivityViewModel.getUser().getProjects(), projectClickListener));
        getParentFragmentManager().setFragmentResultListener("filled", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                ProjectDTO form = result.getParcelable("form");

                mainActivityViewModel.addNewProject(form);
                ((ProjectListAdapter) Objects.requireNonNull(projectList.getAdapter())).addNewProject();

                opensWorkSpace(projectList.getAdapter().getItemCount() - 2); // look at its implementation for why
            }
        });

        getParentFragmentManager().setFragmentResultListener("open shared", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                opensWorkSpace(result.getInt("sharedPosition"));
                ((ProjectListAdapter)projectList.getAdapter()).addNewProject();
            }
        });

        getParentFragmentManager().setFragmentResultListener("new shared", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                RecyclerView notificationList = notificationWindow.findViewById(R.id.notificationList);

                if(mainActivityViewModel.getNotifcations() != null) {
                    mainActivityViewModel.addNewNotification(result.getParcelable("new share"));
                }

                if(notificationList.getAdapter() != null) {
                    ((NotificationListAdapter)notificationList.getAdapter()).notifyItemInserted(0);
                }
            }
        });

        getParentFragmentManager().setFragmentResultListener("deleted", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(projectList.getAdapter() != null) {
                    // accounting for 'new project' button
                    ((ProjectListAdapter)projectList.getAdapter()).deleteAt(result.getInt("position") + 1);
                }
            }
        });
    }

    private void opensWorkSpace(int position) {
        WorkSpaceFragment workSpaceFragment = new WorkSpaceFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("projectPosition", position);

        workSpaceFragment.setArguments(bundle);
        workSpaceFragment.show(getParentFragmentManager(), "workspace");
    }

    private void notificationWindowInit() {
        RecyclerView notificationList = notificationWindow.findViewById(R.id.notificationList);
        notificationList.setLayoutManager(new LinearLayoutManager(getContext()));

        if(mainActivityViewModel.getNotifcations() == null) {
            UserService.service.getNotifications(mainActivityViewModel.getUser().getTenDn()).enqueue(new Callback<List<ShareDTO>>() {
                @Override
                public void onResponse(Call<List<ShareDTO>> call, Response<List<ShareDTO>> response) {
                    RecyclerView notificationList = notificationWindow.findViewById(R.id.notificationList);

                    mainActivityViewModel.setNotifcations(response.body());
                    notificationList.setAdapter(
                            new NotificationListAdapter(mainActivityViewModel.getNotifcations()));
                }

                @Override
                public void onFailure(Call<List<ShareDTO>> call, Throwable t) {
                }
            });
        }
        else if(notificationList.getAdapter() == null) {
            notificationList.setAdapter(
                    new NotificationListAdapter(mainActivityViewModel.getNotifcations()));
        }
    }
}