package com.torv.easygesture.service;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.torv.easygesture.AdminDummyActivity;
import com.torv.easygesture.DeviceLockReceiver;
import com.torv.easygesture.R;
import com.torv.easygesture.ShowDialogDummyActivity;
import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.SecLog;

public class EasyGestureService extends Service implements SensorEventListener{

	private static final String TAG = "EasyGestureService";
	
	private SharedPreferences mSharedPreferences;
	private LinearLayout mFloatLayout;
	private ImageButton mBtnImage;
	private WindowManager.LayoutParams wmParams;
	private WindowManager mWindowManager;
	private LinearLayout mFullScreenCheckView;
	
	private static boolean bIsFullScreen = false;
	
	private MyOrientationEventListener mOrientationEventListener;
	private static int mStatusBarHeight = 0;
	
	private static float mPreviousTouchX = 0;
	private static float mPreviousTouchY = 0;
	
	private static boolean bIsMoved = false;
	private static boolean bIsLongClicked = false;
	
	private static long mPreviousClickTime;
	private static boolean bIsDoubleClicked = false;
	
	private static final int GESTURE_LENGTH = 100;
	
	private Camera mCamera = null;
	private static boolean bIsTouchLightOpened = false;
	
	private SensorManager mSensorManager = null;
	private static int mPreviousAxisY;
	private static int mPreviousAxisX;
	
	private static long mPreviousFlashotTime;

	private IconUpdateReceiver mIconUpdateReceiver;
	
	private PowerManager mPowerManager;
	private WakeLock mWakelock;
	
	private KeyguardManager mKeyguardManager;
	private InputMethodManager mInputMethodManager;
	
	private static boolean bIsViewRemoved;

//	private Thread mLooperThread = new Thread(new Runnable(){
//
//		Handler mLooperHandler;
//		@Override
//		public void run() {
//			Looper.prepare();
//
//			mLooperHandler = new Handler(){
//
//				@Override
//				public void handleMessage(Message msg) {
//					SecLog.e(TAG, "handleMessage");
//					mLooperHandler.sendEmptyMessageDelayed(0, 1000);
//					super.handleMessage(msg);
//				}
//				
//			};
//			
//			SecLog.e(TAG, "sendEmptyMessageDelayed");
//			mLooperHandler.sendEmptyMessageDelayed(0, 1000);
//			Looper.loop();
//			
//		}
//		
//	});
	
	private HandlerThread mHandlerThread;
	private Handler mLooperHandler;
	
	private DevicePolicyManager policyManager;
    private ComponentName componentName;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		SecLog.e(TAG, "onDestroy");
		if(null != mSensorManager)
			mSensorManager.unregisterListener(this);
		
		if(null != mIconUpdateReceiver)
			unregisterReceiver(mIconUpdateReceiver);

		if(mFloatLayout != null)  
        {  
            mWindowManager.removeView(mFloatLayout);
        }
		
		if(mFullScreenCheckView != null){
			mWindowManager.removeView(mFullScreenCheckView);
		}
		
		if(null != mOrientationEventListener){
			mOrientationEventListener.disable();
		}
		
		if(mWakelock != null){
			mWakelock.release();
		}

		if(mHandlerThread != null){
			mHandlerThread.getLooper().quit();
		}

		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED) == false){
			Intent intent = new Intent(this, EasyGestureService.class);
			startService(intent);	
		}

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mSharedPreferences = getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
		
		SecLog.e(TAG, "onStartCommand:"+mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED));

		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED) == true){
			
			SecLog.e(TAG, "onStartCommand stopself");
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
			
		}else{
			
			mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);
			mWakelock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepBackground");
			mWakelock.acquire();

			LayoutInflater inflater = LayoutInflater.from(getApplication());
			mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
			mBtnImage = (ImageButton)mFloatLayout.findViewById(R.id.btn_gesture);
			updateIconType();
			updateIconAlpha();
			mFullScreenCheckView = (LinearLayout) inflater.inflate(R.layout.full_screen_layout, null);
			
			mStatusBarHeight = getStatusBarHeight(getBaseContext());
			
			createFloatView();
			initBtnImageAction();
			createFullScreenCheckView();

			mOrientationEventListener = new MyOrientationEventListener(getBaseContext());
			mOrientationEventListener.enable();

			mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

			mIconUpdateReceiver = new IconUpdateReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(EasyGestureSetting.RECEIVER_ACTION_UPDATE_ICON);
			registerReceiver(mIconUpdateReceiver, intentFilter);
			
//			mLooperThread.setName("LooperThread");
//			mLooperThread.start();
			
			mHandlerThread = new HandlerThread("mHandlerThread");
			mHandlerThread.start();
			
			mLooperHandler = new Handler(mHandlerThread.getLooper()){

				@Override
				public void handleMessage(Message msg) {
					SecLog.e(TAG, "handleMessage");
					mLooperHandler.sendEmptyMessageDelayed(0, 1000);
					super.handleMessage(msg);
				}
				
			};
			mLooperHandler.sendEmptyMessageDelayed(0, 1000);
			
			policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
	        componentName = new ComponentName(this, DeviceLockReceiver.class);

			flags = START_STICKY;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void createFloatView() {
		SecLog.e(TAG, "createFloatView");
		
		wmParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.TRANSPARENT;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		initWindowPosition();
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		
		mWindowManager.addView(mFloatLayout, wmParams);
		bIsViewRemoved = false;
	}

	private void initWindowPosition(){
		SecLog.e(TAG, "initWindowPosition");
		
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);

		if(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_X) == EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_X){
			
			wmParams.x = dm.widthPixels/2 + 150;
			wmParams.y = dm.heightPixels/2;
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
			if(dm.widthPixels < dm.heightPixels){
				editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, false);
			}else{
				editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, true);
			}
			editor.commit();

		}else {

			int x = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, 0);
			int y = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, 0);

			if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, EasyGestureSetting.DEFAULT_VALUE_BOOL_IS_LANDSCAPE) == false){
				if(dm.widthPixels > dm.heightPixels){
					wmParams.x = y;
					wmParams.y = x;
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
					editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, true);
					editor.commit();
				}else{
					wmParams.x = x;
					wmParams.y = y;
				}
			}else{
				if(dm.widthPixels < dm.heightPixels){
					wmParams.x = y;
					wmParams.y = x;
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
					editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, false);
					editor.commit();
				}else{
					wmParams.x = x;
					wmParams.y = y;
				}
			}
		}
	}

	private void createFullScreenCheckView() {
		SecLog.e(TAG, "createFloatView");
		
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = LayoutParams.TYPE_PHONE;
		layoutParams.format = PixelFormat.TRANSPARENT;
		layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

		layoutParams.width = 1;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		
		mFullScreenCheckView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				SecLog.e(TAG, "onGlobalLayout, height="+mFullScreenCheckView.getHeight()+",mheight="+mFullScreenCheckView.getMeasuredHeight());

				DisplayMetrics dm = new DisplayMetrics();
				mWindowManager.getDefaultDisplay().getMetrics(dm);
				int viewHeight = mFullScreenCheckView.getHeight();

				if((viewHeight == dm.widthPixels || viewHeight == dm.heightPixels) && !bIsFullScreen){
					
					bIsFullScreen = true;

				}

				if(!(viewHeight == dm.widthPixels || viewHeight == dm.heightPixels) && bIsFullScreen){
					
					bIsFullScreen = false;

				}

			}
			
		});
		
		mWindowManager.addView(mFullScreenCheckView, layoutParams);
	}

	private int getStatusBarHeight(Context context){
		SecLog.e(TAG, "getStatusBarHeight");

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
	
	private class MyOrientationEventListener extends OrientationEventListener{

		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {

			updatePortraitLand();
		}

	}

	private void initBtnImageAction(){

		mBtnImage.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				SecLog.e(TAG, "onLongClick");
				
				bIsLongClicked =  true;
				mMainHandler.removeMessages(EasyGestureSetting.HANDLER_MSG_EGS_SINGLE_TOUCH);

				return false;
			}
			
		});
		
		mBtnImage.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getActionMasked()){
				case MotionEvent.ACTION_DOWN:

					mPreviousTouchX = event.getRawX();
					mPreviousTouchY = event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:

					if(Math.abs(mPreviousTouchX - event.getRawX()) < 10 && Math.abs(mPreviousTouchY - event.getRawY()) < 10)
						return false;

					bIsMoved = true;

					if(bIsLongClicked || (mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_ICON_MOVE_DISABLED) == false)) {
						wmParams.x = (int) (event.getRawX() - mFloatLayout.getMeasuredWidth()/2);
						if(bIsFullScreen){
							wmParams.y = (int) (event.getRawY() - mFloatLayout.getMeasuredHeight()/2);
						}else{
							wmParams.y = (int) (event.getRawY() - mStatusBarHeight - mFloatLayout.getMeasuredHeight()/2);
						}
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
					}

					break;
				case MotionEvent.ACTION_UP:
					
					long currentTime = System.currentTimeMillis();
					if(currentTime - mPreviousClickTime < EasyGestureSetting.DEFAULT_VALUE_INT_SINGLE_TAP_DELAY_TIME - 10){  // double tap
						bIsDoubleClicked = true;
						mMainHandler.removeMessages(EasyGestureSetting.HANDLER_MSG_EGS_SINGLE_TOUCH);

						doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_DOUBLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_DOUBLE_TAP));
						return false;

					}else{
						bIsDoubleClicked = false;
					}
					mPreviousClickTime = currentTime;

					if(!bIsLongClicked && !bIsMoved){ // Single tap

						mMainHandler.sendEmptyMessageDelayed(EasyGestureSetting.HANDLER_MSG_EGS_SINGLE_TOUCH, EasyGestureSetting.DEFAULT_VALUE_INT_SINGLE_TAP_DELAY_TIME);
					}

					if(bIsLongClicked) {  // Long lick move position
						bIsLongClicked = false;
						bIsMoved = false;
						SharedPreferences.Editor editor = mSharedPreferences.edit();
						editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
						editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
						editor.commit();
						
						return false;
					}
					
					// Not long click
					if(true == bIsMoved){
						bIsMoved = false;

						mMainHandler.removeMessages(EasyGestureSetting.HANDLER_MSG_EGS_SINGLE_TOUCH);

						if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_ICON_MOVE_DISABLED) == false) {
							wmParams.x = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, 0);
							wmParams.y = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, 0);
							mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						}

						if(event.getRawX() - mPreviousTouchX > GESTURE_LENGTH){ // Slide right

							doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_RIGHT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_RIGHT));

						}else if(event.getRawX() - mPreviousTouchX < 0-GESTURE_LENGTH){ // Slide left

							doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_LEFT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_LEFT));

						}else if(event.getRawY() - mPreviousTouchY > GESTURE_LENGTH){ // Slide down

							doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_DOWN, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_DOWN));

						}else if(event.getRawY() - mPreviousTouchY < 0-GESTURE_LENGTH){ // Slide up

							doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_UP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_UP));

						}
					}
					break;
				default:
					break;
				}
				return false;
			}
			
		});
	}
	
	protected Handler mMainHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			SecLog.e(TAG, "handleMessage:"+msg.what);;
			
			switch(msg.what){
			case EasyGestureSetting.HANDLER_MSG_EGS_SINGLE_TOUCH:
				
				if(bIsDoubleClicked){
					bIsDoubleClicked = false;
					return;
				}
				
				doAction(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SINGLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SINGLE_TAP));
				
				break;
			case EasyGestureSetting.HANDLER_MSG_EGS_NOT_SUPPORT_FLASH:

				Toast.makeText(EasyGestureService.this, R.string.not_support_flash, Toast.LENGTH_SHORT).show();

				break;
			case EasyGestureSetting.HANDLER_MSG_EGS_FLASH_NOT_AVAILABLE:

				Toast.makeText(EasyGestureService.this, R.string.flash_is_not_available_now, Toast.LENGTH_SHORT).show();

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}		
	};
	
	private void doShowRecents(){

		try{

			Intent intent = new Intent("com.android.systemui.recent.action.TOGGLE_RECENTS");
			if(android.os.Build.VERSION.SDK_INT > 20) {
				intent.setClassName("com.android.systemui", "com.android.systemui.recents.RecentsActivity");
			}else{
				intent.setClassName("com.android.systemui", "com.android.systemui.recent.RecentsActivity");
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);


		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(EasyGestureService.this, R.string.android_version_not_support, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doBackHome(){
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		
	}
	
	private void doBackKey(){

		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				Instrumentation mInstrumentation = new Instrumentation();
				mInstrumentation.sendCharacterSync(KeyEvent.KEYCODE_BACK);
			}
			
		});
		t.start();
	}
	
	private void doExpandNotification(){
		
		Object service = getSystemService("statusbar");
		try {
			Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			Method expand = null;
			if(android.os.Build.VERSION.SDK_INT <= 16) {
				expand = statusbarManager.getMethod("expand");
			}else{
				expand = statusbarManager.getMethod("expandNotificationsPanel");
			}
			expand.setAccessible(true);
			expand.invoke(service);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(EasyGestureService.this, R.string.android_version_not_support, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void doOpenTorch(){
		
		Thread openTorchThread = new Thread(new Runnable(){

			@Override
			public void run() {
				if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
				{
					mMainHandler.sendEmptyMessage(EasyGestureSetting.HANDLER_MSG_EGS_NOT_SUPPORT_FLASH);
					return;
				}

				try{
					if(!bIsTouchLightOpened){
						if(null == mCamera){
							mCamera = Camera.open();
							Parameters mParam = mCamera.getParameters();
							mParam.setFlashMode(Parameters.FLASH_MODE_TORCH);
							mCamera.setParameters(mParam);
							mCamera.startPreview();
							bIsTouchLightOpened = true;
						}
					}else{
						if(mCamera != null){
							mCamera.stopPreview();
							mCamera.release();
							mCamera = null;
							bIsTouchLightOpened = false;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					mMainHandler.sendEmptyMessage(EasyGestureSetting.HANDLER_MSG_EGS_FLASH_NOT_AVAILABLE);
				}
			}
			
		});
		openTorchThread.setName("OpenTorchThread");
		openTorchThread.start();
	}
	
	private void doLockScreen(){
		
		if(policyManager != null && policyManager.isAdminActive(componentName)){
			policyManager.lockNow();
		}else{
			Intent intent = new Intent(getBaseContext(), AdminDummyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
	}
	
	private void doHideView(){

		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, true);
		editor.commit();
		
		Intent intent = new Intent(EasyGestureSetting.RECEIVER_ACTION_UPDATE_TOGGLE);
		sendBroadcast(intent);

		SecLog.e(TAG, "call stopself");
		stopSelf();
	}
	
	private void doAdjustAlpha(){

		Intent intent = new Intent(this, ShowDialogDummyActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
	}
	
	private void doAction(int actionId){
		
		switch(actionId){
		case EasyGestureSetting.ACTION_CLOSE:
			break;
		case EasyGestureSetting.ACTION_OPEN_TORCH:

			doOpenTorch();

			break;
		case EasyGestureSetting.ACTION_BACK_HOME:

			doBackHome();

			break;
		case EasyGestureSetting.ACTION_EXPAND_NOTI:

			doExpandNotification();

			break;
		case EasyGestureSetting.ACTION_HIDE_VIEW:

			doHideView();

			break;
		case EasyGestureSetting.ACTION_SHOW_RECENT:

			doShowRecents();

			break;
		case EasyGestureSetting.ACTION_LOCK_SCREEN:

			doLockScreen();

			break;
		case EasyGestureSetting.ACTION_ADJUST_ALPHA:

			doAdjustAlpha();

			break;
		case EasyGestureSetting.ACTION_QUICK_SHOT:

			startFlashotThread();

			break;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if(mKeyguardManager.inKeyguardRestrictedInputMode() && !bIsViewRemoved && mFloatLayout != null) {
			mWindowManager.removeView(mFloatLayout);
			bIsViewRemoved = true;
		}else if(!mKeyguardManager.inKeyguardRestrictedInputMode() && bIsViewRemoved && mFloatLayout != null){
			mWindowManager.addView(mFloatLayout, wmParams);
			bIsViewRemoved = false;
		}

		float[] values = event.values;

		SecLog.e(TAG, "x="+values[0]);
		SecLog.e(TAG, "y="+values[1]);
		SecLog.e(TAG, "z="+values[2]);
		///////////////////////////////////////////////////////////////////////////////////
		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ROTATE_SHOT_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_ROTATE_SHOT_DISABLED) == false){
			if(values[0] < -9.5 && values[0] > -9.7){
				mPreviousAxisX = -9;
			}
			
			if(values[0] > 9 && values[0] < 10 && -9 == mPreviousAxisX){
				mPreviousAxisX = 0;
				
				startFlashotThread();

			}
		}
		///////////////////////////////////////////////////////////////////////////////////

		if(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, EasyGestureSetting.DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY) == 0){
			return;
		}else if(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, EasyGestureSetting.DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY) == 1){

			if(values[1] < -9){
				mPreviousAxisY = -9;
			}
			
			if(values[1] > 7 && -9 == mPreviousAxisY){
				mPreviousAxisY = 0;

				if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
					
					WakeLock wakelock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.FULL_WAKE_LOCK |PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WakeupScreen");
					wakelock.acquire(1000);
				}

			}

		}else if(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, EasyGestureSetting.DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY) == 2){
			
			if(Math.abs(values[1]) < 2){
				mPreviousAxisY = 2;
			}
			
			if(values[1] > 7 && 2 == mPreviousAxisY){
				mPreviousAxisY = 0;

				if (mKeyguardManager.inKeyguardRestrictedInputMode()) {

					WakeLock wakelock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.FULL_WAKE_LOCK |PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WakeupScreen");
					wakelock.acquire(1000);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	private class IconUpdateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			switch(intent.getIntExtra("updateType", 0)){
			case EasyGestureSetting.RECEIVER_UPDATE_ICON_TYPE:

				updateIconType();

				break;
			case EasyGestureSetting.RECEIVER_UPDATE_ICON_ALPHA:

				updateIconAlpha();

				break;
			default:
				break;
			}
		}
		
	}

	private void updatePortraitLand(){

		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED) == true){
			return;
		}

		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		
		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, EasyGestureSetting.DEFAULT_VALUE_BOOL_IS_LANDSCAPE) == false){
			if(dm.widthPixels > dm.heightPixels){
				SecLog.e(TAG, "portrait -> land");
				SecLog.e(TAG, "1 wmParams.x="+wmParams.x+",wmParams.y="+wmParams.y);
				wmParams.x = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_Y);
				wmParams.y = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_X);
				SecLog.e(TAG, "2 wmParams.x="+wmParams.x+",wmParams.y="+wmParams.y);
				mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				
				SharedPreferences.Editor editor = mSharedPreferences.edit();
				editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
				editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
				editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, true);
				editor.commit();
			}
		}else{
			if(dm.widthPixels < dm.heightPixels){
				SecLog.e(TAG, "land -> portrait");
				SecLog.e(TAG, "1 wmParams.x="+wmParams.x+",wmParams.y="+wmParams.y);
				wmParams.x = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_Y);
				wmParams.y = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, EasyGestureSetting.DEFAULT_VALUE_INT_WINDOW_X);
				SecLog.e(TAG, "2 wmParams.x="+wmParams.x+",wmParams.y="+wmParams.y);
				mWindowManager.updateViewLayout(mFloatLayout, wmParams);

				SharedPreferences.Editor editor = mSharedPreferences.edit();
				editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_X, wmParams.x);
				editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WINDOW_Y, wmParams.y);
				editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_LANDSCAPE, false);
				editor.commit();
			}
		}
	}
	private void updateIconType(){
		
		switch(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ICON, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ICON)){
		case 0:
			mBtnImage.setImageResource(R.drawable.float_icon_1);
			break;
		case 1:
			mBtnImage.setImageResource(R.drawable.float_icon_2);
			break;
		case 2:
			mBtnImage.setImageResource(R.drawable.float_icon_3);
			break;
		case 3:
			mBtnImage.setImageResource(R.drawable.float_icon_4);
			break;
		case 4:
			mBtnImage.setImageResource(R.drawable.float_icon_5);
			break;
		case 5:
			mBtnImage.setImageResource(R.drawable.float_icon_6);
			break;
		case 6:
			mBtnImage.setImageResource(R.drawable.float_icon_7);
			break;
		case 7:
			mBtnImage.setImageResource(R.drawable.float_icon_8);
			break;
		case 8:
			mBtnImage.setImageResource(R.drawable.float_icon_9);
			break;
		case 9:
			mBtnImage.setImageResource(R.drawable.float_icon_10);
			break;
		}
	}
	
	private void updateIconAlpha(){
		
		mBtnImage.setAlpha(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ALPHA, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ALPHA));
	}
	
	private void startFlashotThread(){

		Thread t = new Thread(new Runnable(){
			
			@Override
			public void run() {
				flashot();
			}
			
		});
		t.setName("FlashotThread");
		t.start();
	}

	private void flashot(){

		if(System.currentTimeMillis() - mPreviousFlashotTime < 3000){
			SecLog.e(TAG, "wait 3 s");
			return;
		}
		mPreviousFlashotTime = System.currentTimeMillis();

		try {

			if(mCamera == null){

				mCamera = Camera.open();				

				SurfaceTexture mDummySurfaceTexture = new SurfaceTexture(100);

				mCamera.setPreviewTexture(mDummySurfaceTexture);

				mCamera.startPreview();

				FlashotAutoFocusCallback mAutoFocusCallback = new FlashotAutoFocusCallback();
				mCamera.autoFocus(mAutoFocusCallback);
			}

		} catch (Exception e) {
			if(mCamera != null){
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
	}

	private class  FlashotPictureCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			SecLog.e(TAG, "onPictureTaken");
			FileOutputStream outStream = null;

			try {
				String dir = Environment.getExternalStorageDirectory()+File.separator+"EasyGesture";
				SecLog.e(TAG, "dir="+dir);
				File destDir = new File(dir);
				if(!destDir.exists()){
					destDir.mkdirs();
				}

				String name = String.format("%d.jpg", System.currentTimeMillis());

				outStream = new FileOutputStream(dir+File.separator+name);	
				outStream.write(data);
				outStream.close();

				mCamera.release();
				mCamera = null;
				Toast.makeText(EasyGestureService.this, "Saved:"+dir+File.separator+name, Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				if(mCamera != null){
					mCamera.release();
					mCamera = null;
				}
				e.printStackTrace();
			}
		}
		
	}
	
	private class  FlashotAutoFocusCallback implements AutoFocusCallback{

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			
			try{
				FlashotPictureCallback mPictureCallback = new FlashotPictureCallback();
				mCamera.takePicture(null, null, mPictureCallback);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
}