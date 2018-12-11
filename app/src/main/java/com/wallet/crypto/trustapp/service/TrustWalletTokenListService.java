package com.wallet.crypto.trustapp.service;

import android.util.Log;

import com.google.gson.Gson;
import com.wallet.crypto.trustapp.entity.Token;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class TrustWalletTokenListService implements TokenListService {

    private final OkHttpClient httpClient;
    private final Gson gson;
    private TrustWalletTokenListService.ApiClient apiClient;

    public interface CallBack {
        void onSuccess(Token[] tokens);
        void onError();
    }

    public TrustWalletTokenListService(
            OkHttpClient httpClient,
            Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
        buildApiClient("https://api.trustwalletapp.com/");
    }

    private void buildApiClient(String baseUrl) {
        apiClient = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TrustWalletTokenListService.ApiClient.class);
    }

    public interface ApiClient {
        @GET("tokens/list?")
        Call<Response<TrustWalletTokenListService.TrustResponse>> fetchTokenList(
                @Query("query") String query,
                @Query("networks") String networkString);
    }

    private static class TrustResponse {
        Token[] response;
    }

    public void fetchTokens(String query, int[] networks, CallBack callBack) {
        String networkString = buildNetworkString(networks);
        Call<Response<TrustWalletTokenListService.TrustResponse>> call =
                apiClient.fetchTokenList(query, networkString);
        call.enqueue(new Callback<Response<TrustResponse>>() {
            @Override
            public void onResponse(Call<Response<TrustResponse>> call, Response<Response<TrustResponse>> response) {
                if (response.isSuccessful()) {
                    Token[] token = response.body().body().response;
                    callBack.onSuccess(token);
                } else {
                    Log.d("Error", response.errorBody().toString());
                    callBack.onError();
                }
            }

            @Override
            public void onFailure(Call<Response<TrustResponse>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                callBack.onError();
            }
        });
    }

    private String buildNetworkString(int[] services) {
        if (services.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(services[0]);
        for (int i = 1; i < services.length; i++) {
            builder.append("," + services[i]);
        }
        return builder.toString();
    }

}
