package we.should.test;

import java.io.IOException;
import java.util.*;

import org.json.JSONException;

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import we.should.list.*;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
/**
 * This tests the interaction between the Database and List packages.
 * 
 * TODO:IMPLEMENT only has trivial tests currently
 * @author Davis
 *
 */
public class ListDBIntegrationTests extends
		ActivityInstrumentationTestCase2<WeShouldActivity> {
	WSdb db;
	Category c;

	public ListDBIntegrationTests() {
		super("we.should.WeShouldActivity", WeShouldActivity.class);
		
		// TODO Auto-generated constructor stub
	}
//	@Override
//	protected void setUp(){
//		db = new WSdb(getActivity());
//		db.open();
//		db.rebuildTables();
//		c = new GenericCategory("test", Field.getDefaultFields());
//	}
//	@Override
//	protected void tearDown(){
//		db.close();
//	}
	public void testDbOpen(){
		db = new WSdb(getActivity());
		db.open();
		db.rebuildTables();
		List<Field> dF = Field.getDefaultFields();
		for(int i= 0; i < 100; i++){
			c = new GenericCategory(i+"", dF);
			c.save(getActivity());
		}
		Set<Category> cats = Category.getCategories(getActivity());
		assertEquals(100, cats.size());
		for(int i = 0; i < 100; i++){
			assertTrue(cats.contains(new GenericCategory(i+"", dF)));
		}
		assertTrue(true);
		db.close();
	}
	public void testGetItems(){
		c = new GenericCategory("master", Field.getDefaultFields());
		c.save(getActivity());
		Item i = c.newItem();
		i.set(Field.ADDRESS, "4012 NE 58th");
		i.set(Field.NAME, "BIKE HOUSE");
		i.set(Field.PHONENUMBER, "555-5555");
		i.save(getActivity());
		c = null;
		Set<Category> cats = Category.getCategories(getActivity());
		assertEquals(101, cats.size());
		List<Item> its = null;
		for(Category cat : cats){
			if(cat.getName().equals("master")){
				its = cat.getItems();
				break;
			}
		}
		assertEquals(1, its.size());
		Item it = its.get(0);
		assertEquals("BIKE HOUSE", it.get(Field.NAME));
	}
}