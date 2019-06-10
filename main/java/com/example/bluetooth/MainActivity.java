package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.sax.StartElementListener;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.kakaolink.internal.KakaoTalkLinkProtocol;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static com.kakao.kakaotalk.KakaoTalkService.requestSendMemo;


public class MainActivity extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;
    String firebasedata = "";
    HashMap<String, String> map = new HashMap<>();
    HashMap<String, String> keymap = new HashMap<>();
    ArrayList<String> amp = new ArrayList<>();
    String[] ListMenu;
    private String userPhone;
    LocalService mService;
    boolean mBound = false;

    class CustomList extends ArrayAdapter<String>{
        private final Activity context;
        public CustomList(Activity context){
            super(context,R.layout.listitem,ListMenu);
            this.context=context;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater=context.getLayoutInflater();
            @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.listitem,null,true);
            TextView title =(TextView) rowView.findViewById(R.id.title);
            TextView date = (TextView) rowView.findViewById(R.id.date);
            TextView number=(TextView) rowView.findViewById(R.id.number);
            String[] temp = ListMenu[position].split(" ");
            String[] temp2= temp[1].split(",");
            title.setText(temp[0]);
            date.setText(temp2[0]);
            number.setText(temp2[1]);
            return rowView;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Drawable alpha = ((Button)findViewById(R.id.button)).getBackground();
        alpha.setAlpha(85);
        final DatabaseReference myRef = database.getReference("kora");
        final DatabaseReference myRef_check = database.getReference("check");
        final ListView listView = (ListView) findViewById(R.id.list);
        final ArrayList<String> FirebaseKey = new ArrayList<>();
        FirebaseInstanceId.getInstance().getToken();
        final String TAG = "";
        Intent intent = new Intent(MainActivity.this,LocalService.class);
        bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
       //휴대폰 번호 받아오기
        try{
            userPhone = mgr.getLine1Number();
            userPhone = userPhone.replace("+82","0");
            Toast.makeText(getApplicationContext(),userPhone,Toast.LENGTH_LONG).show();
        } catch (Exception e){

        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable alpha = ((Button)findViewById(R.id.button)).getBackground();
                alpha.setAlpha(100);
                Intent intent2 = new Intent(getApplicationContext(), Plusbtn.class);
                startActivity(intent2);
            }
        });
        //데이터베이스 받아오기
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                firebasedata = value.toString();
                if (!(firebasedata.isEmpty())) {
                    firebasedata = firebasedata.replaceAll(" ", "");
                    firebasedata = firebasedata.replaceAll("\\{", "");
                    firebasedata = firebasedata.substring(0, firebasedata.lastIndexOf("}"));
                    firebasedata = firebasedata.replaceAll("\\}", "");
                    String[] temp = firebasedata.split("date=");
                    FirebaseKey.add(temp[0].replaceAll("=", "").trim());
                    for (int i = 1; i < temp.length; i++) {
                        String[] temp2 = temp[i].split(",name=");
                        if (temp2[1].contains(",")) {
                            FirebaseKey.add(temp2[1].substring(temp2[1].indexOf(","), temp2[1].lastIndexOf("=")).replaceAll(",", "").trim());
                            temp2[1] = temp2[1].substring(0, temp2[1].indexOf(","));
                        }
                        temp2[0] = temp2[0].replaceAll("number=", "");
                        map.put(temp2[1], temp2[0]);
                        keymap.put(FirebaseKey.get(i - 1), temp2[1]);
                    }

                    ListMenu = map.keySet().toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
                    for (int i = 0; i < ListMenu.length; i++) {
                        ListMenu[i] = ListMenu[i].concat(" " + map.get(ListMenu[i]));
                        amp.add(ListMenu[i].concat(" "));
                    }
                    CustomList adapter=new CustomList(MainActivity.this);
                    listView.setAdapter(adapter);
                    String fruit = "";
                    for (int i = 0; i < amp.size(); i++) {
                        fruit = fruit.concat(amp.get(i));
                    }
                    String[] date_temp=fruit.split(" ");
                    int i=1;
                    while(i<date_temp.length) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("정보확인");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                        Date date_list = new Date();
                        try {
                            date_list = dateFormat.parse(date_temp[i]);
                        } catch (ParseException e) {
                            long now = System.currentTimeMillis();
                            date_list = new Date(now);
                        }
                        long now_long = System.currentTimeMillis();
                        Date now_date = new Date(now_long);
                        long calDate = now_date.getTime() - date_list.getTime();
                        long calDateDays = calDate / (24 * 60 * 60 * 1000);
                        calDateDays = Math.abs(calDateDays);

                        if(calDateDays>=7){
                            myRef_check.setValue("true");
                            break;
                        } else {
                            myRef_check.setValue("false");
                        }
                        i=i+2;
                    }
                    if (mBound) {
                        mService.setFruit(fruit);
                    }
                }
                unbindService(mConnection);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "화면 초기화 실패", Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object Data = (Object) parent.getAdapter().getItem(position);
                final String[] setKey = Data.toString().split(" ");
                String[] setData = setKey[1].split(",");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("정보확인");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                Date date_list = new Date();
                try {
                    date_list = dateFormat.parse(setData[0]);
                } catch (ParseException e) {
                    long now = System.currentTimeMillis();
                    date_list = new Date(now);
                }
                long now_long = System.currentTimeMillis();
                Date now_date = new Date(now_long);
                long calDate = now_date.getTime() - date_list.getTime();
                long calDateDays = calDate / (24 * 60 * 60 * 1000);
                calDateDays = Math.abs(calDateDays);

                builder.setMessage(calDateDays + "일이 지났습니다");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < FirebaseKey.size(); i++) {
                            if (keymap.get(FirebaseKey.get(i)).trim().equals(setKey[0])) {
                                myRef.child(FirebaseKey.get(i).trim()).removeValue();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < FirebaseKey.size(); i++) {
                            if (keymap.get(FirebaseKey.get(i)).trim().equals(setKey[0])) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        Button button_picture=(Button) findViewById(R.id.button_picture);
        button_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PictureBtn.class);
                startActivity(intent);
            }
        });
        Button button_kakao=(Button) findViewById(R.id.button_kakao);
        button_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fruit = "";
                for (int i = 0; i < amp.size(); i++) {
                    fruit = fruit.concat(amp.get(i)).concat(" ");
                }
                TextTemplate params = TextTemplate.newBuilder(fruit, LinkObject.newBuilder().build()).build();
                HashMap<String, String> serverCallbackArgs = new HashMap<String, String>();
                Intent intent1 = getIntent();
                ;
                serverCallbackArgs.put("user_id", "${1081268818}");
                serverCallbackArgs.put("product_id", "${1081268818}");

                KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {

                    }
                });
            }
        });
        Button alarm_button=(Button)findViewById(R.id.alarm_button);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Alarm.class);
                startActivity(intent);
            }
        });
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalService.LocalBinder binder =(LocalService.LocalBinder) service;
            mService=binder.getService();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound=false;
        }
    };
}
