package com.example.aiforyou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.aiforyou.R;
import com.example.aiforyou.dialogs.ErrorDialog;
import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.fragments.StartUpFragment;
import com.example.aiforyou.services.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText userNameEditTxt;
    private EditText passwordEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameEditTxt = findViewById(R.id.userNameEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);

        loginUsingSaved();

        Button loginBtn = findViewById(R.id.loginBtn);

        if(savedInstanceState == null) {
            deleteLastWorkspace();
            new StartUpFragment().show(getSupportFragmentManager(), "start up");
        }

        loginBtn.setOnClickListener((button) -> {
            String userName = userNameEditTxt.getText().toString();
            String password = passwordEditTxt.getText().toString();
            login(userName, password);
        });
    }

    private void save(String userName, String password) throws IOException {
        File f = new File(getFilesDir(), "login");
        f.createNewFile();

        FileOutputStream fOut = new FileOutputStream(f);
        fOut.write((userName + "\t" + password).getBytes());
        fOut.close();
    }

    private void loginUsingSaved() {
        try {
            File file = new File(getFilesDir(), "login");

            if(!file.exists()){
                return;
            }

            FileInputStream fIn = new FileInputStream(file);

            byte[] loginInfo = new byte[(int) file.length()];
            fIn.read(loginInfo);
            String info = new String(loginInfo);

            int index = info.indexOf('\t');
            String userName = info.substring(0, index);
            String password = info.substring(index + 1);

            login(userName, password);
            fIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openError(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);

        ErrorDialog dialog = new ErrorDialog();
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "error");
    }

    private void login(String userName, String password) {
        UserService.service.login(userName, password).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    openError("Failed to log you in! Please try again!");
                    return;
                }

                try {
                    save(userName, password);
                } catch (IOException e) {
                    openError("Can't save your login information! :(");
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", response.body());

                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                openError("Failed to log you in! Please try again!");
            }
        });
    }

    // TODO: JUST FOR TESTING!
    private void deleteLastWorkspace() {
        File dir = new File(getFilesDir(), "workspace");

        if(dir.listFiles() == null) {
            dir.delete();
            return;
        }

        for(File f : dir.listFiles()) {
            f.delete();
        }

        dir.delete();
    }
}