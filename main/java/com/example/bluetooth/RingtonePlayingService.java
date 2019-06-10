package com.example.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RingtonePlayingService extends Service {
    LocalService mService;
    int startId;
    boolean isRunning;
    private Context context;
    FirebaseAnalytics mFirebaseAnalytics;
    private boolean check=false;
    private String check_temp="";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String getState = intent.getExtras().getString("state");
        String getCheck = intent.getExtras().getString("check");
        assert getState != null;
        switch (getState) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        if(getCheck.equals("true")){
            check=true;
        } else{
            check=false;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {
            if (Build.VERSION.SDK_INT >= 26) {
                if (check) {
                    Intent intent1 = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent1,
                            PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
                    String Title = "주의하세요";
                    String Message = "주의하세요";
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentTitle(Title);
                    builder.setContentText(Message);
                    builder.setColor(Color.RED);
                    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    // 사용자가 탭을 클릭하면 자동 제거
                    builder.setAutoCancel(true);
                    builder.setOngoing(true);
                    builder.setContentIntent(pendingIntent);
                    // 알림 표시
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
                    }

                    // id값은
                    // 정의해야하는 각 알림의 고유한 int값
                    notificationManager.notify(1, builder.build());
                } else {
                    Intent intent1 = new Intent(this, MainActivity.class);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent1,
                            PendingIntent.FLAG_ONE_SHOT);


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
                    String Title = "양호";
                    String Message = "양호";
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentTitle(Title);
                    builder.setContentText(Message);
                    builder.setColor(Color.RED);
                    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    // 사용자가 탭을 클릭하면 자동 제거
                    builder.setAutoCancel(true);
                    builder.setOngoing(true);
                    builder.setContentIntent(pendingIntent);
                    // 알림 표시
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
                    }

                    // id값은
                    // 정의해야하는 각 알림의 고유한 int값
                    notificationManager.notify(1, builder.build());
                }
            }
            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {

            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){
            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onDestory() 실행", "서비스 파괴");
    }

    private void createNotification(String Title_ask,String Message_ask) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND,5000);
        Intent intent=new Intent(this,MainActivity.class);
        Long lon =cal.getTimeInMillis();
        String str=lon.toString();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        String Title=Title_ask;
        String Message=Message_ask;
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(Title);
        builder.setContentText(Message);
        builder.setColor(Color.RED);
        builder.setSmallIcon(R.drawable.kakaotalk_icon);
        builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
}