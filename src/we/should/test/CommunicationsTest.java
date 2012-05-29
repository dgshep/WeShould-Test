package we.should.test;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import we.should.ReferDialog;
import we.should.WeShouldActivity;
import we.should.communication.ApproveReferral;
import we.should.communication.BackupService;
import we.should.communication.GetReferralsService;
import we.should.communication.RestoreService;
import we.should.database.WSdb;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * Test case for Communications Package
 * 
 * @author  Colleen Ross
 * 			CSE403 SP12
 */
 
public class CommunicationsTest extends ActivityInstrumentationTestCase2<WeShouldActivity> {
	
	WSdb db;
	long return_val;
	int id;
	int error;
	Cursor c;
	
	public CommunicationsTest() {
		super("we.should", WeShouldActivity.class);
	}

	@Override // Run before each test
	public void setUp(){
		db = new WSdb(getActivity());
		db.open();
		db.rebuildTables();
	}

	@Override  // Run after each test
	public void tearDown() throws Exception {
		if(db.isOpen()) db.close();
	} 

	@Override 
	protected void runTest() throws Throwable {
		super.runTest(); 
		getActivity().finish(); 
	} 
	
	public void testServerCommunication(){
		//TODO: test all urls to make sure they all come back 200s
		ArrayList<String> urls = new ArrayList<String>();
		urls.add(ApproveReferral.getUrl());
		urls.add(GetReferralsService.getUrl());
		urls.add(BackupService.getUrl());
		urls.add(RestoreService.getUrl());
		urls.add(ReferDialog.getUrl());
		
		for(String s: urls){
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("http://23.23.237.174/"+s);
			try {
				HttpResponse response = httpclient.execute(httpget);
				int statusCode = response.getStatusLine().getStatusCode();
				assertEquals(statusCode, 200);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void testBackupRestore(){
		//TODO: send a string to the db and make sure you get the same one back
	}
	
	public void testRefer(){
		//TODO: send a referral to the database and make sure it's there
	}
    
    
    
}

