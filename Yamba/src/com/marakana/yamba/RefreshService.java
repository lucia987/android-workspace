package com.marakana.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {
	public static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	// Executes on a worker thread
	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final String username = prefs.getString("username", "");
		final String password = prefs.getString("password", "");
		
		if (TextUtils.isEmpty(username) ||
				TextUtils.isEmpty(password)) {
			Toast.makeText(this,
					"Please update your username and password",
					Toast.LENGTH_LONG).show();
			return;
		}
		Log.d(TAG, "onStarted");
		
		ContentValues values = new ContentValues();
		
		YambaClient cloud =
				new YambaClient(username, password);
		try {
			// Chapter 13: Used to broadcast
			int count = 0;
			
			List<Status> timeline = cloud.getTimeline(20);
			for (Status status: timeline) {
				values.clear();
				values.put(StatusContract.Column.ID,
						status.getId());
				values.put(StatusContract.Column.USER,
						status.getUser());
				values.put(StatusContract.Column.MESSAGE,
						status.getMessage());
				values.put(StatusContract.Column.CREATED_AT,
						status.getCreatedAt().getTime());
			
				Uri uri = getContentResolver().insert(
						StatusContract.CONTENT_URI, values);
				if (uri != null) {
					
					// Chapter 13: Increment if there are new statuses
					count++;
					Log.d(TAG,
							String.format("%s: %s",
									status.getUser(),
									status.getMessage()));
				}
			}
			
			// Added in Chapter 13: Broadcasting Intents
			if (count > 0) {
				sendBroadcast(new Intent(
						"com.marakana.android.yamba.action.NEW_STATUSES")
				.putExtra("count", count));
			}
		} catch (YambaClientException e) {
			Log.e(TAG, "Failed to fetch the timeline");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}
}
