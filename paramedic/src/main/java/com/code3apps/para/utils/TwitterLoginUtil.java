package com.code3apps.para.utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.code3apps.para.R;
import com.code3apps.para.WebViewActivity;

public class TwitterLoginUtil {
	private Twitter mTwitter;

    private RequestToken mRequestToken;

    private static TwitterLoginUtil instance;

    public static TwitterLoginUtil getInstance() {
        if (instance == null) {
            instance = new TwitterLoginUtil();
        }

        return instance;
    }

    public void connectToTwitter(Context context) {

        new TwitterConnectAsync().execute(context);

    }

    public RequestToken getRequestToken() {
        return mRequestToken;
    }

    public Twitter getTwitterObj() {
        return mTwitter;
    }

    private class TwitterConnectAsync extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder
                    .setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
            configurationBuilder
                    .setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
            Configuration configuration = configurationBuilder.build();

            Context context = params[0];
            
            String callback = "";
            
            callback = context.getString(R.string.twitter_login_app_login);
 
            mTwitter = new TwitterFactory(configuration).getInstance();
            try {
                mRequestToken = mTwitter.getOAuthRequestToken("oauth://"
                        + callback);

                Bundle extras = new Bundle();
                extras.putString(Const.URL,
                        mRequestToken.getAuthenticationURL());
                extras.putString(Const.CALLBACK,
                        Const.TWITTER_CALLBACK_URL);
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtras(extras);

                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(mRequestToken.getAuthenticationURL())));

            } catch (TwitterException e) {
                //Log.e(TwitterLogin.class, e, e.getMessage());
            }

            return null;
        }

    }
}
