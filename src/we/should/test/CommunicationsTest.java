package we.should.test;

import we.should.WeShouldActivity;
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
		
	}
	
	public void testBackupRestore(){
		//TODO: send a string to the db and make sure you get the same one back
	}
	
	public void testRefer(){
		//TODO: send a referral to the database and make sure it's there
	}
    
    
    
}

