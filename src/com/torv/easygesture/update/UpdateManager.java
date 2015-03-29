package com.torv.easygesture.update;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import com.torv.easygesture.R;
import com.torv.easygesture.common.SecLog;

public class UpdateManager {
	
	private static final String TAG = "UpdateManager";

	private Context context;
	private UpdateInfo info;
	private String localVersion;
	
	private Thread mCheckVersionThread;
	
	private static final int HANDLER_MSG_NEED_UPDATE = 1;

	public UpdateManager(Context context){
		this.context = context;
	}
	
	private String getVersionName() throws Exception{
		
		if(context == null){
			return null;
		}

		PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		return packInfo.versionName;
	}
	
	public void checkVersion(){

		mCheckVersionThread = new Thread(new Runnable(){

			InputStream is;

			@Override
			public void run() {
				
				try {

					localVersion = getVersionName();
					SecLog.e(TAG, "localVersion"+localVersion);
					
					if(null == context && null == localVersion){
						return;
					}

					String path = context.getResources().getString(R.string.url_version_xml);
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					SecLog.e(TAG, "conn"+conn);
					if(conn != null){
						conn.setConnectTimeout(5000);
						conn.setRequestMethod("GET");
						int responseCode = conn.getResponseCode();

						SecLog.e(TAG, "responseCode"+responseCode);
						if(200 == responseCode){
							is = conn.getInputStream();
							info = UpdataInfoParser.getUpdateInfo(is);

							if(info != null)
								SecLog.e(TAG, "version:"+info.getVersion()+",url="+info.getUrl()+",description="+info.getDescription()+",xml:"+info.getXmlServer());
							if(info != null && info.getVersion() != null && !(info.getVersion().equals(localVersion))){
								handler.sendEmptyMessage(HANDLER_MSG_NEED_UPDATE);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});

		mCheckVersionThread.setName("CheckVersionThread");
		mCheckVersionThread.start();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case HANDLER_MSG_NEED_UPDATE:
				showUpdateDialog();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	protected void showUpdateDialog(){

		if(null == context){
			return;
		}

		AlertDialog.Builder builder = new Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
		builder.setTitle(R.string.version_update);
		builder.setMessage(info.getDescription());
		
		builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				if(null != context){

					Uri uri = Uri.parse(info.getUrl());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					context.startActivity(intent);
				}				
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
}
