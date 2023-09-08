package com.example.aiforyou.services;

import com.example.aiforyou.custom.QuanHeDTO;
import com.example.aiforyou.custom.ShareDTO;
import com.example.aiforyou.entities.ProjectEntity;
import com.example.aiforyou.entities.QuanHeEntity;
import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.mytools.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    UserService service = new Retrofit.Builder()
            .baseUrl("http://" + Constants.DOMAIN + ":8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(UserService.class);

    @GET("/api/user/login/{name}/{pass}")
    Call<UserDTO> login(
            @Path("name") String name,
            @Path("pass") String password);

    @GET("/api/user/share/list/{name}")
    Call<QuanHeDTO[]> fetchShareList(@Path("name") String name);

    @GET("/api/user/friends/{userName}")
    Call<List<ShareDTO>> getFriendBoxes(@Path("userName") String userName);

    @GET("/api/user/notification/{userName}")
    Call<List<ShareDTO>> getNotifications(@Path("userName") String userName);

    @POST("/api/user/buy/{code}/{userName}")
    Call<Boolean> buy(@Path("code") int code, @Path("userName") String userName);

    @POST("/api/user/save")
    Call<ProjectEntity> save(@Body ProjectEntity project);
}