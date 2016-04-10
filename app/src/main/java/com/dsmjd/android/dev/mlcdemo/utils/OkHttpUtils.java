package com.dsmjd.android.dev.mlcdemo.utils;

import android.content.Intent;
import android.util.Log;

import com.dsmjd.android.dev.mlcdemo.MainActivity;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Set;

/**
 * Created by mylitboy on 2016/4/9.
 */
public class OkHttpUtils {

    public static void main(String[] arg) {
        OkHttpUtils.getBackJsonObj("http://192.168.3.43:8080/web/login.action", new OkCallBack<User>() {
            @Override
            public void onSuccess(User s) {
                System.out.println(s);
            }
        });
//        OkHttpUtils.getBackString("http://121.43.225.110:8080/base/user/login", new OkCallBack<String>() {
//            @Override
//            public void onSuccess(String s) {
//                System.out.println(s);
//            }
//        });


//        OkHttpUtils.getBackString("http://121.43.225.110:8080/greentown/greentown/syncGreenTownUser.action", new OkCallBack<String>() {
//            @Override
//            public void onSuccess(String s) {
//                System.out.println(s);
//                Log.e("xxxxxxxxxx", "dddddddddddddddddddddd");
//                Log.e("xxxxxxxxx333333333x", s);
//            }
//        });
    }

    public static abstract class OkCallBack<T> {
        Type mType;
        public OkCallBack()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }


        public abstract void onSuccess(T t);

        public void onFailed(Request request, IOException e) {

        }
    }

    public static void getBackString(String url, OkCallBack okCallBack) {
        Request request = buildGetRequest(url);
        callBackString(request, okCallBack);
    }

    public static void getBackJsonObj(String url,OkCallBack okCallBack){
        Request request = buildGetRequest(url);
        callBackJsonObj(request,okCallBack);
    }

    private static void callBackJsonObj(Request request, final OkCallBack okCallBack) {
        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("OnFailure");
                okCallBack.onFailed(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                Object person = gson.fromJson(response.body().charStream(), okCallBack.mType);
                okCallBack.onSuccess(person);
            }
        });
    }
    public static void postBackString(String url, Map<String, String> params, OkCallBack okCallBack) {
        Request request = buildPostRequest(url, map2Body(params));
        callBackString(request, okCallBack);
    }

    private static RequestBody map2Body(Map<String, String> params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private static void callBackString(Request request, final OkCallBack okCallBack) {
        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                Log.e("MlcDemo", "error");
                System.err.println("OnFailure");
                okCallBack.onFailed(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                Result person = gson.fromJson(response.body().charStream(), Result.class);

                char[] buffer = new char[1024];
                response.body().charStream().read(buffer);
                System.out.println(buffer);
                okCallBack.onSuccess(response.body().string());


//                Log.e("MlcDemoxxxxxxxxxxxxxx", response.message());
            }
        });
    }
    class Result{
        String status;
        String msg;
    }

    class User extends Result{

    }


    private static OkHttpClient okHttpClient = new OkHttpClient();

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    private static Request buildPostRequest(String url, RequestBody requestBody) {
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private static Request buildGetRequest(String url) {
        return new Request.Builder().url(url).build();
    }

}
