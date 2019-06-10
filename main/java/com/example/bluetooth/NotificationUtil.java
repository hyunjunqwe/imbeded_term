package com.example.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationUtil extends BroadcastReceiver {
    private String channelId = "channel";
    private String channelName = "channelName";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //오레오 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);  // 알림 클릭 시 이동할 액티비티 지정

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // FLAG_UPDATE_CURRENT : 이미 생성된 PendingIntent가 존재하면 해당 Intent의 Extra Data만 변경한다.
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("알람") //제목
                .setContentText("알람 테스트입니다") //내용
                .setDefaults(Notification.DEFAULT_ALL) //알림 설정(사운드, 진동)
                .setAutoCancel(true) //터치 시 자동으로 삭제할 지 여부
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림의 중요도
                .setSmallIcon(R.drawable.kakaotalk_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.kakaotalk_icon))
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
}
