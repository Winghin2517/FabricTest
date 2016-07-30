package test.com.fabrictest;

import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "XXX";
    private static final String TWITTER_SECRET = "XXX";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.startMethodTracing("main");
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());
        setContentView(R.layout.activity_main);
        TextView signin = (TextView) findViewById(R.id.signin);
        signin.setOnClickListener(digitsButtonClicked);

        if (Digits.getSessionManager().getActiveSession() != null) {
           //each time my app launches, it checks for an active session to see whether a user has logged in
        }
        Debug.stopMethodTracing();
    }

    View.OnClickListener digitsButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Digits.authenticate(new DigitsAuthConfig.Builder()
                    .withAuthCallBack(authCallback)
                    .withThemeResId(R.style.CustomDigitsTheme)
                    .build());
        }
    };

    //Twitter Digits integration
    final AuthCallback authCallback = new AuthCallback() {
        @Override
        public void success(DigitsSession session, String phoneNumber) {
            TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
            TwitterAuthToken authToken = (TwitterAuthToken) Digits.getInstance().getSessionManager().getActiveSession().getAuthToken();
            DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
            Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
            ArrayList<String> entries = new ArrayList<>(authHeaders.values());
        }

        @Override
        public void failure(DigitsException exception) {
            Log.e("main", "signin failed");
        }
    };
}
