package com.torv.easygesture.frg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.torv.easygesture.OperationIntroActivity;
import com.torv.easygesture.R;
import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.Feature;
import com.torv.easygesture.common.SecLog;
import com.torv.easygesture.service.EasyGestureService;

public class FragmentSetting extends Fragment {

	protected static final String TAG = "FragmentSetting";

	private ToggleButton mTogBtnFunctionControl;
	private TextView mTvFunctionStatus;
	
	private ToggleButton mTogBtnRotateShot;
	private TextView mTvRotateShotStatus;
	
	private ToggleButton mTogBtnIconMove;
	private TextView mTvIconMoveStatus;
	
	private RelativeLayout mRlWakeUpScreen;
	private RelativeLayout mRlOperateIntro;
	private RelativeLayout mRlRestoreDefault;
	private RelativeLayout mRlShareWechat;

	private SharedPreferences mSharedPreferences;
	
	private Intent intent = new Intent(EasyGestureSetting.RECEIVER_ACTION_UPDATE_ICON);
	
	private View rootView;

	/////**********wechat stat**************/////
	private IWXAPI mWxApi;
	/////**********wechat end**************/////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SecLog.e(TAG, "onCreate");

		/////**********wechat stat**************/////
		if(Feature.FEATURE_SHARE_TO_WECHAT) {
			mWxApi = WXAPIFactory.createWXAPI(getActivity(), EasyGestureSetting.WECHAT_APP_ID,false);
		}
		/////**********wechat end**************/////

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		SecLog.e(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		SecLog.e(TAG, "onResume");
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		SecLog.e(TAG, "onCreateView");
		if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_setting, container, false);
            initView(rootView);
        }
		return rootView;
	}
	
	private void initView(View rootview){
		
		mTvFunctionStatus = (TextView)rootview.findViewById(R.id.id_tv_function_status);
		mTvRotateShotStatus = (TextView)rootview.findViewById(R.id.id_tv_rotate_shot_status);
		mTvIconMoveStatus = (TextView)rootview.findViewById(R.id.id_tv_icon_move_status);

		mSharedPreferences = getActivity().getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);

		// Enable Gesture
		mTogBtnFunctionControl = (ToggleButton)rootview.findViewById(R.id.id_tb_function_control);
		mTogBtnFunctionControl.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				
				SecLog.e(TAG, "onCheckedChanged, isChecked:"+isChecked);

				Intent intent = new Intent(getActivity(), EasyGestureService.class);
				
				
				SharedPreferences.Editor editor = mSharedPreferences.edit();

				if(isChecked){
					mTvFunctionStatus.setText(R.string.enabled);

					if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, false) == false)
						return;

					getActivity().stopService(intent);

					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, false);
					editor.commit();

					getActivity().startService(intent);

				}else{
					mTvFunctionStatus.setText(R.string.disabled);

					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, true);
					editor.commit();

					getActivity().stopService(intent);
				}
			}
			
		});
		
		// Enable icon move
		mTogBtnIconMove = (ToggleButton)rootview.findViewById(R.id.id_tb_enable_icon_move);
		mTogBtnIconMove.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				
				SharedPreferences.Editor editor = mSharedPreferences.edit();
				
				if(isChecked){
					mTvIconMoveStatus.setText(R.string.enabled);
					
					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED, false);
				}else{
					mTvIconMoveStatus.setText(R.string.disabled);
					
					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED, true);
				}
				
				editor.commit();
			}
			
		});
		
		// Enable rotate shot
		mTogBtnRotateShot = (ToggleButton)rootview.findViewById(R.id.id_tb_quick_capture);
		mTogBtnRotateShot.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				
				SharedPreferences.Editor editor = mSharedPreferences.edit();

				if(isChecked){
					mTvRotateShotStatus.setText(R.string.enabled);

					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ROTATE_SHOT_DISABLED, false);

				}else{
					mTvRotateShotStatus.setText(R.string.disabled);

					editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ROTATE_SHOT_DISABLED, true);
				}

				editor.commit();
			}

		});

		initTogBtnStatus();

		// wake up screen setting
		mRlWakeUpScreen = (RelativeLayout)rootview.findViewById(R.id.id_rl_wake_up_gesture);
		mRlWakeUpScreen.setClickable(true);
		mRlWakeUpScreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				final int currentWay = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, EasyGestureSetting.DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY);
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
				builder.setSingleChoiceItems(R.array.wakeupWay, currentWay, new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int item) {
						
						if(item != currentWay){
							SharedPreferences.Editor editor = mSharedPreferences.edit();
							editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, item);
							editor.commit();				
						}
				        dialog.cancel();
				  } 
				});
				builder.setTitle(R.string.title_wake_up_dialog);
				dialog = builder.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
			
		});

		mRlOperateIntro = (RelativeLayout)rootview.findViewById(R.id.id_rl_operation_intro);
		mRlOperateIntro.setClickable(true);
		mRlOperateIntro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(getActivity(), OperationIntroActivity.class);
				startActivity(intent);
			}
			
		});

		mRlRestoreDefault = (RelativeLayout)rootview.findViewById(R.id.id_rl_restore_default);
		mRlRestoreDefault.setClickable(true);
		mRlRestoreDefault.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
				builder.setTitle(R.string.title_reset_dialog);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetSeting();
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				dialog = builder.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}

		});
		
		/////**********wechat stat**************/////
		if(Feature.FEATURE_SHARE_TO_WECHAT) {
//			mRlShareWechat = (RelativeLayout)rootview.findViewById(R.id.id_rl_share_wechat);
			mRlShareWechat.setClickable(true);
			mRlShareWechat.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View arg0) {
	
					mWxApi.registerApp(EasyGestureSetting.WECHAT_APP_ID);
	
					WXTextObject textObj = new WXTextObject();
					textObj.text = "title?";
					
					WXMediaMessage msg = new WXMediaMessage();
					msg.mediaObject = textObj;
					
					msg.title = "title2?";
					msg.description = "description";
					
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = "transaction?";
					req.message = msg;
					
					mWxApi.sendReq(req);
					Toast.makeText(getActivity(), "send req", Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		/////**********wechat end**************/////
	}

	public void initTogBtnStatus(){
		SecLog.e(TAG, "initTogBtnStatus");

		if(null != mTogBtnFunctionControl){
			boolean setCheck = !mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_FUNCTION_DISABLED);
			if(mTogBtnFunctionControl.isChecked() != setCheck)
				mTogBtnFunctionControl.setChecked(setCheck);
		}
		
		if(null != mTogBtnRotateShot){
			boolean setCheck = !mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ROTATE_SHOT_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_ROTATE_SHOT_DISABLED);
			if(mTogBtnRotateShot.isChecked() != setCheck)
				mTogBtnRotateShot.setChecked(setCheck);
		}
		
		if(null != mTogBtnIconMove){
			boolean setCheck = !mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_ICON_MOVE_DISABLED, EasyGestureSetting.DEFAULT_VALUE_BOOL_ICON_MOVE_DISABLED);
			if(mTogBtnIconMove.isChecked() != setCheck)
				mTogBtnIconMove.setChecked(setCheck);
		}
		
	}
	
	
	public void resetSeting(){
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_LEFT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_LEFT);
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_RIGHT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_RIGHT);
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_UP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_UP);
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_DOWN, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_DOWN);
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SINGLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SINGLE_TAP);
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_DOUBLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_DOUBLE_TAP);
		
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_WAKEUP_SCREEN_WAY, EasyGestureSetting.DEFAULT_VALUE_INT_WAKEUP_SCREEN_WAY);
		
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ICON, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ICON);
		intent.putExtra("updateType", EasyGestureSetting.RECEIVER_UPDATE_ICON_TYPE);
		getActivity().sendBroadcast(intent);
		
		editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ALPHA, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ALPHA);
		intent.putExtra("updateType", EasyGestureSetting.RECEIVER_UPDATE_ICON_ALPHA);
		getActivity().sendBroadcast(intent);
		
		editor.commit();
	}
}