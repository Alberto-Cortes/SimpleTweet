package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
// Client and requests class
public class TwitterClient extends OAuthBaseClient {
	// Instance of requests with required keys set on the api keys file
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	// Method used to fetch tweets from the API using a GET request.
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}
	public void publishTweet(String content, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", content);
		client.post(apiUrl, params, "", handler);
	}
	public void likeInteraction(boolean isLiked, long id, JsonHttpResponseHandler handler){
		String apiUrl;
		if (isLiked){
			apiUrl = getApiUrl("/favorites/destroy.json");
		} else {
			apiUrl = getApiUrl("/favorites/create.json");
		}
		RequestParams params = new RequestParams();
		params.put("id", String.valueOf(id));
		client.post(apiUrl, params, "", handler);
	}
	public void retweetInteraction(boolean isRetweeted, long id, JsonHttpResponseHandler handler){
		String apiID = String.valueOf(id);
		String apiUrl;
		if (isRetweeted){
			apiUrl = getApiUrl("/statuses/unretweet/" + apiID	+ ".json");
		} else {
			apiUrl = getApiUrl("/statuses/retweet/" + apiID + ".json");
		}
		client.post(apiUrl, handler);
	}
}
