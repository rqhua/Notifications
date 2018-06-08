package com.rqhua.demo.notifications;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/6/4.
 */

public class NetStatusUtils {

    enum Status {
        WIFI, //wifi
        MOBILE, //mobile
        CONNECTED, //已连接
        UNCONNECTED, //未连接
        UNKNOWN //状态未知
    }

    public static Status getStatus(ConnectivityManager conn) {
        if (conn == null)
            return Status.UNKNOWN;
        Status status = isOnline(conn);
        if (status != Status.CONNECTED) {
            return status;
        }
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return Status.WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return Status.MOBILE;
        }
        return Status.UNKNOWN;
    }

    //网络是否连接
    public static Status isOnline(ConnectivityManager conn) {
        if (conn == null)
            return Status.UNKNOWN;
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo == null) {
            return Status.UNKNOWN;
        }
        if (networkInfo.isConnected()) {
            return Status.CONNECTED;
        }
        return Status.UNCONNECTED;
    }

    public static class Speed {
        private static Timer timer;
        private static TimerTask timerTask;
        private static long lasttotalRxBytes;
        private static long lasttotalTxBytes;

        public static void updateNetSpeed(final SpeedCallback callback) {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            if (timer == null) {
                timer = new Timer();
            }
            if (timerTask == null) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        long totalRxBytes = TrafficStats.getTotalRxBytes();
                        long totalTxBytes = TrafficStats.getTotalTxBytes();
                        long speedTx = 0;
                        long speedRx = 0;
                        if (lasttotalRxBytes != 0) {
                            speedRx = totalRxBytes - lasttotalRxBytes;
                        }
                        if (lasttotalTxBytes != 0) {
                            speedTx = totalTxBytes - lasttotalTxBytes;
                        }
                        lasttotalTxBytes = totalTxBytes;
                        lasttotalRxBytes = totalRxBytes;
                        if (callback != null)
                            callback.progress(Double.valueOf(speedRx), Double.valueOf(speedTx));
                    }
                };
            }
            timer.schedule(timerTask, 1000, 3000);
        }

        private static long k = 1024;
        private static long m = 1024 << 10;

        //网速描述语句
        public static String getDesOfSpeed(double speedB) {
            String des = "";
            speedB = speedB / 3;
            DecimalFormat format = new DecimalFormat("#0.00");
            if (0 <= speedB && speedB < k) {
                des = format.format(speedB) + "B/S";
            } else if (k <= speedB && speedB < m) {
                double speedK = speedB / 1024;
                des = format.format(speedK) + "K/S";
            } else if (speedB >= m) {
                double speedM = speedB / 1024 / 1024;
                des = format.format(speedM) + "M/S";
            }

            return des;
        }

        public static void cancelUpdate(){
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

        }

        public interface SpeedCallback {
            /**
             * @param rSpeed 接受网速
             * @param dSpeed 发送网速
             */
            void progress(double rSpeed, double dSpeed);
        }


    }

}