package com.torv.easygesture;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.torv.easygesture.common.EasyGestureSetting;
import com.torv.easygesture.frg.FragmentGesture;
import com.torv.easygesture.frg.FragmentImage;
import com.torv.easygesture.frg.FragmentSetting;
import com.torv.easygesture.service.EasyGestureService;
import com.torv.easygesture.update.UpdateManager;

public class EasyGestureActivity extends FragmentActivity implements OnClickListener{

	private FragmentSetting mFragmentSetting = new FragmentSetting();
	private FragmentImage mFragmentImage = new FragmentImage();
	private FragmentGesture mFragmentGesture = new FragmentGesture();
	
	private LinearLayout mTabSetting;
	private LinearLayout mTabImage;
	private LinearLayout mTabGesture;
	
	private ViewPager mViewPager;  
    private ViewPagerAdapter mViewPagerAdapter;
    
    private static final int TAB_INDEX_COUNT = 3;  
    
    private static final int TAB_INDEX_ONE = 0;  
    private static final int TAB_INDEX_TWO = 1;  
    private static final int TAB_INDEX_THREE = 2;
    
    private SharedPreferences mSharedPreferences;
    
    private ToggleUpdateReceiver mToggleUpdateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easy_gesture);

		mSharedPreferences = getSharedPreferences(EasyGestureSetting.PREFERENCES_PRIVATE_EASY_GESTURE_SERVICE, Activity.MODE_PRIVATE);

		mTabSetting = (LinearLayout) findViewById(R.id.id_tab_bottom_setting);
		mTabImage = (LinearLayout) findViewById(R.id.id_tab_bottom_image);
		mTabGesture = (LinearLayout) findViewById(R.id.id_tab_bottom_gesture);

		mTabSetting.setOnClickListener(this);
		mTabImage.setOnClickListener(this);
		mTabGesture.setOnClickListener(this);

		initDefaultSetting();
		setUpViewPager();
		
		mToggleUpdateReceiver = new ToggleUpdateReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(EasyGestureSetting.RECEIVER_ACTION_UPDATE_TOGGLE);
		registerReceiver(mToggleUpdateReceiver, intentFilter);
	}
	
	@Override
	protected void onResume() {
		
		// check update
		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkVersion();

		super.onResume();
	}

	@Override
	protected void onDestroy() {

		unregisterReceiver(mToggleUpdateReceiver);

		super.onDestroy();
	}

	@Override
	protected void onPause() {
//		finish();
		super.onPause();
	}

	public void initDefaultSetting(){
		
		String localVersionName = null;
		try {
			localVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_FIRST_TIME, true)){
		
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			
			editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_IS_FIRST_TIME, false);
			
			editor.putBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, false);
			
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_LEFT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_LEFT);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_RIGHT, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_RIGHT);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_UP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_UP);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SLIDE_DOWN, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SLIDE_DOWN);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_SINGLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_SINGLE_TAP);
			editor.putInt(EasyGestureSetting.KEY_PREFERENCES_EGS_GESTURE_ITEM_DOUBLE_TAP, EasyGestureSetting.DEFAULT_VALUE_INT_GESTURE_ITEM_DOUBLE_TAP);
			
			editor.commit();
			
			Intent intent = new Intent(this, EasyGestureService.class);
			stopService(intent);
			startService(intent);
		}else if(localVersionName != null && !localVersionName.equals(mSharedPreferences.getString(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_VERSION_NAME, null))){

			SharedPreferences.Editor editor = mSharedPreferences.edit();
			editor.putString(EasyGestureSetting.KEY_PREFERENCES_EGS_CURRENT_VERSION_NAME, localVersionName);
			editor.commit();

			if(mSharedPreferences.getBoolean(EasyGestureSetting.KEY_PREFERENCES_EGS_FUNCTION_DISABLED, false) == false){
				Intent intent = new Intent(this, EasyGestureService.class);
				stopService(intent);
				startService(intent);
			}
		}
	}

	private void setUpViewPager() {  
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  
          
        mViewPager = (ViewPager)findViewById(R.id.pager);  
        mViewPager.setAdapter(mViewPagerAdapter);  
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            	switch(position){
            	case TAB_INDEX_ONE:
            		mTabSetting.setBackgroundColor(getResources().getColor(R.color.blue));
        			mTabImage.setBackgroundColor(getResources().getColor(R.color.gray));
        			mTabGesture.setBackgroundColor(getResources().getColor(R.color.gray));
            		break;
            	case TAB_INDEX_TWO:
            		mTabSetting.setBackgroundColor(getResources().getColor(R.color.gray));
        			mTabImage.setBackgroundColor(getResources().getColor(R.color.blue));
        			mTabGesture.setBackgroundColor(getResources().getColor(R.color.gray));
            		break;
            	case TAB_INDEX_THREE:
            		mTabSetting.setBackgroundColor(getResources().getColor(R.color.gray));
        			mTabImage.setBackgroundColor(getResources().getColor(R.color.gray));
        			mTabGesture.setBackgroundColor(getResources().getColor(R.color.blue));
            		break;
            	}
            }  
              
            @Override  
            public void onPageScrollStateChanged(int state) {  
                switch(state) {  
                    case ViewPager.SCROLL_STATE_IDLE:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_DRAGGING:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_SETTLING:  
                        //TODO  
                        break;  
                    default:  
                        //TODO  
                        break;  
                }  
            }  
        });  
    }

	public class ViewPagerAdapter extends FragmentPagerAdapter {  
		  
        public ViewPagerAdapter(FragmentManager fragmentManager) {  
            super(fragmentManager);  
        }  

		@Override  
        public Fragment getItem(int position) {  
            switch (position) {  
                case TAB_INDEX_ONE:
                    return mFragmentSetting;
                case TAB_INDEX_TWO:
                    return mFragmentImage;
                case TAB_INDEX_THREE:
                    return mFragmentGesture;
            }  
            throw new IllegalStateException("No fragment at position " + position);  
        }  
  
        @Override  
        public int getCount() {  
            return TAB_INDEX_COUNT;  
        }  
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_tab_bottom_setting:
			
			mTabSetting.setBackgroundColor(getResources().getColor(R.color.blue));
			mTabImage.setBackgroundColor(getResources().getColor(R.color.gray));
			mTabGesture.setBackgroundColor(getResources().getColor(R.color.gray));			

			mViewPager.setCurrentItem(TAB_INDEX_ONE);
			break;
		case R.id.id_tab_bottom_image:
			
			mTabSetting.setBackgroundColor(getResources().getColor(R.color.gray));
			mTabImage.setBackgroundColor(getResources().getColor(R.color.blue));
			mTabGesture.setBackgroundColor(getResources().getColor(R.color.gray));			

			mViewPager.setCurrentItem(TAB_INDEX_TWO);
			break;
		case R.id.id_tab_bottom_gesture:

			mTabSetting.setBackgroundColor(getResources().getColor(R.color.gray));
			mTabImage.setBackgroundColor(getResources().getColor(R.color.gray));
			mTabGesture.setBackgroundColor(getResources().getColor(R.color.blue));			

			mViewPager.setCurrentItem(TAB_INDEX_THREE);
			break;
		}
	}
	
	private class ToggleUpdateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if(mFragmentSetting != null){
				mFragmentSetting.initTogBtnStatus();
			}
		}
		
	}
}
