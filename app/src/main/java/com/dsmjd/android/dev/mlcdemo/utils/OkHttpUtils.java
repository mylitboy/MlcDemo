package com.dsmjd.android.dev.mlcdemo.utils;

import android.content.Intent;
import android.util.Log;

import com.dsmjd.android.dev.mlcdemo.MainActivity;
import com.dsmjd.android.dev.mlcdemo.models.BaseResult;
import com.dsmjd.android.dev.mlcdemo.models.OkResult;
import com.dsmjd.android.dev.mlcdemo.models.UserResult;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mylitboy on 2016/4/9.
 */
public class OkHttpUtils {

    public static void main(String[] arg) {
//        String url = "http://121.43.225.110:8080/base/user/login";
        String url = "http://192.168.3.43:8080/web/login.action";
//        String url = "http://121.43.225.110:8080/greentown/greentown/syncGreenTownUser.action";
        Map<String,String> params = new HashMap<String,String>();
        params.put("user.itme1","18000000000");
        params.put("user.password","180000");
        OkHttpUtils.doPost(url,params, new OkCallBack<BaseResult<UserResult>>() {
            @Override
            public void onSuccess(BaseResult<UserResult> s) {
                System.out.println(s);
            }
        });
    }

    public static void doGet(String url, OkCallBack okCallBack) {
        Request request = buildGetRequest(url);
        callRequest(request, okCallBack);
    }

    public static void doPost(String url, Map<String, String> params, OkCallBack okCallBack) {
        Request request = buildPostRequest(url, map2Body(params));
        callRequest(request, okCallBack);
    }


    public static abstract class OkCallBack<T> {
        Type mType;

        public OkCallBack() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }


        public abstract void onSuccess(T t);

        public void onFailed(Request request, IOException e) {

        }
    }

    private static void callRequest(Request request, final OkCallBack okCallBack) {
        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("OnFailure");
                okCallBack.onFailed(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (okCallBack.mType == String.class) {
                    okCallBack.onSuccess(response.body().string());
                } else {
                    try {
                        Gson gson = new Gson();
                        Object person = gson.fromJson(response.body().charStream(), okCallBack.mType);
                        okCallBack.onSuccess(person);
                    } catch (Exception e) {
                        e.printStackTrace();
                        okCallBack.onSuccess(response.body().string());
                    }
                }
            }
        });
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
