package we.should.test;

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * Test case for WeShould database - WSdb.java
 * 
 * @author  Troy Schuring
 * 			CSE403 SP12
 */
 
public class DBUnitTest extends ActivityInstrumentationTestCase2<WeShouldActivity> {
	
	WSdb db;
	long return_val;
	int id;
	int error;
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
		return_val=db.insertCategory("testCat1", "abc123", "testCat1 schema");			
		c=db.getCategory((int)return_val);
		assertTrue(c.moveToNext());
		c.close();
	}
	
	
	// verify unique name constraint
	public void testInsertDuplicateCategoryFail(){
		int error=0;
		return_val=db.insertCategory("testCat1", "abc123", "testCat1 schema");
		try{
			return_val=db.insertCategory("testCat1", "abc123", "testCat2 schema");
		}catch(SQLiteException ce){
			error++;
		}
		assertEquals(1,error);
		c=db.getAllCategories();
		assertEquals(1,c.getCount());
		c.close();
	}
	

	
	// verify not null,empty string, or space-only string constraints
	public void testInsertCategoryNullAndEmpty(){
		int error=0;
		return_val=0;
		try{
			return_val=db.insertCategory(null, "abc123", "testCat1 schema");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(1,error);
		assertEquals(0,return_val);
		
		error=0;
		try{
			return_val=db.insertCategory("test3", "abc123", null);
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(1,error);
		assertEquals(0,return_val);
		
		//empty or space strings
		error=0;
		try{
			return_val=db.insertCategory("", "abc123", "testCat1 schema");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(1,error);
		assertEquals(0,return_val);
		
		error=0;
		try{
			return_val=db.insertCategory("test3", "abc123", "");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(1,error);
		assertEquals(0,return_val);
	}
	
	
	//***************************************************************
	//		                Insert Item
	//***************************************************************	
	
	// simple item insert
	public void testInsertItem(){
		db.insertCategory("test", "abc123", "schema");
		return_val=db.insertItem("testItem1", 1, "testItem1 data");
		assertEquals(1,return_val);
		c=db.getAllItems();
		assertTrue(c.moveToNext());
		c.close();
		assertTrue(return_val>0);
		return_val=db.insertItem("testItem2", 1, "testItem2 data");
		assertTrue(return_val>0);
		c=db.getAllItems();
		assertTrue(c.moveToNext());
		assertTrue(c.moveToNext());
		c.close();
	}
	
	// test adding an item with a category id that does not exist
	// expected return_val=-1
    public void testInsertItemInvalidCatId() { 
    	error=0;
    	return_val=0;
    	try{
    		return_val=db.insertItem("testItem1", 500, "testItem1 data");
    	}catch(SQLiteConstraintException ec){	
    		error++;
    	}  	
    	assertEquals(1,error);
    	assertEquals(0,return_val);
    }
    
    // test adding an item with duplicate name 
    // expected SQLiteConstraintException
    public void testInsertDuplicateItemName() { 
    	error=0;
    	db.insertCategory("test", "abc123", "schema");
    	return_val=db.insertItem("duplicateTest", 1, "testItem1 data");
    	assertEquals(1,return_val);
    	return_val=0;
    	try{
    		return_val=db.insertItem("duplicateTest", 2, "testItem1 data");
    	}catch(SQLiteConstraintException ce){
			error++;
		}
		assertEquals(1,error);
    	assertEquals(0,return_val);
     }
    
    // verify not null,empty string, or space-only string constraints 	
    public void testInsertItemNullAndEmpty(){
    	db.insertCategory("test", "abc123", "schema");
    	
    	error=0; 
 		return_val=0;
    	try{ 
    		return_val=db.insertItem(null, 1, "testItem1 data");
    	}catch(IllegalArgumentException iae){ 
    		error++; 
    	}
 		assertEquals(1,error); // IAException 
 		assertEquals(0,return_val); // item not inserted
 		
 		error=0; 
 		return_val=0;
    	try{  			
    		return_val=db.insertItem("testItem1", 0, "testItem1 data");
    	}catch(IllegalArgumentException iae){ 
    		error++; 
    	}	
		assertEquals(1,error); // IAException 
		assertEquals(0,return_val); // item not inserted
 		
		error=0; 
 		return_val=0;
    	try{  		
    		return_val=db.insertItem("testItem1", 1, null);
    	}catch(IllegalArgumentException iae){ 
    		error++; 
    	}
    	assertEquals(1,error); // IAException 
    	assertEquals(0,return_val); // item not inserted    	
    	
    	error=0; 
 		return_val=0;
    	try{ 
    		return_val=db.insertItem("", 1, "testItem1 data");
	    }catch(IllegalArgumentException iae){ 
			error++; 
		}
		assertEquals(1,error); // IAException 
		assertEquals(0,return_val); // item not inserted
 		
    	error=0; 
 		return_val=0;
    	try{  		
    		return_val=db.insertItem("testItem1", 1, "");
    	}catch(IllegalArgumentException iae){ 
    		error++; 
    	}
    	assertEquals(1,error); // IAException 
    	assertEquals(0,return_val); // item not inserted
 	}
 	
 	
 
	//***************************************************************
	//		                  Insert Tag
	//***************************************************************
	
	// simple insert of tag
	public void testInsertTag(){
		return_val=db.insertTag("testTag1","abc123");
		c=db.getTag((int)return_val);
		assertTrue(c.moveToNext());
		c.close();
	}
	

	
	// verify not null,empty string, or space-only string constraints	
	public void testInsertTagNullAndEmpty(){
		error=0;
		return_val=0;
		try{
			return_val=db.insertTag(null,"abc123");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(0,return_val);
		assertEquals(1,error);
		
		error=0;
		try{
			return_val=db.insertTag("","abc123");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(0,return_val);
		assertEquals(1,error);
		
		error=0;
		try{
			return_val=db.insertTag("            ","abc123");
		}catch(IllegalArgumentException success){
			error++;
		}
		assertEquals(0,return_val);
		assertEquals(1,error);
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
		c = db.getItemsOfTag(1);
		assertTrue(c.getCount()>0);
		c.close();
	}
	
	// insert item tag that already exists
	public void testInsertItemTagRelationshipFail(){
		error=0;
		return_val=0;
		db.fillTables();
		assertTrue(db.isItemTagged(1,1));
		try{
			return_val=db.insertItem_Tag(1,1);
		}catch(SQLiteConstraintException ce){
			error++;
		}
		assertEquals(0,return_val);
		assertEquals(1, error);
	}

	// test adding an item_tag with tag id that does not exist
    public void testItem_TagFailNoTag() {
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
    	db.insertItem("testItem1", 1, "testItem1 data");
    	c=db.getItem(1);
    	assertTrue(c.moveToNext()); // verify item with id=1 exists
    	c=db.getTag(1);
    	assertFalse(c.moveToNext()); // verify tag with id=1 does not exist
    	c.close();
    	
    	error=0;
    	return_val=0;
    	try{
    		return_val=db.insertItem_Tag(1, 1);
    	}catch(SQLiteConstraintException ce){
    		error++;
    	}
        assertEquals(1,error);
        assertEquals(0,return_val);
    }
    
    // test adding an item_tag with item id that does not exist
    public void testForItem_TagException1() {
    	// verify item with id=1 does not exist
    	c=db.getItem(1);
    	assertEquals(0,c.getCount());
    	assertFalse(c.moveToNext()); 
    	
    	// add & verify tag with id=1 does exist
    	return_val=db.insertTag("testTag1","abc123");
    	c=db.getTag(1);
    	assertTrue(c.moveToNext());
        c.close();
        
        return_val=0;
    	try{
    		return_val=db.insertItem_Tag(1, 1);
    	}catch(SQLiteConstraintException ce){
    		error++;
    	}
        assertEquals(1,error);
        assertEquals(0,return_val);
    }

   
    //***************************************************************
    //				          Query Tests
    //***************************************************************

    // verify proper number of records
 	public void testGetAllCategoriesNumber(){
 		db.insertCategory("testCat1", "abc123", "testCat1 schema");
 		db.insertCategory("testCat2", "abc123", "testCat2 schema");
 		db.insertCategory("testCat3", "abc123", "testCat3 schema");
 		db.insertCategory("testCat4", "abc123", "testCat4 schema");
 		db.insertCategory("testCat5", "abc123", "testCat5 schema");
 		c=db.getAllCategories();
 		assertEquals(5,c.getCount());
 		c.close();
 	}
    
 	// verify proper number of records
  	public void testGetAllItemsNumber(){
  		db.insertCategory("testCat1", "abc123", "testCat1 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 1, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 1, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
  		c=db.getAllItems();
  		assertEquals(5,c.getCount());
  		c.close();
  	}
  	
  	// verify proper number of records
   	public void testGetAllTagsNumber(){
   		db.insertTag("testTag1","abc123");
   		db.insertTag("testTag2","abc123");
   		db.insertTag("testTag3","abc123");
   		db.insertTag("testTag4","abc123");
   		db.insertTag("testTag5","abc123");
   		c=db.getAllTags();
   		assertEquals(5,c.getCount());
   		c.close();
   	}
	
    //verify getItem returns proper item
   	public void testGetItem(){
  		//set values of item to check for
   		String name = "correct name";
  		int catid = 2;
   		String data = "correct data";
   		
   		//insert data into database
   		db.insertCategory("testCat1", "abc123", "testCat1 schema");
  		db.insertCategory("testCat2", "abc123", "testCat2 schema");
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
 		c.close();
   	}
    
   	//verify getCategory returns proper Category
   	public void testGetCategory(){
  		//set values of item to check for
   		String name = "correct name";
  		String color = "abc123";
   		String data = "correct schema";
   		
   		//insert data into database
   		db.insertCategory("testCat1", "abc123", "testCat1 schema");
 		db.insertCategory("testCat2", "abc123", "testCat2 schema");
 		// next category will be the one to check for
 		return_val=db.insertCategory(name, color, data);
 		db.insertCategory("testCat4", "abc123", "testCat4 schema");
 		db.insertCategory("testCat5", "abc123", "test" +
 				"Cat5 schema");
  		
 		c=db.getCategory((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		assertEquals(color,c.getString(2));
 		assertEquals(data,c.getString(3));
 		c.close();
   	}
    
    //verify getTag returns proper tag
   	public void testGetTag(){
  		//set values of item to check for
   		String name = "correct name";
   		
   		db.insertTag("testTag1","abc123");
   		db.insertTag("testTag2","abc123");
   		// look for next one
   		return_val=db.insertTag(name,"abc123");
   		
   		
   		
   		db.insertTag("testTag4","abc123");
   		db.insertTag("testTag5","abc123");
  		
 		c=db.getTag((int)return_val);
 		assertTrue(c.moveToNext());
 		assertEquals(1,c.getCount());
  		assertEquals(return_val, c.getInt(0));
 		assertEquals(name,c.getString(1));
 		c.close();
  	}
    
    
    // test get items of tag - three results
    public void testGetItemsOfTag(){
    	int[] expect = {2,4,5};
    	
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
  		db.insertCategory("testCat2", "abc123", "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
 		db.insertTag("testTag1","abc123");
   		db.insertTag("testTag2","abc123");
   		db.insertTag("testTag3","abc123");
   		db.insertTag("testTag4","abc123");
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
    	c.close();
    }
    
    // test get items of tag - 0 results
    public void testGetItemsOfTagNoResults(){
    	
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
  		db.insertCategory("testCat2", "abc123", "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
 		db.insertTag("testTag1","abc123");
   		db.insertTag("testTag2","abc123");
   		db.insertTag("testTag3","abc123");
   		db.insertTag("testTag4","abc123");
 		db.insertItem_Tag(1,1);
 		db.insertItem_Tag(2,2);
 		db.insertItem_Tag(4,2);
 		db.insertItem_Tag(5,2);
 		db.insertItem_Tag(5,1);
    	
 		c=db.getItemsOfTag(3);
 		assertEquals(0,c.getCount());
    	assertFalse(c.moveToNext());
    	c.close();
    }
    
    public void testGetTagsOfItem(){
    	int[] expect = {1,2,4};
    	
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
  		db.insertCategory("testCat2", "abc123", "testCat2 schema");
 		db.insertItem("testItem1", 1, "testItem data");
 		db.insertItem("testItem2", 2, "testItem data");
 		db.insertItem("testItem3", 1, "testItem data");
 		db.insertItem("testItem4", 2, "testItem data");
 		db.insertItem("testItem5", 1, "testItem data");
 		db.insertTag("testTag1","abc123");
   		db.insertTag("testTag2","abc123");
   		db.insertTag("testTag3","abc123");
   		db.insertTag("testTag4","abc123");
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
    	c.close();
    }
    
    
    //get items of category
    public void testGetItemsOfCategory(){
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
  		db.insertCategory("testCat2", "abc123", "testCat2 schema");
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
    	c.close();
    }
        
  
    //*************************************************************************
    //				Update Tests
    //*************************************************************************
    
    
    // basic category update with no invalid arguments
    public void testUpdateCategory(){
    	String newName="Updated Name";
    	String newSchema="My New Schema";
    	String newColor="123abc"; 
    	db.fillTables();
    	long id=db.insertCategory("update me", "abc123", "My Old Schema");
    	assertTrue(db.updateCategory((int)id,newName,newColor,newSchema));
    	c=db.getCategory((int)id);
    	assertEquals(1,c.getCount());
    	assertTrue(c.moveToNext());
    	assertEquals(newName,c.getString(1));
    	assertEquals(newColor,c.getString(2));
    	assertEquals(newSchema,c.getString(3));
    	c.close();
    }
    
    // update to category name that already exists
    public void testUpdateCategoryDuplicateName(){
    	int exceptionValue=0;  //increment when exception caught then test
    	String oldName="Old Name", newName="Updated Name";
    	String oldSchema="Old Schema", newSchema="My New Schema";
    	String oldColor="aaaaaa", newColor="bbbbbb"; 
    	long id=db.insertCategory(newName, "abc123", "schema");
    	db.fillTables();
    	id=db.insertCategory(oldName, oldColor, oldSchema);
    	try{
    		db.updateCategory((int)id,newName,newColor,newSchema);
    	}catch(SQLiteConstraintException ex){
    		Log.e("testUpdateCategoryDuplicateName", "ex=" + ex.toString());
    		exceptionValue++;
    	}finally{
    		assertEquals(1,exceptionValue);
    		c=db.getCategory((int)id);
    		assertEquals(1,c.getCount());
    		assertTrue(c.moveToNext());
    		assertEquals(oldName,c.getString(1));
    		assertEquals(oldColor,c.getString(2));
    		assertEquals(oldSchema,c.getString(3));
    		c.close();
    	}
    	assertEquals(1,exceptionValue);
    }
    

    
    // basic item update with no invalid arguments
    public void testUpdateItem(){
    	String newName="Updated Name";
    	String newData="My New Data";
    	db.fillTables();
    	long id=db.insertItem("update me", 1, "My Old Data");
    	assertTrue(db.updateItem((int)id,newName,1,newData));
    	c=db.getItem((int)id);
    	assertEquals(1,c.getCount());
    	assertTrue(c.moveToNext());
    	assertEquals(newName,c.getString(1));
    	assertEquals(1,c.getInt(2));
    	assertEquals(newData,c.getString(3));
    	c.close();
    }
    
    // basic tag update with no invalid arguments
    public void testUpdateTag(){
    	String newName="Updated Name";
    	db.fillTables();
    	long id=db.insertTag("update me","abc123");
    	assertTrue(db.updateTag((int)id,newName,"abc123"));
    	c=db.getTag((int)id);
    	assertEquals(1,c.getCount());
    	assertTrue(c.moveToNext());
    	assertEquals(newName,c.getString(1));
    	c.close();
    }
    
  
    
    // update to tag name that already exists
    public void testUpdateTagDuplicateName(){
    	int exceptionValue=0;  //increment when exception caught then test
    	String oldName="Old Name", newName="Updated Name";
    	long id=db.insertTag(newName,"abc123");
    	db.fillTables();
    	id=db.insertTag(oldName,"abc123");
    	try{
    		db.updateTag((int)id,newName,"abc123");
    	}catch(SQLiteConstraintException ex){
    		exceptionValue++;
    	}finally{
    		assertEquals(1,exceptionValue);
    		c=db.getTag((int)id);
    		assertEquals(1,c.getCount());
    		assertTrue(c.moveToNext());
    		assertEquals(oldName,c.getString(1));
    		c.close();
    	}
    	assertEquals(1,exceptionValue);
    }
    
    //*************************************************************************
    //				Delete Tests
    //*************************************************************************


    public void testDeleteCatReinsert(){
    	int error=0;
    	long id=db.insertCategory("testCat1", "abc123", "testCat1 schema");
		assertEquals(1,id);

    	try{
			id=db.insertCategory("testCat1", "abc123", "testCat2 schema");
    	}catch (SQLiteConstraintException ce){
    		error++;
    	}
    	assertEquals(1,error);
    	assertTrue(db.deleteCategory((int)id));
		id = db.insertCategory("testCat1", "abc123", "testCat3 schema");
		assertEquals(2,id);
    }
    
    public void testDeleteItemReinsert(){
    	int error=0;
    	long id=db.insertCategory("testCat1", "abc123", "testCat1 schema");
    	
    	id=0;
    	try{
    		id=db.insertItem("testIt1", 1, "Data");
    	}catch(SQLiteConstraintException ce){
    		error++;
    	}
    	assertEquals(0,error);
    	assertEquals(1,id);
    	int id2=0;
    	try{
    		id=db.insertItem("testIt1", 1, "Different Data");
    	}catch (SQLiteConstraintException ce){
    		error++;
    	}
    	assertEquals(1,error);
    	assertEquals(0,id2);
		assertTrue(db.deleteItem((int)id));
		id=0;
		try{
    		id=db.insertItem("testIt1", 1, "Different Data");
    	}catch (SQLiteConstraintException ce){
    		error++;
    	}
    	assertEquals(1,error);
    	assertEquals(2,id);
    }
    
    public void testDeleteTagReinsert(){
    	String name="TAG__NAME";
    	int id1=(int)db.insertTag(name,"abc123");
    	db.fillTables();
    	c=db.getTag(id1);
    	assertEquals(1,c.getCount());
    	db.deleteTag(id1);
    	c=db.getTag(id1);
    	assertEquals(0,c.getCount());
    	int id2=(int)db.insertTag(name,"abc123");
    	c=db.getTag(id2);
    	assertEquals(1,c.getCount());
    	assertFalse(id1==id2);
    	c.close();
    }
    
    public void testBackup(){
    	db.fillTables();
    	String testString=db.Backup();
    	Log.v("testBackup",testString);
    }
    
    public void testRestore(){
    	db.insertCategory("testCat1", "abc123", "testCat1 schema");
    	db.insertCategory("testCat2", "abc123", "testCat2 schema");
    	db.insertCategory("testCat3", "abc123", "testCat3 schema");
    	db.insertCategory("testCat4", "abc123", "testCat4 schema");
    	db.insertCategory("testCat5", "abc123", "testCat5 schema");
    	db.insertCategory("testCat6", "abc123", "testCat6 schema");
    	db.insertItem("item1", 4, "Data 1");
    	db.insertItem("item2", 6, "Data 2");
    	db.insertItem("item3", 4, "Data 3");
    	db.insertItem("item4", 1, "Data 4");
    	db.insertItem("item5", 6, "Data 5");
    	db.insertTag("tag1","abc123");
    	db.insertTag("tag2","abc123");
    	db.insertTag("tag3","abc123");
    	db.insertTag("tag4","abc123"); 
    	//db.insertItem_Tag(3, 1);
    	//db.insertItem_Tag(3, 4);
    	//db.insertItem_Tag(5, 4);
    	db.deleteTag(2);
    	db.deleteTag(3);
    	db.deleteItem(1);
    	db.deleteItem(2);
    	db.deleteItem(4);
    	db.deleteCategory(1);
    	db.deleteCategory(2);
    	db.deleteCategory(3);
    	db.deleteCategory(5);
    	
    	String testString=db.Backup();
    	Log.v("testRestore",testString);
    	db.Restore(testString);
    }
    
    
    
}
