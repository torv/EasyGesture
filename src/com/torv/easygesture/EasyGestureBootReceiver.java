package com.torv.easygesture;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.SecLog;
import com.torv.easygesture.service.EasyGestureService;

public class EasyGestureBootReceiver extends BroadcastReceiver {

	private static final String TAG = "EasyGestureBootReceiver";
	

	@Override
	public void onReceive(Context context, Intent arg1) {
		SecLog.e(TAG, "EasyGestureBootReceiver.onReceive");

		SharedPreferences mSharedPreferences = context.getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
		
		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED) == false){
			Intent intent = new Intent(context, EasyGestureService.class);
			context.stopService(intent);
			context.startService(intent);
		}
	}
}
