package com.example.diyujia.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by diyujia on 2017/12/15.
 */

public class NetUtil {

    /**
     * NETWORN_NONE 代表网络连接失败.
     */
    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    /**
     *getNetworkState方法用于检测网络是否连接正常
     */
    public static int getNetworkState(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo == null){
            return NETWORN_NONE;
        }

        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE){
            return NETWORN_MOBILE;
        } else if(nType == ConnectivityManager.TYPE_WIFI){
            return NETWORN_WIFI;
        }
        return NETWORN_NONE;
    }
}
