package com.example.aiforyou.services;

import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.custom.ReceiptDTO;
import com.example.aiforyou.custom.ServiceDTO;
import com.example.aiforyou.entities.ProjectEntity;
import com.example.aiforyou.mytools.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AIService {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    AIService service = new Retrofit.Builder()
            .baseUrl("http://" + Constants.DOMAIN + ":8080/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(AIService.class);

    @GET("api/projects/history/purchase/{userName}")
    Call<ReceiptDTO[]> purchaseHistory(@Path("userName") String userName);

    @GET("api/projects/unpurchased/{userName}")
    Call<ServiceDTO[]> getUnpurchasedServices(@Path("userName") String userName);

    @GET("api/projects/load/{id}/{requester}")
    Call<ProjectDTO> loadProject(
            @Path("id") int projectId,
            @Path("requester") int requester);

    @GET("api/ai/lr/{requester}/{col}")
    Call<String> getLRTrainParams(
            @Path("requester") String requester,
            @Path("col") int col,
            @Query("features") List<String> features,
            @Query("excelUrl") String url);

    @GET("api/ai/lor/{requester}/{col}")
    Call<String> getLoRTrainParams(
            @Path("requester") String requester,
            @Path("col") int col,
            @Query("features") List<String> features,
            @Query("excelUrl") String url);

    @GET("api/ai/clustering/{requester}/{cluster}")
    Call<String> cluster(@Path("requester") String requester,
                         @Path("cluster") int cluster,
                         @Query("excelUrl") String url,
                         @Query("features") List<String> features);

    @DELETE("api/projects/delete/{id}/{userId}")
    Call<Boolean> deleteProject(@Path("id") int id, @Path("userId") int userId);
}