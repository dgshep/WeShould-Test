package we.should.test;

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test case for WeShould database - WSdb.java
 * 
 * @author  Troy Schuring
 * 			CSE403 SP12
 */
 
public class DBUnitTest extends ActivityInstrumentationTestCase2<WeShouldActivity> {
	
	WSdb db;
	long return_val;
	Cursor c;
	
	public DBUnitTest() {
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
	
	
	//***************************************************************
	//		                 Open & Close
	//***************************************************************

	// verify open returns writable database
	public void testOpenTwice(){
		assertTrue(db.open());
		db.close();
	}
	
	// verify opening db twice doesn't crash
	public void testOpenWritable(){
		assertTrue(db.open());
		db.open();
		assertTrue(db.isOpen());
		db.close();
	}
	
	// verify database is closed properly
	public void testClose(){
		assertTrue(db.open());
		db.close();
		assertFalse(db.isOpen());
	}
	
	//verify closing a closed db doesn't crash
	public void testCloseTwice(){
		assertTrue(db.open());
		db.close();
		assertFalse(db.isOpen());
		db.close();
		assertFalse(db.isOpen());
	}
	
	
	//***************************************************************
	//		               Insert Category
	//***************************************************************

	
	// simple category insert into empty database
	public void testInsertCategory(){
		return_val=db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
		c=db.getCategory((int)return_val);
		assertTrue(c.moveToNext());
	}
	
	// verify insert with invalid color rgb hex value fails
	public void testInsertCategoryColorFail(){
		// 'g' not hex
		return_val=db.insertCategory("testCat1", "9e9g99", "testCat1 schema");
		assertEquals(-1,return_val);
		
		// length not 6
		return_val=db.insertCategory("testCat1", "9e999", "testCat1 schema");
		assertEquals(-1,return_val);
	}
	
	// verify unique name constraint
	public void testInsertDuplicateCategoryFail(){
		return_val=db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
		return_val=db.insertCategory("testCat1", "888888", "testCat2 schema");
		c=db.getAllCategories();
		assertEquals(1,c.getCount());
		assertEquals(-1,return_val);
	}
	
	// verify not null,empty string, or space-only string constraints
	public void testInsertCategoryNullAndEmpty(){
		
		//nulls
		return_val=db.insertCategory(null, "9e9f99", "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test2", null, "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test3", "9e9f99", null);
		assertEquals(-1,return_val);
		
		//empty or space strings
		return_val=db.insertCategory("", "9e9f99", "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test2", "    ", "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test3", "9e9f99", "");
		assertEquals(-1,return_val);
	}
	
	
	
	//***************************************************************
	//		                Insert Item
	//***************************************************************	
	
	// simple item insert
	public void testInsertItem(){
		db.insertCategory("test", "aaaaaa", "schema");
		return_val=db.insertItem("testItem1", 1, true, "testItem1 data");
		c=db.getAllItems();
		assertTrue(c.moveToNext());
	}
	
	// test adding an item with a category id that does not exist
	// expected return_val=-1
    public void testInsertItemInvalidCatId() { 
    	return_val=db.insertItem("testItem1", 20, false, "testItem1 data");
    	assertEquals(-1,return_val);
    }
    
    // test adding an item with duplicate name 
    // expected return_val=-1
    public void testInsertDuplicateItemName() { 
    	db.fillTables();
    	return_val=db.insertItem("duplicateTest", 1, true, "testItem1 data");
    	return_val=db.insertItem("duplicateTest", 2, false, "testItem1 data");
    	assertEquals(-1,return_val);
     }
    
 // verify not null,empty string, or space-only string constraints 	
    public void testInsertItemNullAndEmpty(){
 		db.insertCategory("test", "aaaaaa", "schema");
    	return_val=db.insertItem(null, 1, true, "testItem1 data");
 		assertEquals(-1,return_val);
 		return_val=db.insertItem("testItem1", 0, true, "testItem1 data");
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("testItem1", 1, true, null);
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("", 1, true, "testItem1 data");
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("testItem1", 1, true, "");
 		assertEquals(-1,return_val);
 	}
 	
 	
 
	//***************************************************************
	//		                  Insert Tag
	//***************************************************************
	
	// simple insert of tag
	public void testInsertTag(){
		return_val=db.insertTag("testTag1");
		c=db.getTag((int)return_val);
		assertTrue(c.moveToNext());
	}
	
	// verify not null,empty string, or space-only string constraints	
	public void testInsertTagNullAndEmpty(){
		return_val=db.insertTag(null);
		assertEquals(-1,return_val);
		return_val=db.insertTag("");
		assertEquals(-1,return_val);
		return_val=db.insertTag("            ");
		assertEquals(-1,return_val);
	}

	
	//***************************************************************
	//		          Insert Item-Tag Relationship
	//***************************************************************    
   
	// simple insert into item_tag table
	public void testInsertItemTagRelationship(){
		db.fillTables();
		long return_val=db.insertItem_Tag(1,1);
		assertTrue(return_val>0);
	}

	// test adding an item_tag with tag id that does not exist
    public void testItem_TagFailNoTag() {
    	db.insertCategory("testCat1", "999999", "testCat1 schema");
    	db.insertItem("testItem1", 1, true, "testItem1 data");
    	Cursor c=db.getItem(1);
    	assertTrue(c.moveToNext()); // verify item with id=1 exists
    	c=db.getTag(1);
    	assertFalse(c.moveToNext()); // verify tag with id=1 does not exist
    	return_val=db.insertItem_Tag(1, 1);
        assertEquals(-1,return_val);
    }
    
    // test adding an item_tag with item id that does not exist
    public void testForItem_TagException1() {
    	Cursor c=db.getItem(5);
    	assertEquals(0,c.getCount());
    	assertFalse(c.moveToNext()); // verify item with id=5 does not exist
    	return_val=db.insertTag("testTag1");
    	c=db.getTag(1);
    	assertTrue(c.moveToNext()); // verify tag with id=1 does exist
    	return_val=db.insertItem_Tag(5, 1);
        assertEquals(-1,return_val);
    }

    
    //***************************************************************
    //				          Query Tests
    //***************************************************************

    // verify proper number of records
 	public void testGetAllCategoriesNumber(){
 		db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
 		db.insertCategory("testCat2", "888888", "testCat2 schema");
 		db.insertCategory("testCat3", "888888", "testCat3 schema");
 		db.insertCategory("testCat4", "888888", "testCat4 schema");
 		db.insertCategory("testCat5", "888888", "testCat5 schema");
 		c=db.getAllCategories();
 		assertEquals(5,c.getCount());
 	}
    
 	// verify proper number of records
  	public void testGetAllItemsNumber(){
  		db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 1, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		db.insertItem("testItem4", 1, true, "testItem data");
 		db.insertItem("testItem5", 1, true, "testItem data");
  		c=db.getAllItems();
  		assertEquals(5,c.getCount());
  	}
  	
  	// verify proper number of records
   	public void testGetAllTagsNumber(){
   		db.insertTag("testTag1");
   		db.insertTag("testTag2");
   		db.insertTag("testTag3");
   		db.insertTag("testTag4");
   		db.insertTag("testTag5");
   		c=db.getAllTags();
   		assertEquals(5,c.getCount());
   	}
	
    //verify getItem returns proper item
   	public void testGetItem(){
  		//set values of item to check for
   		String name = "correct name";
  		int catid = 2;
  		boolean map = false;
   		String data = "correct data";
   		
   		//insert data into database
   		db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
  		db.insertCategory("testCat2", "9e9f99", "testCat2 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 1, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		// next item will be the one to check for
 		return_val=db.insertItem(name, catid, false, data);
 		db.insertItem("testItem5", 1, true, "testItem data");
  		
 		c=db.getItem((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		assertEquals(catid,c.getInt(2));
 		assertEquals(0,c.getInt(3)); // bools are 0 or 1
 		assertEquals(data,c.getString(4));
  	}
    
   	//verify getItem returns proper Category
   	public void testGetCategory(){
  		//set values of item to check for
   		String name = "correct name";
  		String color = "ff34Db";
   		String data = "correct schema";
   		
   		//insert data into database
   		db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
 		db.insertCategory("testCat2", "888888", "testCat2 schema");
 			// next item will be the one to check for
 		return_val=db.insertCategory(name, color, data);
 		db.insertCategory("testCat4", "888888", "testCat4 schema");
 		db.insertCategory("testCat5", "888888", "testCat5 schema");
  		
 		c=db.getCategory((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		assertEquals(color,c.getString(2));
 		assertEquals(data,c.getString(3));
  	}
    
    //verify getItem returns proper Category
   	public void testGetTag(){
  		//set values of item to check for
   		String name = "correct name";
   		
   		db.insertTag("testTag1");
   		db.insertTag("testTag2");
   		// look for next one
   		return_val=db.insertTag(name);
   		db.insertTag("testTag4");
   		db.insertTag("testTag5");
  		
 		c=db.getTag((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
  	}
    
    
    // test get items of tag - three results
    public void testGetItemsOfTag(){
    	int[] expect = {2,4,5};
    	
    	db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
  		db.insertCategory("testCat2", "9e9f99", "testCat2 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 2, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		db.insertItem("testItem4", 2, true, "testItem data");
 		db.insertItem("testItem5", 1, true, "testItem data");
 		db.insertTag("testTag1");
   		db.insertTag("testTag2");
   		db.insertTag("testTag3");
   		db.insertTag("testTag4");
 		db.insertItem_Tag(1,1);
 		db.insertItem_Tag(2,2);
 		db.insertItem_Tag(4,2);
 		db.insertItem_Tag(5,2);
 		db.insertItem_Tag(5,1);
    	
 		c=db.getItemsOfTag(2);
 		assertEquals(3,c.getCount());
    	c.moveToNext();
    	assertEquals(expect[0],c.getInt(0));
    	c.moveToNext();
    	assertEquals(expect[1],c.getInt(0));
    	c.moveToNext();
    	assertEquals(expect[2],c.getInt(0));
    }
    
    // test get items of tag - 0 results
    public void testGetItemsOfTagNoResults(){
    	int[] expect = {2,4,5};
    	
    	db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
  		db.insertCategory("testCat2", "9e9f99", "testCat2 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 2, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		db.insertItem("testItem4", 2, true, "testItem data");
 		db.insertItem("testItem5", 1, true, "testItem data");
 		db.insertTag("testTag1");
   		db.insertTag("testTag2");
   		db.insertTag("testTag3");
   		db.insertTag("testTag4");
 		db.insertItem_Tag(1,1);
 		db.insertItem_Tag(2,2);
 		db.insertItem_Tag(4,2);
 		db.insertItem_Tag(5,2);
 		db.insertItem_Tag(5,1);
    	
 		c=db.getItemsOfTag(3);
 		assertEquals(0,c.getCount());
    	assertFalse(c.moveToNext());
    }
    
    public void testGetTagsOfItem(){
    	int[] expect = {4,2,1};
    	
    	db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
  		db.insertCategory("testCat2", "9e9f99", "testCat2 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 2, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		db.insertItem("testItem4", 2, true, "testItem data");
 		db.insertItem("testItem5", 1, true, "testItem data");
 		db.insertTag("testTag1");
   		db.insertTag("testTag2");
   		db.insertTag("testTag3");
   		db.insertTag("testTag4");
 		db.insertItem_Tag(1,1);
 		db.insertItem_Tag(5,4);
 		db.insertItem_Tag(4,2);
 		db.insertItem_Tag(5,2);
 		db.insertItem_Tag(5,1);
    	
 		c=db.getTagsOfItem(5);
 		assertEquals(3,c.getCount());
    	c.moveToNext();
    	assertEquals(expect[0],c.getInt(0));
    	c.moveToNext();
    	assertEquals(expect[1],c.getInt(0));
    	c.moveToNext();
    	assertEquals(expect[2],c.getInt(0));
    }
    
    
    //get items of category
    public void testGetItemsOfCategory(){
    	db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
  		db.insertCategory("testCat2", "9e9f99", "testCat2 schema");
 		db.insertItem("testItem1", 1, true, "testItem data");
 		db.insertItem("testItem2", 2, true, "testItem data");
 		db.insertItem("testItem3", 1, true, "testItem data");
 		db.insertItem("testItem4", 2, true, "testItem data");
 		db.insertItem("testItem5", 1, true, "testItem data");
 		
 		c=db.getItemsOfCategory(1);
 		assertEquals(3,c.getCount());
    	c.moveToNext();
    	assertEquals(1,c.getInt(0));
    	c.moveToNext();
    	assertEquals(3,c.getInt(0));
    	c.moveToNext();
    	assertEquals(5,c.getInt(0));
    }
    
    
    
  
    //*************************************************************************
    //				Update Tests
    //*************************************************************************
    
    
    int affected;
    
    
    //simple update
    public void testUpdateColor(){
    	String color="abdcef";
    	String newColor="012345";
    	
    	return_val=db.insertCategory("testCat1", color, "testCat1 schema");
    	
    	affected=db.updateCategoryColor(1, newColor);
    	assertEquals(1,affected);
    	
    	c=db.getCategory((int)return_val);
    	c.moveToNext();
    	assertEquals(newColor,c.getString(2));
    }
    
    //simple update
    public void testUpdateSameColor(){
    	String color="abdcef";
    	return_val=db.insertCategory("testCat1", color, "testCat1 schema");
    	affected=db.updateCategoryColor((int)return_val, color);
    	assertEquals(1,affected);
    	c=db.getCategory((int)return_val);
    	c.moveToNext();
    	assertEquals(color,c.getString(2));
    }
    
    public void testUpdateCategoryName(){
    	String name = "name";
    	String newname="newname";
    	return_val=db.insertCategory(name, "abcdef", "testCat1 schema");
    	affected=db.updateCategoryName((int)return_val, newname);
    	
    	assertEquals(1,affected);
    	c=db.getCategory((int)return_val);
    	c.moveToNext();
    	assertEquals(newname,c.getString(1));
    }
    
    public void testUpdateTagName(){
    	String name = "name";
    	String newname="newname";
    	return_val=db.insertTag(name);
    	affected=db.updateTagName((int)return_val, newname);
    	
    	assertEquals(1,affected);
    	c=db.getTag((int)return_val);
    	c.moveToNext();
    	assertEquals(newname,c.getString(1));
    }
    
    public void testUpdateItemName(){
    	String name = "name";
    	String newname="newname";
    	db.insertCategory(name, "abcdef", "testCat1 schema");
    	return_val=db.insertItem(name,1,true,"data");
    	affected=db.updateItemName((int)return_val, newname);
    	
    	assertEquals(1,affected);
    	c=db.getItem((int)return_val);
    	c.moveToNext();
    	assertEquals(newname,c.getString(1));
    }
    
    //*************************************************************************
    //				Delete Tests
    //*************************************************************************

    public void testDeleteCatReinsert(){
    	return_val=db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
    	int id = (int) return_val;
		return_val=db.insertCategory("testCat1", "888888", "testCat2 schema");
		assertEquals(-1,return_val);
		assertTrue(id != -1);
		assertTrue(db.deleteCategory(id));
		return_val = db.insertCategory("testCat1", "888888", "testCat2 schema");
		assertTrue(return_val != -1);
    }
    public void testDeleteItemReinsert(){
    	return_val=db.insertCategory("testCat1", "9e9f99", "testCat1 schema");
    	return_val=db.insertItem("testIt1", 1, false, "Data");
    	int id = (int) return_val;
		return_val=db.insertItem("testIt1", 1, false, "Different Data");
		assertEquals(-1,return_val);
		assertTrue(id != -1);
		assertTrue(db.deleteItem(id));
		return_val = db.insertItem("testIt1", 1, false, "Different Data");
		assertTrue(return_val != -1);
    }
    //TODO
    
    
}
