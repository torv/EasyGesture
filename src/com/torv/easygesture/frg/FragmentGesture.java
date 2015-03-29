package com.torv.easygesture.frg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.torv.easygesture.R;
import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.SecLog;
import com.torv.easygesture.common.Util;

public class FragmentGesture extends ListFragment {
	
	private static final String TAG = "FragmentGesture";

	private GestureSettingListAdapter adapter = null;
	private SharedPreferences mSharedPreferences;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        adapter = new GestureSettingListAdapter (
        		getActivity(), 
        		getResources().getStringArray(R.array.settingArray), 
        		getResources().getStringArray(R.array.actionArray));
        setListAdapter(adapter);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mSharedPreferences = getActivity().getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
		return inflater.inflate(R.layout.fragment_gesture, container, false);
	}
	
	@Override
	public void onListItemClick(ListView l, final View v, final int position, long id) {
		SecLog.e(TAG, "position="+position+",id="+id);
		
		final int currentAction = mSharedPreferences.getInt(Util.getSettingNameById(position), 0);

		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
		builder.setSingleChoiceItems(R.array.actionArray, currentAction, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int item) {
				
				if(item != currentAction){
					((TextView)v.findViewById(R.id.id_tv_gesture_action)).setText(Util.getActionStringById(getActivity(), item));
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt(Util.getSettingNameById(position), item);
					editor.commit();
				}
		        dialog.cancel();
		  } 
		});
		builder.setTitle(R.string.title_select_action);
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

		super.onListItemClick(l, v, position, id);
	}
	
}
