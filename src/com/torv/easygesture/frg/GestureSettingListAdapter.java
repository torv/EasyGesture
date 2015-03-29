package com.torv.easygesture.frg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.torv.easygesture.R;
import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.common.SecLog;
import com.torv.easygesture.common.Util;


class ViewHolder {  
	public TextView item;
	public TextView action;
	public ImageView arrow;  
}  

public class GestureSettingListAdapter extends BaseAdapter {
	
	private static final String TAG = "GestureSettingListAdapter";

	private LayoutInflater mInflater = null;
	
	private String[] mainArray;
	private String[] subArray;
	
	private SharedPreferences mSharedPreferences;
	
	public GestureSettingListAdapter(Context context, String[] mainArray, String[] subArray){
		super();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.mainArray =mainArray;
		this.subArray = subArray;
		
		mSharedPreferences = context.getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return mainArray.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;  
		if (convertView == null) {  
			holder = new ViewHolder();  
			convertView = mInflater.inflate(R.layout.gesture_list_item, null);
			holder.item = (TextView) convertView.findViewById(R.id.id_tv_gesture_item);
			holder.action = (TextView) convertView.findViewById(R.id.id_tv_gesture_action);
			holder.arrow = (ImageView) convertView.findViewById(R.id.id_im_arrow);

			convertView.setTag(holder);
		} else {  
			holder = (ViewHolder) convertView.getTag();  
		}

		holder.item.setText(mainArray[position]);
		holder.action.setText(subArray[mSharedPreferences.getInt(Util.getSettingNameById(position), 0)]);

		return convertView;
	}  

}