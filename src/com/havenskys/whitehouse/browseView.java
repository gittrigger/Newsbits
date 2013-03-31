package com.havenskys.whitehouse;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class browseView extends Activity {

	private static String TAG = "Browse";
	
	private WebView mSummary, mContent;
	private TextView mTitle, mDate;
	private LinearLayout mLinearLayout;
	private String mLink;
	//private Bundle mIntentExtras;
	private long mCurrentID = 0;
	private SharedPreferences mSharedPreferences;
	private Editor mPreferencesEditor;
	private Custom mLog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLog = new Custom(this);
		
		setContentView(R.layout.browser);

		mContent = (WebView) this.findViewById(R.id.browser_viewer);
		mContent.setBackgroundColor(Color.BLACK);
		
		//mContent.setVisibility(View.INVISIBLE);
		//mHideData.sendEmptyMessageDelayed(1, 10);
		
		mSharedPreferences = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
        mPreferencesEditor = mSharedPreferences.edit();
        long id = mSharedPreferences.contains("id") ? mSharedPreferences.getLong("id",0) : 0;
        //mPreferencesEditor.putLong("id", id); mPreferencesEditor.commit();


		//mIntentExtras = getIntent().getExtras();
		//long id = mIntentExtras != null ? mIntentExtras.getLong("id") : 0;
		
		mLinearLayout = (LinearLayout) this.findViewById(R.id.browser);
		mTitle = (TextView) this.findViewById(R.id.browser_title);
		mDate = (TextView) this.findViewById(R.id.browser_date);
	
		loadRecord(id);
		
	}

	private void easyLoadData(String html){
		Message msg = new Message();
		Bundle bdl = new Bundle();
		bdl.putString("html", html);
		msg.setData(bdl);
		mLoadData.sendMessage(msg);
	}
	private void easyLoadData(String url, String html){
		Message msg = new Message();
		Bundle bdl = new Bundle();
		bdl.putString("url", url);
		bdl.putString("html", html);
		msg.setData(bdl);
		mLoadData.sendMessage(msg);
	}
	private Handler mLoadData = new Handler(){
		public void handleMessage(Message msg){
			Bundle bdl = msg.getData();
			String html = bdl.containsKey("html") ? bdl.getString("html") : "";
			String url = bdl.containsKey("url") ? bdl.getString("url") : "";
			if( url.length() > 0 ){
				mContent.loadDataWithBaseURL(url, html, "text/html", "UTF-8", url);
			}else{
				mContent.loadData(html, "text/html", "UTF-8");
			}
			mContent.setVisibility(View.VISIBLE);
		}
	};/*
	private Handler mHideData = new Handler(){
		public void handleMessage(Message msg){
			Bundle bdl = msg.getData();
			//String html = bdl.containsKey("html") ? bdl.getString("html") : "";
			//mContent.loadData(html, "text/html", "UTF-8");
			mContent.setVisibility(View.INVISIBLE);
		}
	};//*/

    private void loadRecord(long id) {
    	
    	
    	//if( id == mCurrentID ){ return; }
    	
    	mTitle.setText("");
		mDate.setText("");
		easyLoadData("<html></html>");
		//easyLoadData("<html><body bgcolor=#000000 text=#e0e0e0 link=#0066cc vlink=#cc6600><h3><center>Loading</center></h1></body></html>");
    	//mContent.loadData("<html><body bgcolor=#000000 text=#e0e0e0 link=#0066cc vlink=#cc6600><h3><center>Loading</center></h1></body></html>", "text/html", "UTF-8");
    	//mContent.setVisibility(View.INVISIBLE);
    	mCurrentID = id;
		Cursor lCursor = SqliteWrapper.query(this, getContentResolver(), DataProvider.CONTENT_URI, 
        		//new String[] { "_id", "address", "body", "strftime(\"%Y-%m-%d %H:%M:%S\", date, \"unixepoch\", \"localtime\") as date" },
        		//strftime("%Y-%m-%d %H:%M:%S"
        		new String[] {"_id", "title", "link", "datetime(date,'localtime')", "author", "content"  },
				//new String[] { "_id", "address", "body", "date" },
        		"_id = " + id,
        		null, 
        		null);
		
		if( lCursor != null ){
			startManagingCursor(lCursor);
			if ( lCursor.moveToFirst() ){
				String title = null;
				String link = null;
				String date = null;
				String author = null;
				String content = null;
				
				if( lCursor.getColumnCount() == 6 ){/// <<<<<<<<<<<<<<<<<  LOOK HERE
					title = lCursor.getString(1) != null ? lCursor.getString(1) : "";
					link = lCursor.getString(2) != null ? lCursor.getString(2) : "";
					date = lCursor.getString(3) != null ? lCursor.getString(3) : "";
					author = lCursor.getString(4) != null ? lCursor.getString(4) : "";
					content = lCursor.getString(5) != null ? lCursor.getString(5) : "";
					
					if( author.length() == 0 ){
						author = "Author Not Stated";
					}
					mLog.w(TAG,"Found rowid("+id+") title("+title+")");
					mTitle.setText(title);
					mDate.setText(date);

					//mContent.getSettings().supportMultipleWindows();
					mContent.getSettings().setJavaScriptEnabled(true);
					easyLoadData(link + "#contenttopoff", "<html><style>body {font-size: 24px;}</style><body bgcolor=#000000 text=#b0b0b0 link=#0066cc vlink=#cc6600><div style=\"font-size:24px;\"><b>"+title+"</b></div><div style=\"font-size:16px;\">"+date + "</div><div style=\"font-size:16px;\">"+author+"</div>\n<hr noshade><a name=contenttop></a>\n"+content + "<hr noshade>\n<div style=\"font-size:16px;\">" + link + "</div><br></body></html>");//, "text/html", "UTF-8", link);
					mContent.getSettings().setSupportZoom(true);
					
					mLink = link;

					ContentValues cv = new ContentValues();
					cv.put("status", 2);
					SqliteWrapper.update(this, getContentResolver(), DataProvider.CONTENT_URI, cv, "_id = " + id, null);
				}
			}
			//mBrowser.addJavascriptInterface(new AndroidBridge(), "android");
		}
	}


	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		//menu.add(0, 401, 0, "View Article Link")
			//.setIcon(android.R.drawable.ic_menu_view);
        menu.add(0, 402, 0, "Forward")
			.setIcon(android.R.drawable.ic_dialog_email);
		return super.onCreatePanelMenu(featureId, menu);
	}


	@Override
	public View onCreatePanelView(int featureId) {
		// TODO Auto-generated method stub
		return super.onCreatePanelView(featureId);
	}

	


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		mLog.w(TAG,"onMenuItemSelected()");
		return super.onMenuItemSelected(featureId, item);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mLog.w(TAG,"onOptionsItemSelected()");
		
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final String link = mLink; 
		final String title = mTitle.getText().toString(); 
		final String date = mDate.getText().toString(); 

    	
		switch(item.getItemId()){
		case 401:
			Intent d = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			startActivity(d);
			break;
		case 402:
			{
				Intent jump = new Intent(Intent.ACTION_SEND);
				jump.putExtra(Intent.EXTRA_TEXT, "Published "+date + "\nLink\n" +link + "\n\n\n"); 
				jump.putExtra(Intent.EXTRA_SUBJECT, "FW: " + title );
				jump.setType("message/rfc822"); 
				startActivity(Intent.createChooser(jump, "Email"));
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mLog.w(TAG,"onConfigurationChanged() ++++++++++++++++++++++++++++++++");
		super.onConfigurationChanged(newConfig);
	}


	@Override
	protected void onRestart() {
		mLog.w(TAG,"onRestart() ++++++++++++++++++++++++++++++++");
		super.onRestart();
	}


	@Override
	protected void onResume() {
		mLog.w(TAG,"onResume() ++++++++++++++++++++++++++++++++");
		super.onResume();
		//mHideData.sendEmptyMessage(2);
		//easyLoadData("<html><body bgcolor=#000000 text=#e0e0e0 link=#0066cc vlink=#cc6600><h3><center>Loading</center></h1></body></html>");
		
		long id = mSharedPreferences.contains("id") ? mSharedPreferences.getLong("id",0) : 0;
		loadRecord(id);
	}


	@Override
	protected void onStart() {
		mLog.w(TAG,"onStart() ++++++++++++++++++++++++++++++++");
		super.onStart();
	}

	@Override
	protected void onPause() {
		mLog.w(TAG,"onPause() ++++++++++++++++++++++++++++++++");
		easyLoadData("<html></html>");
		//mHideData.sendEmptyMessage(2);
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		mLog.w(TAG,"onStop() ++++++++++++++++++++++++++++++++");
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mLog.w(TAG,"onWindowFocusChanged("+hasFocus+") ++++++++++++++++++++++++++++++++");
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
	}

    
}

