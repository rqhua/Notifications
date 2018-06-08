package com.rqhua.demo.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    NetworkReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.basic).setOnClickListener(this);
        findViewById(R.id.custome).setOnClickListener(this);
        findViewById(R.id.activite).setOnClickListener(this);
        receiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basic:
                sendBasicNotification();
                break;
            case R.id.custome:
                customeNotification();
                break;
            case R.id.activite:
                startActivity(new Intent(this, LNotificationActivity.class));
                break;
        }
    }

    private static final int NOTIFICATION_ID_basic = 1;

    public void sendBasicNotification() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //一、通过NotificationCompat.Builder 构建通知内容
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         * 使用简单的图标
         */
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        // 点击通知时将触发的Intent
        builder.setContentIntent(pendingIntent);
        // 点击之后消失
        builder.setAutoCancel(true);
        //通知条显示的图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        //内容标题，在通知的顶部以大字体显示
        builder.setContentTitle("BasicNotifications Sample");
        //内容文本，出现在标题下方的小文本中
        builder.setContentText("Time to learn about notifications!");
        //文本下面显示的提示，在4.2之前运行Android版本的设备将忽略这个字段，所以不要用于显示重要的信息
        builder.setSubText("Tap to view documentation about notifications.");

        //二、通过NotificationManager发送通知，将立即在通知栏中显示通知图标。
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_basic, builder.build());
    }

    private void customeNotification() {
        //一、通过NotificationCompat.Builder 构建通知内容
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //创建Intent，点击之后重新打开MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setTicker("自定义布局notification");
        builder.setSmallIcon(R.drawable.ic_stat_custom);
        // 点击之后消失
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        // 自定义布局
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        final String time = DateFormat.getTimeInstance().format(new Date()).toString();
        final String text = "收缩的通知：显示一个机器人 \n " + time;
        contentView.setTextViewText(R.id.textView, text);
        notification.contentView = contentView;

        //api 16及以上，通知支持扩展显示，通过设置bigContentView实现
        if (Build.VERSION.SDK_INT >= 16) {
            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
            notification.bigContentView = expandedView;
        }
        //二、通过NotificationManager发送通知，将立即在通知栏中显示通知图标。
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

}
