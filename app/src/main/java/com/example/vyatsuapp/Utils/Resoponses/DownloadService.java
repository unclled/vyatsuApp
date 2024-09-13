package com.example.vyatsuapp.Utils.Resoponses;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DownloadService {
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
