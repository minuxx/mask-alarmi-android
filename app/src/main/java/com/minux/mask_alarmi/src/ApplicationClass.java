package com.minux.mask_alarmi.src;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;


public class ApplicationClass extends Application {
  /*  public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=uft-8");
    public static MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    //mask map
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final String SEARCH_CONFIRM_KEY = "U01TX0FVVEgyMDIwMDMyMTEyNDExOTEwOTU2NjA="; //address search public api access key for convert address

    //M단위
    public final static int INRADIUS = 1000; // 1M

    // 테스트 서버 주소
    public static String BASE_URL = "http://api.maskalarmi.com/";

    public static String PUBLIC_MASK_URL = "https://8oi9s0nnth.apigw.ntruss.com/";

    public static String PUBLIC_JUSO_URL = "http://www.juso.go.kr/";

//    public static String BASE_URL = "https://adsfgdhfgjhkjk.com/";

    // 실서버 주소
//    public static String BASE_URL = "https://template.softsquared.com/";

    public static SharedPreferences sSharedPreferences = null;

    // SharedPreferences 키 값
    public static String TAG = "TEMPLATE_APP";

    // JWT Token 값
    public static String X_ACCESS_TOKEN = "X-ACCESS-TOKEN";

    //날짜 형식
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    // Retrofit 인스턴스
    public static Retrofit retrofit;

    public static Retrofit publicMaskRetrofit;

    public static Retrofit publicJusoRetrofit;


    @Override
    public void onCreate() {
        super.onCreate();

        if (sSharedPreferences == null) {
            sSharedPreferences = getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


    public static Retrofit getPublicMaskRetrofit() {
        if (publicMaskRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .build();

            publicMaskRetrofit = new Retrofit.Builder()
                    .baseUrl(PUBLIC_MASK_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return publicMaskRetrofit;
    }


    public static Retrofit getPublicJusoRetrofit() {
        if (publicJusoRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .build();

            publicJusoRetrofit = new Retrofit.Builder()
                    .baseUrl(PUBLIC_JUSO_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return publicJusoRetrofit;
    }*/
}
