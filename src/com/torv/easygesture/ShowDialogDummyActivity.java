package com.torv.easygesture;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

import com.torv.easygesture.frg.AlphaSeekbarDialog;

public class ShowDialogDummyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlphaSeekbarDialog mAlphaSeekbarDialog = new AlphaSeekbarDialog(this, R.style.AlertDialogCustom);
		mAlphaSeekbarDialog.show();
		mAlphaSeekbarDialog.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss(DialogInterface arg0) {
				finish();
			}
			
		});
	}

}
