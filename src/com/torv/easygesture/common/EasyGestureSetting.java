package com.torv.easygesture.common;

public class EasyGestureSetting {

	// app id
	public static final String WECHAT_APP_ID = "wxc85b9dbef61b3fe4";

	// action
	public static final int ACTION_CLOSE = 0;
	public static final int ACTION_OPEN_TORCH = 1;
	public static final int ACTION_SHOW_RECENT = 2;
	public static final int ACTION_BACK_HOME = 3;
	public static final int ACTION_EXPAND_NOTI = 4;
	public static final int ACTION_LOCK_SCREEN = 5;
	public static final int ACTION_HIDE_VIEW = 6;
	public static final int ACTION_ADJUST_ALPHA = 7;
	public static final int ACTION_QUICK_SHOT = 8;

	// preferences name
	public static final String PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE = "preferences_private_easy_gesture_service";
	
	// preferences key
	public static final String KEY_PREFERENCES_EGS_FUNCTION_DISABLED = "key_preferences_egs_function_disabled";
	
	public static final String KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED = "key_preferences_egs_icon_move_disabled";
	
	public static final String KEY_PREFERENCES_EGS_ROTATE_SHOT_DISABLED = "key_preferences_egs_rotate_shot_disabled";

	public static final String KEY_PREFERENCES_EGS_IS_LANDSCAPE = "key_preferences_egs_is_landscape";
	public static final String KEY_PREFERENCES_EGS_WINDOW_X = "key_preferences_egs_window_x";
	public static final String KEY_PREFERENCES_EGS_WINDOW_Y = "key_preferences_egs_window_y";

	public static final String KEY_PREFERENCES_EGS_IS_FIRST_TIME = "key_preferences_egs_is_first_time";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_LEFT = "key_preferences_egs_gesture_item_slide_left";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_RIGHT = "key_preferences_egs_gesture_item_slide_right";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_UP = "key_preferences_egs_gesture_item_slide_up";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_DOWN = "key_preferences_egs_gesture_item_slide_down";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_SINGLE_TAP = "key_preferences_egs_gesture_item_single_tap";
	public static final String KEY_PREFERENCES_EGS_GESTURE_ITEM_DOUBLE_TAP = "key_preferences_egs_gesture_item_double_tap";
	
	public static final String KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY = "key_preferences_egs_wakeup_screen_way";
	
	public static final String KEY_PREFERENCES_EGS_CURRENT_ICON = "key_preferences_egs_current_icon";
	public static final String KEY_PREFERENCES_EGS_CURRENT_ALPHA = "key_preferences_egs_current_alpha";
	
	public static final String KEY_PREFERENCES_EGS_CURRENT_VERSION_NAME = "key_preferences_egs_current_version_name";
	
	//default value
	public static final boolean DEFAULT_VALUE_BOOL_FUNCTION_DISABLED = false;
	public static final boolean DEFAULT_VALUE_BOOL_ICON_MOVE_DISABLED = false;
	public static final boolean DEFAULT_VALUE_BOOL_ROTATE_SHOT_DISABLED = true;
	
	public static final boolean DEFAULT_VALUE_BOOL_IS_LANDSCAPE = false;
	public static final int DEFAULT_VALUE_INT_WINDOW_X = 0;
	public static final int DEFAULT_VALUE_INT_WINDOW_Y = 0;
	
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_LEFT = ACTION_OPEN_TORCH;
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_RIGHT = ACTION_SHOW_RECENT;
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_UP = ACTION_BACK_HOME;
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_DOWN = ACTION_EXPAND_NOTI;
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_SINGLE_TAP = ACTION_LOCK_SCREEN;
	public static final int DEFAULT_VALUE_INT_GESTURE_ITEM_DOUBLE_TAP = ACTION_HIDE_VIEW;
	
	public static final int DEFAULT_VALUE_INT_SINGLE_TAP_DELAY_TIME = 200;
	
	public static final int DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY = 1;
	
	public static final int DEFAULT_VALUE_INT_CURRENT_ICON = 0;
	public static final int DEFAULT_VALUE_INT_CURRENT_ALPHA = 100;
	
	// handler msg
	public static final int HANDLER_MSG_EGS_SINGLE_TOUCH = 1;
	public static final int HANDLER_MSG_EGS_NOT_SUPPORT_FLASH = 2;
	public static final int HANDLER_MSG_EGS_FLASH_NOT_AVAILABLE = 3;
	
	// setting item id
	public static final int ID_EGS_GESTURE_ITEM_SLIDE_LEFT = 0;
	public static final int ID_EGS_GESTURE_ITEM_SLIDE_RIGHT = 1;
	public static final int ID_EGS_GESTURE_ITEM_SLIDE_UP = 2;
	public static final int ID_EGS_GESTURE_ITEM_SLIDE_DOWN = 3;
	public static final int ID_EGS_GESTURE_ITEM_SINGLE_TAP = 4;
	public static final int ID_EGS_GESTURE_ITEM_DOUBLE_TAP = 5;
	
	// receiver action
	public static final String RECEIVER_ACTION_UPDATE_ICON = "com.torv.easygesture.service.UPDATEICON";
	public static final String RECEIVER_ACTION_UPDATE_TOGGLE = "com.torv.easygesture.service.UPDATETOGGLE";
	
	// update icon type
	public static final int RECEIVER_UPDATE_ICON_TYPE = 1;
	public static final int RECEIVER_UPDATE_ICON_ALPHA = 2;

}