package com.rqhua.demo.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/6/4.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private ConnectivityManager conn;
    public static boolean refreshDisplay = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetStatusUtils.Status status = NetStatusUtils.getStatus(conn);

        if (status == NetStatusUtils.Status.WIFI) {
            Toast.makeText(context, "WIFI连接", Toast.LENGTH_SHORT).show();
        } else if (status == NetStatusUtils.Status.MOBILE) {
            Toast.makeText(context, "MOBILE连接", Toast.LENGTH_SHORT).show();
        } else if (status == NetStatusUtils.Status.CONNECTED) {
            Toast.makeText(context, "已连接", Toast.LENGTH_SHORT).show();
        } else if (status == NetStatusUtils.Status.UNCONNECTED) {
            Toast.makeText(context, "断开连接", Toast.LENGTH_SHORT).show();
        } else if (status == NetStatusUtils.Status.UNKNOWN) {
            Toast.makeText(context, "未知连接状态", Toast.LENGTH_SHORT).show();
        }

        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (!isOnline()) {
            refreshDisplay = false;
            Toast.makeText(context, "无网络连接", Toast.LENGTH_SHORT).show();
            return;
        }


        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            refreshDisplay = true;
            Toast.makeText(context, "wifi已连接", Toast.LENGTH_SHORT).show();
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            refreshDisplay = true;
            if (Build.VERSION.SDK_INT > 21)
                createNotification(context);
            Toast.makeText(context, "手机网络已连接", Toast.LENGTH_SHORT).show();
        }

    }


    @RequiresApi(21)
    private void createNotification(Context context) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setSmallIcon(R.drawable.ic_launcher_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_notification));
        builder.setAutoCancel(true);
        builder.setContentTitle("悬挂式通知");
        //设置点击跳转
        Intent hangIntent = new Intent();
        hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        hangIntent.setClass(context, LNotificationActivity.class);
        //如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
        PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setFullScreenIntent(hangPendingIntent, true);
        mNotificationManager.notify(2, builder.build());


        /*Notification.Builder notificationBuilder = new Notification.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_launcher_notification)
                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("Sample Notification")
                .setContentText("This is a normal notification.");
        if (makeHeadsUpNotification) {
            Intent push = new Intent();
//            push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            push.setClass(getActivity(), LNotificationActivity.class);

            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getActivity(), 0,
                    push, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder
                    .setContentText("Heads-Up Notification on Android L or above.")
                    .setFullScreenIntent(fullScreenPendingIntent, true);
        }
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());*/
    }

    //网络是否连接
    public boolean isOnline() {
        if (conn == null)
            return false;
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
