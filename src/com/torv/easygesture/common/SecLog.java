package com.torv.easygesture.common;

import android.util.Log;

public class SecLog {

	public static void e(String TAG, String msg){

		if(Feature.FEATURE_ENABLE_LOG)
			Log.e(TAG, msg);
	}
}