package com.graduation.even.graduationclient.net.connector;

import com.google.gson.Gson;
import com.graduation.even.graduationclient.constant.API;
import com.graduation.even.graduationclient.net.bean.request.ChangePwdRequest;
import com.graduation.even.graduationclient.net.bean.request.LoginRequest;
import com.graduation.even.graduationclient.net.bean.request.LogoutRequest;
import com.graduation.even.graduationclient.net.bean.request.RegisterRequest;
import com.graduation.even.graduationclient.net.bean.request.SetEmailRequest;
import com.graduation.even.graduationclient.net.bean.response.ChangePwdResponse;
import com.graduation.even.graduationclient.net.bean.response.LoginResponse;
import com.graduation.even.graduationclient.net.bean.response.LogoutResponse;
import com.graduation.even.graduationclient.net.bean.response.RegisterResponse;
import com.graduation.even.graduationclient.net.bean.response.SetEmailResponse;
import com.graduation.even.graduationclient.net.callback.NetCallBack;
import com.graduation.even.graduationclient.user.UserInfo;
import com.graduation.even.graduationclient.util.MD5Util;
import com.graduation.even.graduationclient.util.PLog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Even on 2017/11/8.
 */

public class NetworkConnector {
    private static NetworkConnector mNetworkConnector;

    private OkHttpClient mClient;
    private MediaType JSON, FILE, JPG;
    private Gson mGson;
    private UserInfo mUserInfo;

    public static NetworkConnector getInstance() {
        if (null == mNetworkConnector) {
            mNetworkConnector = new NetworkConnector();
        }
        return mNetworkConnector;
    }

    private NetworkConnector() {
        PLog.i("network connector is created");
        mClient = new OkHttpClient();
        mGson = new Gson();
        JSON = MediaType.parse("application/json; charset=utf-8");
        FILE = MediaType.parse("application/octet-stream");
        JPG = MediaType.parse("image/jpg");
        mUserInfo = UserInfo.getInstance();
    }

    // 登录
    public void login(String phone, String pwd, final NetCallBack callBack) {
        PLog.i("login, url is " + API.URL_LOGIN);

        LoginRequest loginRequest = new LoginRequest(phone, pwd);
        RequestBody body = RequestBody.create(JSON, mGson.toJson(loginRequest));
        Request request = new Request.Builder()
                .url(API.URL_LOGIN)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        PLog.i("do enqueue");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PLog.i("failed to login:" + e);
                callBack.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PLog.i("login, response is " + string);
                LoginResponse loginResponse = mGson.fromJson(string, LoginResponse.class);
                if (loginResponse.isSuccess()) {
                    PLog.i("success to login");
                    mUserInfo.setInfo(loginResponse.data.userId, loginResponse.data.token,
                            loginResponse.data.phone, loginResponse.data.email,
                            loginResponse.data.invalidTime);
                    callBack.onSuccess(null);
                } else {
                    PLog.i("failed to login:" + loginResponse.error);
                    callBack.onFailed(loginResponse.error);
                }
            }
        });
    }

    // 注册
    public void register(String phone, String pwd, final NetCallBack callBack) {
        PLog.i("register, url is " + API.URL_REGISTER);

        RegisterRequest registerRequest = new RegisterRequest(phone, pwd);
        RequestBody body = RequestBody.create(JSON, mGson.toJson(registerRequest));
        Request request = new Request.Builder()
                .url(API.URL_REGISTER)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        PLog.i("do enqueue");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PLog.i("failed to register:" + e);
                callBack.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PLog.i("register, response is " + string);
                RegisterResponse registerResponse = mGson.fromJson(string, RegisterResponse.class);
                if (registerResponse.isSuccess()) {
                    PLog.i("success to register");
                    mUserInfo.setInfo(registerResponse.data.userId, registerResponse.data.token,
                            registerResponse.data.phone, registerResponse.data.invalidTime);
                    callBack.onSuccess(null);
                } else {
                    PLog.i("failed to register:" + registerResponse.error);
                    callBack.onFailed(registerResponse.error);
                }
            }
        });
    }

    //登出
    public void logout(final NetCallBack callBack) {
        PLog.i("logout, url is " + API.URL_LOGOUT);

        String token = UserInfo.getInstance().getToken();
        LogoutRequest logoutRequest = new LogoutRequest(token);
        RequestBody body = RequestBody.create(JSON, mGson.toJson(logoutRequest));
        Request request = new Request.Builder()
                .url(API.URL_LOGOUT)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        PLog.i("do enqueue");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PLog.i("failed to logout:" + e);
                callBack.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PLog.i("logout, response is " + string);
                LogoutResponse logoutResponse = mGson.fromJson(string, LogoutResponse.class);
                if (logoutResponse.isSuccess()) {
                    PLog.i("success to logout");
                    callBack.onSuccess(null);
                } else {
                    PLog.i("failed to logout:" + logoutResponse.error);
                    callBack.onFailed(logoutResponse.error);
                }
            }
        });
    }

    //设置邮箱
    public void setEmail(String email, final NetCallBack callBack) {
        PLog.i("set email, url is " + API.URL_SET_EMAIL);

        String token = UserInfo.getInstance().getToken();
        int userId = UserInfo.getInstance().getUserId();
        SetEmailRequest setEmailRequest = new SetEmailRequest(token, userId, email);
        RequestBody body = RequestBody.create(JSON, mGson.toJson(setEmailRequest));
        Request request = new Request.Builder()
                .url(API.URL_SET_EMAIL)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        PLog.i("do enqueue");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PLog.i("failed to set email:" + e);
                callBack.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PLog.i("set email, response is " + string);
                SetEmailResponse setEmailResponse = mGson.fromJson(string, SetEmailResponse.class);
                if (setEmailResponse.isSuccess()) {
                    PLog.i("success to set email");
                    callBack.onSuccess(null);
                } else {
                    PLog.i("failed to set email:" + setEmailResponse.error);
                    callBack.onFailed(setEmailResponse.error);
                }
            }
        });
    }

    //修改密码
    public void changePwd(String oldPwd, String newPwd, final NetCallBack callBack) {
        PLog.i("change pwd, url is " + API.URL_CHANGE_PWD);

        String token = UserInfo.getInstance().getToken();
        int userId = UserInfo.getInstance().getUserId();
        ChangePwdRequest changePwdRequest = new ChangePwdRequest(token, userId, oldPwd, newPwd);

        RequestBody body = RequestBody.create(JSON, mGson.toJson(changePwdRequest));
        Request request = new Request.Builder()
                .url(API.URL_CHANGE_PWD)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        PLog.i("do enqueue");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PLog.i("failed to change pwd:" + e);
                callBack.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PLog.i("change pwd, response is " + string);
                ChangePwdResponse changePwdResponse = mGson.fromJson(string, ChangePwdResponse.class);
                if (changePwdResponse.isSuccess()) {
                    PLog.i("success to change pwd");
                    callBack.onSuccess(null);
                } else {
                    PLog.i("failed to change pwd:" + changePwdResponse.error);
                    callBack.onFailed(changePwdResponse.error);
                }
            }
        });

    }
}
