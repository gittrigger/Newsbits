package com.havenskys.whitehouse;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class listView extends ListActivity {

	private static String TAG = "List";
	//private Bundle mIntentExtras;
	private SharedPreferences mSharedPreferences;
	private Editor mPreferencesEditor;
	private long mCurrentID = 0;
	private Custom mLog;
	
	private RelativeLayout childView;
	private TextView cSummary, cTitle, cDate, cAuthor;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		mPreferencesEditor.putLong("id", id); mPreferencesEditor.commit();
		this.setTitle("" + id);
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.listview);
	  mLog = new Custom(this);
	
	  //mIntentExtras = getIntent().getExtras();
	  //long id = mIntentExtras != null ? mIntentExtras.getLong("id") : 0;

	  mSharedPreferences = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
	  mPreferencesEditor = mSharedPreferences.edit();
      long id = mSharedPreferences.contains("id") ? mSharedPreferences.getLong("id",0) : 0;
      //mPreferencesEditor.putLong("id", id); mPreferencesEditor.commit();
		
	  mLog.loadlist(this);
	  loadRecord(id);
	  
	  //mRotateFields.sendEmptyMessageDelayed(1, 1000);
	  
	  this.getListView().setOnHierarchyChangeListener(new OnHierarchyChangeListener(){
		  
		  
		public void onChildViewAdded(View parent, View child) {
			// TODO Auto-generated method stub
			int p = getListView().getPositionForView(child);
			if( p == getListView().getCount() - 1){
				return;
			}
			try{
				childView = (RelativeLayout) child;
			}catch(ClassCastException cce){
				mLog.w(TAG, "Unable to cast child into relative layout, must be header/footer.");
				return;
			}
			cTitle = (TextView) childView.getChildAt(0);
			//String ct = cTitle.getText().toString();
			//cTitle.setText(ct.replaceAll("&#039;", "'"));
			cDate = (TextView) childView.getChildAt(1);
			//cDate.setId(2000 + p);
			cAuthor = (TextView) childView.getChildAt(2);
			//cAuthor.setId(1000 + p);
			//easyRotateFields(childView.getId());
			
			
			//mLog.w(TAG,"("+p+")p cDate Id: " + cDate.getId() );
			//cAuthor.setId(30 + p);
			//mLog.w(TAG,"("+p+")p cAuthor Id: " + cAuthor.getId() );
			//cSummary = (TextView) childView.getChildAt(2);
			//if( cSummary.length() > 0 ){
			//String summ = cSummary.getText().toString();
			
			//String summ2 = summ.replaceAll("<.*?>", "");
			//cSummary.setText("*"+summ2);
			//}
		}

		public void onChildViewRemoved(View parent, View child) {
			// TODO Auto-generated method stub
			
		}
		  
	  });
	
	}
	/*
	private void easyRotateFields(int layoutid){
		Message msg = new Message();
		Bundle bdl = new Bundle();
		bdl.putInt("layoutid", layoutid);
		msg.setData(bdl);
		mRotateFields.sendMessageDelayed(msg, 200);
	}//*/
	private Handler mRotateFields = new Handler(){
		public void handleMessage(Message msg){
			if( !getListView().isShown() ){
				return;
			}
			
			for(int p = getListView().getFirstVisiblePosition(); p < getListView().getLastVisiblePosition(); p ++){
				RelativeLayout child = (RelativeLayout) getListView().getItemAtPosition(p);
				try{
					childView = (RelativeLayout) child;
				}catch(ClassCastException cce){
					mLog.e(TAG, "Unable to cast child into relative layout, must be header/footer.");
					return;
				}
				cDate = (TextView) childView.getChildAt(2);
				cAuthor = (TextView) childView.getChildAt(3);
				if( cDate.getVisibility() == View.VISIBLE ){
					cDate.setVisibility(View.GONE);
					cAuthor.setVisibility(View.VISIBLE);
				}else{
					cAuthor.setVisibility(View.GONE);
					cDate.setVisibility(View.VISIBLE);
				}
			}
		
			mRotateFields.sendEmptyMessageDelayed(2, 3000);
		
		}
	};

	private void loadRecord(long id) {
		if( mCurrentID == id ){return;}
		mCurrentID = id;
		if( id > 0 ){
			int cnt = getListView().getCount();
			int position = 0;
			for( position = cnt; position > 0; position--){
				if( getListView().getItemIdAtPosition(position) == id ){
					break;
				}
			}
			getListView().setSelectionFromTop(position, 1);
			getListView().setSelected(true);
		}
	}


	
	
	@Override
	protected void onResume() {
		mLog.w(TAG,"onResume() ++++++++++++++++++++++++++++++++");
		
		super.onResume();
		long id = mSharedPreferences.contains("id") ? mSharedPreferences.getLong("id",0) : 0;
		loadRecord(id);
		
	}

}
