package com.torv.easygesture;

import android.app.Activity;
import android.os.Bundle;

public class OperationIntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operate_intro);
	}

	@Override
	protected void onPause() {

		finish();

		super.onPause();
	}

}