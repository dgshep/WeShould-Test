package we.should.test;

import java.util.*;

import we.should.WeShouldActivity;
import we.should.list.*;
import android.test.ActivityInstrumentationTestCase2;

public class ItemTest extends
		ActivityInstrumentationTestCase2<WeShouldActivity> {
	Category C;
	Item it;

	public ItemTest() {
		super("we.should.WeShouldActivity", WeShouldActivity.class);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void setUp(){
		
		Item it = new GenericItem()
	}
	public void testSet(){
		
	}
}