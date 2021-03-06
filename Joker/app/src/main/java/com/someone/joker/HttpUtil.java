package com.someone.joker;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Anonymous on 2016/3/29.
 */
public class HttpUtil {

    private static final String TAG = "HttpUtil";

    private String address;
    private String key;
    private String time;
    private Activity activity;

    public HttpUtil(Activity activity,String address,String key,String time){
        this.activity = activity;
        this.address = address;
        this.key = key;
        this.time = time;
    }


    public void sendHttpRequest(final HttpCallbackListener listener) {
        /**
         * 检测网络状态，如果有网络，进行http请求
         */
        if (isNetworkAvailable(activity)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 拼接发送的地址
                    address = address + "?key=" + key + "&page=2&pagesize=20&sort=asc"
                                + "&time=" + time;
                    // 打印发送的地址
                    Log.d(TAG, "Send Address: " + address);

                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(address);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(8000);
                        conn.setReadTimeout(8000);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        if (listener != null){
                            listener.onFinish(buffer.toString());
                        }
                        Log.d(TAG, "sendHttpRequest: buffer:" + buffer.toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (listener != null){
                            listener.onError(e);
                        }
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
            }).start();
        } else {
            /**
             * 没有网络连接，提示用户没有网络连接
             */
            Toast.makeText(activity,"无可用网络连接!",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 判断是否有网络连接
     * @param activity 当前活动
     * @return 是否有网络连接
     */
    private boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null){
            return false;
        } else {
            NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
            if (networkInfos != null && networkInfos.length > 0){
                for (int i = 0; i < networkInfos.length; i++) {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
