package com.example.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarm_Reciver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // intent로부터 전달받은 string
        String get_yout_string = intent.getExtras().getString("state");
        String get_yout_check = intent.getExtras().getString("check");
        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        // RingtonePlayinService로 extra string값 보내기
        service_intent.putExtra("state", get_yout_string);
        service_intent.putExtra("check", get_yout_check);
        // start the ringtone service

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}