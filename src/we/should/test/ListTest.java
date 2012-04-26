package we.should.test;

import java.util.*;

import we.should.WeShouldActivity;
import we.should.list.*;
import android.test.ActivityInstrumentationTestCase2;

public class ListTest extends
		ActivityInstrumentationTestCase2<WeShouldActivity> {
	Category C;

	public ListTest() {
		super("we.should.WeShouldActivity", WeShouldActivity.class);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void setUp(){
		Field[] fields = Field.values();
		Set<Field> fieldSet = new HashSet<Field>();
		for(Field f : fields) fieldSet.add(f);
		C = new GenericCategory("Test Category", fieldSet);
	}
	public void testTrivial(){
		assertTrue(C.getItems().size() == 0);
	}
	
}
