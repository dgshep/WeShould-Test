package we.should.test;

import we.should.WeShouldActivity;
import android.test.ActivityInstrumentationTestCase2;

public class ListTest extends
		ActivityInstrumentationTestCase2<WeShouldActivity> {

	public ListTest() {
		super("we.should.WeShouldActivity", WeShouldActivity.class);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void setUp(){
		
	}
	public void testTrivial(){
		assertTrue(0==0);
	}
	
}
