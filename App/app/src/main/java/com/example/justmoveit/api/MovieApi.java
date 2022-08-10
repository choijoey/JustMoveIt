package com.example.justmoveit.api;

import com.example.justmoveit.model.Movie;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface MovieApi {
    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)   // 10초동안 커넥션했는데 반응이 없으면 종료
            .readTimeout(10, TimeUnit.SECONDS)      // 보내고 쓰는거 10초로 제한
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)                // 실패했을 때 다시 시도할 것인가
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://i7d207.p.ssafy.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    // 상영 중인 전체 영화 목록
    @GET("/api/movies")
    Call<Movie[]> getMovieList();

    /*// 영화 상영 정보
    @GET("/justmoveit/info")
    Call<JsonArray> getMoviePlayingInfo();
    */
}
