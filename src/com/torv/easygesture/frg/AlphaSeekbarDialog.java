package com.torv.easygesture.frg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.torv.easygesture.R;
import com.torv.easygesture.common.EasyGestureSetting;

public class AlphaSeekbarDialog extends AlertDialog {

	private SeekBar mSeekBarAlpha;
	private TextView mTvAlpha;

	public AlphaSeekbarDialog(final Context context) {

		super(context);
	}

	public AlphaSeekbarDialog(final Context context, int theme) {
		super(context, theme);
		
		final SharedPreferences mSharedPreferences = context.getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
		
		View view = getLayoutInflater().inflate(R.layout.seek_bar_dialog, null);
		mSeekBarAlpha = (SeekBar) view.findViewById(R.id.id_alpha_seekbar);
		mTvAlpha = (TextView) view.findViewById(R.id.id_tv_alpha);
		setView(view);
		
		mTvAlpha.setText(""+mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ALPHA, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ALPHA));

		mSeekBarAlpha.setProgress(mSharedPreferences.getInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ALPHA, EasyGestureSetting.DEFAULT_VALUE_INT_CURRENT_ALPHA));
		mSeekBarAlpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTvAlpha.setText(""+progress);
				
				SharedPreferences.Editor editor = mSharedPreferences.edit();
				editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_ALPHA, progress);
				editor.commit();
				
				Intent intent = new Intent(EasyGestureSetting.RECEIVER_ACTION_UPDATE_ICON);
				intent.putExtra("updateType", EasyGestureSetting.RECEIVER_UPDATE_ICON_ALPHA);
				context.sendBroadcast(intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
		});
		
	}

}
