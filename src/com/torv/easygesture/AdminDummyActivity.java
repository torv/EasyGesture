package com.torv.easygesture;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AdminDummyActivity extends Activity {

	private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private static final int ADMIN_REQUEST_CODE = 1987;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceLockReceiver.class);
        
        
		activeManage();

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	private void activeManage(){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.app_name);
		startActivityForResult(intent, ADMIN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode){
		case RESULT_OK:
			if(ADMIN_REQUEST_CODE == requestCode){
				policyManager.lockNow();
				finish();
			}
			break;
		default:
			finish();
			break;
		}

		finish();

		super.onActivityResult(requestCode, resultCode, data);
	}

}