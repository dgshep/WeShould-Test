package we.should.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import we.should.ReferDialog;
import we.should.WeShouldActivity;
import we.should.communication.ApproveReferral;
import we.should.communication.BackupService;
import we.should.communication.GetReferralsService;
import we.should.communication.Referral;
import we.should.communication.RestoreService;
import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Field;
import we.should.list.GenericCategory;
import we.should.list.Item;
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
			Log.v("SERVER TEST", s);
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
		BackupService bs = new BackupService();
		String data = "foobar"; //TODO: hardcode a string > 4096 chars
		bs.sendToDb(data, "foo@foo.com"); 
		RestoreService rs = new RestoreService();
		String response = rs.buildDbString("foo@foo.com");
		assertEquals(data, response);
		
		//try it with the actual db backup
		bs.sendToDb(db.Backup(), "bar@bar.com");
		String dbresponse = rs.buildDbString("bar@bar.com");
		assertEquals(db.Backup(), dbresponse);
	}
	
	public void testRefer(){
		//TODO: send a referral to the database and make sure it's there
		Category c = new GenericCategory("master", Field.getDefaultFields(), getActivity());
		c.save();
		Item i = c.newItem();
		i.set(Field.ADDRESS, "4012 NE 58th");
		i.set(Field.NAME, "BIKE HOUSE");
		i.set(Field.PHONENUMBER, "555-5555");
		i.save();
		
		GetReferralsService grs = new GetReferralsService();
		
		ArrayList<Referral> refstodelete = new ArrayList<Referral>();
		refstodelete.add(new Referral(i.getName(), "foo@foo.com", false, null));
		ApproveReferral ar = new ApproveReferral();
		ar.deleteRefs(refstodelete, "bar@bar.com");
		ar.deleteRefs(refstodelete, "baz@baz.com");
		
		JSONArray refs = grs.getFromDb("bar@bar.com");
		assertEquals(0, refs.length());
		
		refs = grs.getFromDb("baz@baz.com");
		assertEquals(0, refs.length());
		
		HttpClient httpclient = new DefaultHttpClient();

		try {
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("user_email", "foo@foo.com"));
		    nameValuePairs.add(new BasicNameValuePair("email_list", "bar@bar.com, baz@baz.com"));
		    

		    nameValuePairs.add(new BasicNameValuePair("item_data", i.dataToDB().toString()));
		    nameValuePairs.add(new BasicNameValuePair("item_name", i.getName()));
		    
		    String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		    
		    HttpGet httpget = new HttpGet("http://23.23.237.174/refer?"+paramString);

		    HttpResponse response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			Log.e("ReferDialog",e.getMessage());
		} catch (IOException e) {
			Log.e("ReferDialog",e.getMessage());
		}
		
		
		
		refs = grs.getFromDb("bar@bar.com");
		assertEquals(1, refs.length());
		
		refs = grs.getFromDb("baz@baz.com");
		assertEquals(1, refs.length());
		
		
		refstodelete = new ArrayList<Referral>();
		refstodelete.add(new Referral(i.getName(), "foo@foo.com", false, null));
		ar.deleteRefs(refstodelete, "bar@bar.com");
		ar.deleteRefs(refstodelete, "baz@baz.com");
		
		refs = grs.getFromDb("bar@bar.com");
		assertEquals(0, refs.length());
		
		refs = grs.getFromDb("baz@baz.com");
		assertEquals(0, refs.length());
	}
    
    
    
}

