package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;

import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;
public class KakaoTalkMainActivity extends Activity {
    private void  redirectLoginActivity() {
        final Intent intent = new Intent(this, KakaoLoginActivity.class);
        startActivity(intent);
        finish();
    }
    public static abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }
        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }
    }
}