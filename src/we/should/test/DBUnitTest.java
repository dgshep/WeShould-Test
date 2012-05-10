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
		//db.insertColor("TestColor", "ffffff", "link to drawable");
		return_val=db.insertCategory("testCat1", 1, "testCat1 schema");
		c=db.getCategory((int)return_val);
		assertTrue(c.moveToNext());
	}
	
	
	// verify unique name constraint
	public void testInsertDuplicateCategoryFail(){
		//db.insertColor("TestColor", "ffffff", "link to drawable");
		return_val=db.insertCategory("testCat1", 1, "testCat1 schema");
		return_val=db.insertCategory("testCat1", 1, "testCat2 schema");
		c=db.getAllCategories();
		assertEquals(1,c.getCount());
		assertEquals(-1,return_val);
	}
	
	// verify not null,empty string, or space-only string constraints
	public void testInsertCategoryNullAndEmpty(){
		//db.insertColor("TestColor", "ffffff", "link to drawable");
		//nulls
		return_val=db.insertCategory(null, 1, "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test3", 1, null);
		assertEquals(-1,return_val);
		
		//empty or space strings
		return_val=db.insertCategory("", 1, "testCat1 schema");
		assertEquals(-1,return_val);
		return_val=db.insertCategory("test3", 1, "");
		assertEquals(-1,return_val);
	}
	
	/* verify failed insert with color id that does not exist
	public void testInsertInvalidColorId(){
		db.insertColor("TestColor", "ffffff", "link to drawable");
		return_val=db.insertCategory("name", 2, "testCat1 schema");
		assertEquals(-1,return_val);
	}
	*/
	
	//***************************************************************
	//		                Insert Item
	//***************************************************************	
	
	// simple item insert
	public void testInsertItem(){
		db.insertCategory("test", 1, "schema");
		return_val=db.insertItem("testItem1", 1, "testItem1 data");
		c=db.getAllItems();
		assertTrue(c.moveToNext());
	}
	
	// test adding an item with a category id that does not exist
	// expected return_val=-1
    public void testInsertItemInvalidCatId() { 
    	return_val=db.insertItem("testItem1", 20, "testItem1 data");
    	assertEquals(-1,return_val);
    }
    
    // test adding an item with duplicate name 
    // expected return_val=-1
    public void testInsertDuplicateItemName() { 
    	db.fillTables();
    	return_val=db.insertItem("duplicateTest", 1, "testItem1 data");
    	return_val=db.insertItem("duplicateTest", 2, "testItem1 data");
    	assertEquals(-1,return_val);
     }
    
 // verify not null,empty string, or space-only string constraints 	
    public void testInsertItemNullAndEmpty(){
 		db.insertCategory("test", 1, "schema");
    	return_val=db.insertItem(null, 1, "testItem1 data");
 		assertEquals(-1,return_val);
 		return_val=db.insertItem("testItem1", 0, "testItem1 data");
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("testItem1", 1, null);
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("", 1, "testItem1 data");
 		assertEquals(-1,return_val);
    	return_val=db.insertItem("testItem1", 1, "");
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
		assertFalse(db.isItemTagged(2,1));
		long return_val=db.insertItem_Tag(2,1);
		assertTrue(return_val>0);
		assertTrue(db.isItemTagged(2, 1));
	}
	
	// insert item tag that already exists
	public void testInsertItemTagRelationshipFail(){
		db.fillTables();
		assertTrue(db.isItemTagged(1,1));
		long return_val=db.insertItem_Tag(1,1);
		assertTrue(return_val<0);
		assertTrue(db.isItemTagged(1, 1));
	}

	// test adding an item_tag with tag id that does not exist
    public void testItem_TagFailNoTag() {
    	db.insertCategory("testCat1", 1, "testCat1 schema");
    	db.insertItem("testItem1", 1, "testItem1 data");
    	c=db.getItem(1);
    	assertTrue(c.moveToNext()); // verify item with id=1 exists
    	c=db.getTag(1);
    	assertFalse(c.moveToNext()); // verify tag with id=1 does not exist
    	return_val=db.insertItem_Tag(1, 1);
        assertEquals(-1,return_val);
    }
    
    // test adding an item_tag with item id that does not exist
    public void testForItem_TagException1() {
    	c=db.getItem(5);
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
 		db.insertCategory("testCat1", 1, "testCat1 schema");
 		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertCategory("testCat3", 1, "testCat3 schema");
 		db.insertCategory("testCat4", 1, "testCat4 schema");
 		db.insertCategory("testCat5", 1, "testCat5 schema");
 		c=db.getAllCategories();
 		assertEquals(5,c.getCount());
 	}
    
 	// verify proper number of records
  	public void testGetAllItemsNumber(){
  		db.insertCategory("testCat1", 1, "testCat1 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 1, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 1, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
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
   		String data = "correct data";
   		
   		//insert data into database
   		db.insertCategory("testCat1", 1, "testCat1 schema");
  		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 1, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		// next item will be the one to check for
 		return_val=db.insertItem(name, catid, data);
 		db.insertItem("testItem5", 1, "testItem data");
  		
 		c=db.getItem((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		assertEquals(catid,c.getInt(2));
 		assertEquals(data,c.getString(3));
  	}
    
   	//verify getCategory returns proper Category
   	public void testGetCategory(){
  		//set values of item to check for
   		String name = "correct name";
  		int color = 1;
   		String data = "correct schema";
   		
   		//insert data into database
   		db.insertCategory("testCat1", 1, "testCat1 schema");
 		db.insertCategory("testCat2", 1, "testCat2 schema");
 		// next category will be the one to check for
 		return_val=db.insertCategory(name, color, data);
 		db.insertCategory("testCat4", 1, "testCat4 schema");
 		db.insertCategory("testCat5", 1, "testCat5 schema");
  		
 		c=db.getCategory((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		assertEquals(color,c.getInt(2));
 		assertEquals(data,c.getString(3));
  	}
    
    //verify getTag returns proper tag
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
    	
    	db.insertCategory("testCat1", 1, "testCat1 schema");
  		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
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
    	
    	db.insertCategory("testCat1", 1, "testCat1 schema");
  		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
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
    	
    	db.insertCategory("testCat1", 1, "testCat1 schema");
  		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
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
    	db.insertCategory("testCat1", 1, "testCat1 schema");
  		db.insertCategory("testCat2", 1, "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
 		
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
    
    public void testUpdateCategory(){
    	//TODO
    }
    
    
    public void testUpdateItem(){
    	//TODO
    }
    
    
    public void testUpdateTag(){
    	//TODO
    }
    
    
    //*************************************************************************
    //				Delete Tests
    //*************************************************************************

    public void testDeleteCatReinsert(){
    	return_val=db.insertCategory("testCat1", 1, "testCat1 schema");
    	int id = (int) return_val;
		return_val=db.insertCategory("testCat1", 1, "testCat2 schema");
		assertEquals(-1,return_val);
		assertTrue(id != -1);
		assertTrue(db.deleteCategory(id));
		return_val = db.insertCategory("testCat1", 1, "testCat2 schema");
		assertTrue(return_val != -1);
    }
    
    public void testDeleteItemReinsert(){
    	return_val=db.insertCategory("testCat1", 1, "testCat1 schema");
    	return_val=db.insertItem("testIt1", 1, "Data");
    	assertEquals(return_val,1);
    	int id = (int) return_val;
    	assertEquals(id,1);
		return_val=db.insertItem("testIt1", 1, "Different Data");
		assertEquals(return_val,-1);
		assertEquals(-1,return_val);
		assertTrue(id != -1);
		assertTrue(db.deleteItem(id));
		return_val = db.insertItem("testIt1", 1, "Different Data");
		assertTrue(return_val != -1);
    }
    
    public void testDeleteTagReinsert(){
    	//TODO
    }
    
    
    //TODO
    
    
}
























