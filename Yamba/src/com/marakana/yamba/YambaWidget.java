package com.marakana.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

/* Our widget is a subclass of AppWidgetProvider, which
	itself is a BroadcastReceiver.
*/
public class YambaWidget extends AppWidgetProvider {
	private static final String TAG = YambaWidget.class.getSimpleName();

	// Called whenever widget updated
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int appWidgetIds[]) {
		Log.d(TAG, "onUpdate");
		
		// Get the latest tweet
		Cursor cursor = context.getContentResolver().query(
				StatusContract.CONTENT_URI, null, null, null,
				StatusContract.DEFAULT_SORT);
		
		// We just want the latest update
		if (!cursor.moveToFirst())
			return;
		
		String user = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.USER));
		String message = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.MESSAGE));
		long createdAt = cursor.getLong(cursor
				.getColumnIndex(StatusContract.Column.CREATED_AT));
		
		PendingIntent operation = PendingIntent.getActivity(context, -1,
				new Intent(context, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Loop through all instances of Yamba Widget
		for (int appWidgetId : appWidgetIds) {
			
			//Update the view that is another process (inside Home application)
			// The RemoteViews  is a special shmem system designed specifically for widgets
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);
			
			// Update the remote view
			views.setTextViewText(R.id.list_item_text_user, user);
			views.setTextViewText(R.id.list_item_text_message, message);
			views.setTextViewText(R.id.list_item_text_created_at,
					DateUtils.getRelativeTimeSpanString(createdAt));
			views.setOnClickPendingIntent(R.id.list_item_text_user,
					operation);
			views.setOnClickPendingIntent(R.id.list_item_text_message,
					operation);
			
			// Update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		this.onUpdate(context, appWidgetManager, appWidgetManager
				.getAppWidgetIds(new ComponentName(context,
						YambaWidget.class)));
	}
}
