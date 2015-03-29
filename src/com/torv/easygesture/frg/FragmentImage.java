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
import android.widget.RelativeLayout;

import com.torv.easygesture.R;
import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.SecLog;


public class FragmentImage extends Fragment {

	private static final String TAG = "FragmentImage";
	
	private RelativeLayout mRlChooseIcon;
	private RelativeLayout mRlAdjustAlpha;
	
	private SharedPreferences mSharedPreferences;
	
	private Intent intent = new Intent(EasyGestureSetting.RECEIVER_ACTION_UPDATE_ICON);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_image, container, false);
		
		mSharedPreferences = getActivity().getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
		
		mRlChooseIcon = (RelativeLayout)view.findViewById(R.id.id_rl_select_icon);
		mRlChooseIcon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final int currentIcon = mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ICON, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ICON);
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
				builder.setSingleChoiceItems(R.array.iconArray, currentIcon, new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int item) {
						
						if(item != currentIcon){
							SecLog.e(TAG, "item = "+item);
							SharedPreferences.Editor editor = mSharedPreferences.edit();
							editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ICON, item);
							editor.commit();
							
							intent.putExtra("updateType", EasyGestureSetting.RECEIVER_UPDATE_ICON_TYPE);
							getActivity().sendBroadcast(intent);
						}
				        dialog.cancel();
				  }
				});
				builder.setTitle(R.string.title_select_icon);
				dialog = builder.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
			
		});
		mRlChooseIcon.setClickable(true);
		
		mRlAdjustAlpha = (RelativeLayout)view.findViewById(R.id.id_rl_adjust_alpha);
		mRlAdjustAlpha.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				AlphaSeekbarDialog mAlphaSeekbarDialog = new AlphaSeekbarDialog(getActivity(), R.style.AlertDialogCustom);
				mAlphaSeekbarDialog.show();

			}
			
		});
		mRlAdjustAlpha.setClickable(true);

		return view;
	}
}