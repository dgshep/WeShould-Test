package we.should.test;

import java.util.*;

import junit.framework.TestCase;

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import we.should.list.*;
import android.location.Address;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class ListTest extends ActivityInstrumentationTestCase2<WeShouldActivity>  {
	Category C;
	Item it;
	List<Field> fieldSet;
	WSdb db;

	public ListTest() {
		super("we.should.WeShouldActivity", WeShouldActivity.class);
		
	}
	@Override
	protected void setUp(){
		List<Field> fields = Field.getDefaultFields();
		//Log.v("Set Up List Test", fields.toString());
		fields.remove(Field.COMMENT);
		C = new GenericCategory("Test NO COMMENT FIELD", fields);
		db = new WSdb(getActivity());
		db.open();
		db.rebuildTables();
		db.close();
		C.save(getActivity());
		it = C.newItem();
	}
	public void testItems(){
		assertTrue(C.getItems().size() == 0);
		it.save(null);
		assertTrue(C.getItems().size() == 1);
		it.save(null);
		assertTrue(C.getItems().size() == 1);
		it.delete();
		assertTrue(C.getItems().size() == 0);
	}
	public void testItemGetSetWBadArgument(){
		try {
			it.get(Field.COMMENT);
			fail("Should Throw illegal argument exception");
		} catch(IllegalArgumentException success) {
			
		}
		try {
			it.set(Field.COMMENT, "This should not be added.");
			fail("Should Throw illegal argument exception");
		}catch(IllegalArgumentException success) {
			
		}
	}
	public void testItemSetGetWGoodArgument(){
		String testAd = "test address";
		String testAd1 = "another Test Address";
		it.set(Field.ADDRESS, testAd);
		assertTrue(it.get(Field.ADDRESS).equals(testAd));
		assertTrue(it.get(Field.ADDRESS).equals(testAd));
		it.set(Field.ADDRESS, testAd1);
		assertTrue(it.get(Field.ADDRESS).equals(testAd1));
		assertTrue(it.get(Field.ADDRESS).equals(testAd1));
		assertFalse(it.get(Field.ADDRESS).equals(testAd));
	}
	public void testGetItems(){
		List<Item> items = C.getItems();
		List<Item> testItems = new LinkedList<Item>();
		assertEquals(items.size(), 0);
		assertEquals(items, testItems);
		it.set(Field.NAME, "Test1");
		it.save(null);
		testItems.add(it);
		items = C.getItems();
		assertEquals(1, items.size());
		assertEquals(items, testItems);
		Item it1 = C.newItem();
		it1.set(Field.NAME, "Test2");
		it1.save(null);
		testItems.add(it1);
		items = C.getItems();
		assertEquals(2, items.size());
		assertEquals(items, testItems);
		Item it2 = C.newItem();
		it2.set(Field.NAME, "Test3");
		it2.save(null);
		testItems.add(it2);
		items = C.getItems();
		assertEquals(3, items.size());
		assertEquals(items, testItems);
		
	}
	public void testGetFields(){
		List<Field> fields = C.getFields();
		List<Field> testFields = Field.getDefaultFields();
		testFields.remove(Field.COMMENT);
		assertEquals(testFields, fields);
	}
	public void testGetComment(){
		Category Ctest = new GenericCategory("Default", Field.getDefaultFields());
		Item testItem = Ctest.newItem();
		testItem.set(Field.COMMENT, "test Comment");
		assertEquals("test Comment", testItem.getComment());
	}
	public void testGetPhoneNo(){
		it.set(Field.PHONENUMBER, "testNumber");
		assertEquals("testNumber", it.getPhoneNo());
	}
	public void testGetName(){
		it.set(Field.NAME, "testName");
		assertEquals("testName", it.getName());
	}
	public void testEquals(){
		it.set(Field.NAME, "testName");
		Item it2 = C.newItem();
		it2.set(Field.NAME, "testName");
		assertTrue(it2.equals(it));
		assertTrue(it.equals(it));
	}
	public void testGetAddresses(){
		it.set(Field.ADDRESS, "4012 NE 58th St, 98105");
		Set<Address> add = it.getAddresses(getActivity());
		for(Address a : add){
			assertEquals(47.671645, a.getLatitude(), .001);
			assertEquals(-122.284233, a.getLongitude(), .001);
		}
	}
	
}
