package com.example.aiforyou.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.aiforyou.R;
import com.example.aiforyou.adapters.listadapters.FriendListAdapter;
import com.example.aiforyou.adapters.pageradapters.MainViewPagerAdapter;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ShareDTO;

import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.dialogs.ErrorDialog;

import com.example.aiforyou.services.AIService;
import com.example.aiforyou.services.UserService;

import com.example.aiforyou.mytools.STOMPConnection;
import com.example.aiforyou.viewmodels.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public interface OnFriendBoxListener {
        void onFriendBox(ShareDTO box);
    }

    private ViewPager2 mainPager;
    private DrawerLayout drawer;

    private STOMPConnection stompConnection;

    private MainActivityViewModel viewModel;

    private final OnFriendBoxListener onFriendBoxListener = (box) -> {
        AIService.service.loadProject(box.getIdProject(), viewModel.getUser().getId()).enqueue(new Callback<ProjectDTO>() {
            @Override
            public void onResponse(Call<ProjectDTO> call, Response<ProjectDTO> response) {
                int position = viewModel.registerShared(response.body());

                Bundle bundle = new Bundle();
                bundle.putInt("sharedPosition", position);

                getSupportFragmentManager().setFragmentResult("open shared", bundle);
            }

            @Override
            public void onFailure(Call<ProjectDTO> call, Throwable t) {
                openErrorDialog("Can't load this project! We're sorry :(");
            }
        });
    };

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getData() == null) {
                    return;
                }

                Uri uri = result.getData().getData();

                Bundle bundle = new Bundle();
                bundle.putParcelable("uri", uri);
                getSupportFragmentManager().setFragmentResult("done picking",bundle);
            });

    private final STOMPConnection.MessageReceivedListener messageReceivedListener = (message) -> {
        Gson decodingGson = new Gson();
        ShareDTO decodedShare = decodingGson.fromJson(message, ShareDTO.class);

        runOnUiThread(() -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("new share", decodedShare);
            getSupportFragmentManager().setFragmentResult("new shared", bundle);

            RecyclerView list = drawer.findViewById(R.id.friendList);
            FriendListAdapter adapter = (FriendListAdapter) list.getAdapter();
            if(adapter == null) {
                return; // do nothing we haven't open drawer
            }

            adapter.addNewShare(decodedShare);
        });

        try {
            Bitmap bm = Glide.with(this).asBitmap().load(decodedShare.getAnhNguoiGui()).submit().get();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this, getString(R.string.notification_channel))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(bm)
                    .setContentTitle(decodedShare.getTenNguoiGui())
                    .setContentText(decodedShare.getTenNguoiGui() + " shared something  to you!")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(decodedShare.getTenNguoiGui() + " shared to you project \"" +
                                    decodedShare.getTenProject() + "\""))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(decodedShare.getIdProject(), builder.build());
        } catch (JsonSyntaxException e) {
            openErrorDialog("Your project not saved to cloud yet!");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if(viewModel.getUser() == null) {
            viewModel.setUser(getIntent().getParcelableExtra("user"));
        }

        mainPager = findViewById(R.id.mainPager);
        drawer = findViewById(R.id.drawer);

        BottomNavigationView bottomBar = findViewById(R.id.bottomBar);

        mainPager.setAdapter(new MainViewPagerAdapter(this));
        mainPager.setUserInputEnabled(false);

        bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.project) {
                    mainPager.setCurrentItem(0);
                }
                else {
                    mainPager.setCurrentItem(1);
                }

                return true;
            }
        });

        stompConnection = new STOMPConnection(messageReceivedListener);
        stompConnection.subscribe(((UserDTO)getIntent().getParcelableExtra("user")).getId());

        getSupportFragmentManager().setFragmentResultListener("drawer", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                loadShares();
            }
        });

        getSupportFragmentManager().setFragmentResultListener("picker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");

                launcher.launch(chooseFile);
            }
        });

        getSupportFragmentManager().setFragmentResultListener("share", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                stompConnection.sendMessage(result.getParcelable("share"), "share");
                Toast.makeText(MainActivity.this, "Shared successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openErrorDialog(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);

        DialogFragment dialog = new ErrorDialog();
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "error");
    }

    private void loadShares() {
        if(viewModel.getShares() == null) {
            UserService.service.getFriendBoxes(viewModel.getUser().getTenDn()).enqueue(new Callback<List<ShareDTO>>() {
                @Override
                public void onResponse(Call<List<ShareDTO>> call, Response<List<ShareDTO>> response) {
                    viewModel.setShares(response.body());
                    openDrawer();
                }

                @Override
                public void onFailure(Call<List<ShareDTO>> call, Throwable t) {
                    openErrorDialog("Failed to load your friends list");
                }
            });
        }
        else {
            openDrawer();
        }
    }

    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
        RecyclerView list = drawer.findViewById(R.id.friendList);

        if(list.getAdapter() == null) {
            list.setAdapter(new FriendListAdapter(viewModel.getShares(), onFriendBoxListener));
            list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }
    }
}