package com.example.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;


public class KakaoLoginActivity extends AppCompatActivity {
    private Context mContext;
    private Button btn_custom_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_TALK, KakaoLoginActivity.this);
            }
        });
    }
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.getInstance().requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);
                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //재 로그인 구현 필요
                        //redirectLoginActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }
                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인 성공 시 로그인한 사용자의 일련번호, 닉네임, 이미지url 리턴
                    //사용자 캐시 정보 업데이트 - 별 필요 없는듯
                    if (userProfile != null) {
                        userProfile.saveUserToCache();
                    }
                    Logger.e("succeeded to update user profile", userProfile, "\n");
                    //////////////////
                    final String nickName = userProfile.getNickname();//닉네임
                    final long userID = userProfile.getId();//사용자 고유번호
                    final String pImage = userProfile.getProfileImagePath();//사용자 프로필 경로
                    Log.e("UserProfile", userProfile.toString());//전체 정보 출력
                    Intent intent = new Intent(KakaoLoginActivity.this, MainActivity.class);
                    intent.putExtra("user_ID",userID);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }
}
