package com.example.bluetooth;




import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

class SessionCallback implements ISessionCallback {
    // 로그인에 성공한 상태
    @Override
    public void onSessionOpened() {
        requestMe();
    }

    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    // 사용자 정보 요청
    public void requestMe() {
        // 사용자정보 요청 결과에 대한 Callback
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            // 세션 오픈 실패. 세션이 삭제된 경우,
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
            }

            // 회원이 아닌 경우,
            @Override
            public void onNotSignedUp() {
                Log.e("SessionCallback :: ", "onNotSignedUp");
            }

            // 사용자정보 요청에 성공한 경우,
            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("SessionCallback :: ", "onSuccess");
                final String nickname = userProfile.getNickname();
                final String email = userProfile.getEmail();
                final String profileImagePath = userProfile.getProfileImagePath();
                final String thumnailPath = userProfile.getThumbnailImagePath();
                final String UUID = userProfile.getUUID();
                long id = userProfile.getId();
                Log.e("Profile : ", nickname + "");
                Log.e("Profile : ", email + "");
                Log.e("Profile : ", profileImagePath + "");
                Log.e("Profile : ", thumnailPath + "");
                Log.e("Profile : ", UUID + "");
                Log.e("Profile : ", id + "");
            }

            // 사용자 정보 요청 실패
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
            }
        });
    }
}