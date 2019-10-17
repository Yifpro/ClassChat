package com.example.wyf.classchat.feature.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author WYF on 2017/9/23.
 */
public class LoginHelper {

    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            })
            .build();

    public static void sendOkHttpRequest(String address, Callback callback) {
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 模拟教务登陆
     * @param activity 上下文
     * @param views 参数
     * @return
     */
    public static String connectJw(final Activity activity, EditText[] views) {
        String name = views[0].getText().toString().trim();
        String psd = views[1].getText().toString().trim();
        String code = views[2].getText().toString().trim();
        String identity = psd.length() < 18 ? "教师" : "学生";

        RequestBody requestBody = new FormBody.Builder()
                .add("__VIEWSTATE", "dDw3OTkxMjIwNTU7Oz72G0jnx2CVi9cEqCETKg2lgGSYBw==")
                .add("TextBox1", name)
                .add("TextBox2", psd)
                .add("TextBox3", code)
                .add("RadioButtonList1", identity)
                .add("Button1", "")
                .add("lbLanguage", "")
                .add("hidPdrs", "")
                .add("hidsc", "")
                .build();
        final Request request = new Request.Builder()
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "http://jw2012.gdcp.cn/")
                .url("http://jw2012.gdcp.cn/default2.aspx")
                .post(requestBody)
                .build();
        String error = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                Document doc = Jsoup.parse(body);
                Elements scriptElements = doc.getElementsByTag("script");
                String text = scriptElements.get(1).toString();
                if ("正方教务管理系统".equals(Jsoup.parse(body).select("title").get(0).text())) {
                    //EventBus.getDefault().post(new MessageEvent(Constants.CONN_JW_SUCCESS, body));
                    return error;
                }
                if (text.contains("alert('验证码不正确！！')")) {
                    error = "验证码错误，请重新输入";
                } else if (text.contains("alert('用户名不存在或未按照要求参加教学活动！！')")) {
                    error = "用户名错误，请重新输入";
                } else if (text.contains("alert('密码错误！！')")) {
                    error = "密码错误，请重新输入";
                } else {
                    error = "发生未知异常，请稍后重试";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }

    /**
     * 获取验证码
     * @return 验证码
     */
    public static Bitmap loadVertificationCode() {
        try {
            Request request = new Request.Builder().url("http://jw2012.gdcp.cn/CheckCode.aspx").build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                InputStream inputStream = response.body().byteStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert mConnectivityManager != null;
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }
}
