package com.torv.easygesture.common;

import android.content.Context;

import com.torv.easygesture.R;

public class Util {

	private static final String TAG = "EGSUtil";

	public static String getSettingNameById(int id){
		SecLog.e(TAG, "getSettingNameById:id="+id);
		switch(id){
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_SLIDE_LEFT:
			 return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_LEFT;
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_SLIDE_RIGHT:
			return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_RIGHT;
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_SLIDE_UP:
			return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_UP;
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_SLIDE_DOWN:
			return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_DOWN;
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_SINGLE_TAP:
			return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SINGLE_TAP;
		case EasyGestureSetting.ID_EGS_GESTURE_ITEM_DOUBLE_TAP:
			return EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_DOUBLE_TAP;
		}
		return null;
	}
	
	public static String getActionStringById(Context context, int id){
		SecLog.e(TAG, "getActionStringById:id="+id);
		switch(id){
		case EasyGestureSetting.ACTION_CLOSE:
			return context.getResources().getStringArray(R.array.actionArray)[0];
		case EasyGestureSetting.ACTION_OPEN_TORCH:
			return context.getResources().getStringArray(R.array.actionArray)[1];
		case EasyGestureSetting.ACTION_SHOW_RECENT:
			return context.getResources().getStringArray(R.array.actionArray)[2];
		case EasyGestureSetting.ACTION_BACK_HOME:
			return context.getResources().getStringArray(R.array.actionArray)[3];
		case EasyGestureSetting.ACTION_EXPAND_NOTI:
			return context.getResources().getStringArray(R.array.actionArray)[4];
		case EasyGestureSetting.ACTION_LOCK_SCREEN:
			return context.getResources().getStringArray(R.array.actionArray)[5];
		case EasyGestureSetting.ACTION_HIDE_VIEW:
			return context.getResources().getStringArray(R.array.actionArray)[6];
		case EasyGestureSetting.ACTION_ADJUST_ALPHA:
			return context.getResources().getStringArray(R.array.actionArray)[7];
		case EasyGestureSetting.ACTION_QUICK_SHOT:
			return context.getResources().getStringArray(R.array.actionArray)[8];
		}
		return null;
	}
}
