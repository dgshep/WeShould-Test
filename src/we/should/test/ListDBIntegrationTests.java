package we.should.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import we.should.PinColor;
import we.should.WeShouldActivity;
import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Field;
import we.should.list.GenericCategory;
import we.should.list.Item;
import we.should.list.Tag;
import android.test.ActivityInstrumentationTestCase2;
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
	}
	
	@Override
	protected void setUp(){
		db = new WSdb(getActivity());
		db.open();
		db.rebuildTables();
		db.close();
		c = new GenericCategory("test", Field.getDefaultFields(), getActivity());
	}
	
	public void testGetCategories(){
		List<Field> dF = Field.getDefaultFields();
		for(int i= 0; i < 10; i++){
			c = new GenericCategory(i+"", dF, getActivity());
			c.save();
		}
		Set<Category> cats = Category.getCategories(getActivity());
		assertEquals(10, cats.size());
		for(int i = 0; i < 10; i++){
			assertTrue(cats.contains(new GenericCategory(i+"", dF, getActivity())));
		}
	}
	
	public void testGetItems(){
		c = new GenericCategory("master", Field.getDefaultFields(), getActivity());
		c.save();
		Item i = c.newItem();
		i.set(Field.ADDRESS, "4012 NE 58th");
		i.set(Field.NAME, "BIKE HOUSE");
		i.set(Field.PHONENUMBER, "555-5555");
		i.save();
		i = c.newItem();
		i.set(Field.ADDRESS, "4014 NE 58th");
		i.set(Field.NAME, "NOT BIKE HOUSE");
		i.set(Field.PHONENUMBER, "1-800-555-5555");
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("TestTag1", PinColor.Blue));
		tags.add(new Tag("TestTag2", PinColor.Purple));
		i.setTags(tags);
		i.save();
		c = null;
		Set<Category> cats = Category.getCategories(getActivity());
		assertEquals(1, cats.size());
		List<Item> its = null;
		for(Category cat : cats){
			if(cat.getName().equals("master")){
				its = cat.getItems();
				assertEquals(its, cat.getItems());
				break;
			}
		}
		assertEquals(2, its.size());
		Item it = its.get(0);
		assertEquals("BIKE HOUSE", it.get(Field.NAME));
		//assertEquals("4012 NE 58th", it.get(Field.ADDRESS));
		assertEquals("555-5555", it.get(Field.PHONENUMBER));
		it = its.get(1);
		assertEquals("NOT BIKE HOUSE", it.get(Field.NAME));
		//assertEquals("4014 NE 58th Ave", it.get(Field.ADDRESS));
		assertEquals("1-800-555-5555", it.get(Field.PHONENUMBER));
		assertEquals(2, it.getTags().size());
	}
	
	
	public void testGetItemsDuplicates(){
		c = new GenericCategory("master1", Field.getMovieFields(), getActivity());
		c.save();
		Item i = c.newItem();
		i.set(Field.NAME, "New BIKE HOUSE");
		i.save();
		i.set(Field.COMMENT, "This is new.");
		i.save();
		c = null;
		Set<Category> cats = Category.getCategories(getActivity());
		assertEquals(1, cats.size());
		List<Item> its = null;
		for(Category cat : cats){
			if(cat.getName().equals("master1")){
				its = cat.getItems();
				assertEquals(its, cat.getItems());
				break;
			}
		}
		assertEquals(1, its.size());
		Item it = its.get(0);
		assertEquals("New BIKE HOUSE", it.getName());
		assertEquals("This is new.", it.getComment());
	}
	public void testGetTagsStatic(){
		c.save();
		Item it = c.newItem();
		it.set(Field.NAME, "test");
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("Tag1", PinColor.DEFAULT));
		tags.add(new Tag("Tag2", PinColor.DEFAULT));
		tags.add(new Tag("Tag3", PinColor.DEFAULT));
		it.setTags(tags);
		it.save();
		List<Tag> allTags = Tag.getTags(getActivity());
		for(Tag t : allTags){
			assertTrue(t.getId()>0);
		}
	}
	public void testGetItemByTagStatic(){
		c.save();
		Item it1 = c.newItem();
		Item it2 = c.newItem();
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("test", PinColor.DEFAULT));
		it1.setTags(tags);
		it1.set(Field.NAME, "Item1");
		tags = new HashSet<Tag>();
		tags.add(new Tag("test", PinColor.DEFAULT));
		it2.setTags(tags);
		it2.set(Field.NAME, "Item2");
		it1.save();
		it2.save();
		List<Tag> allTags = Tag.getTags(getActivity());
		assertEquals(1, allTags.size());
		Set<Item> testItems = Item.getItemsOfTag(allTags.iterator().next(), getActivity());
		assertEquals(2, testItems.size());
		it1.setTags(new HashSet<Tag>());
		it1.save();
		allTags = Tag.getTags(getActivity());
		assertEquals(1, allTags.size());
	}
	public void testGetItemByTagStaticCategories(){
		c.save();
		Item it1 = c.newItem();
		Item it2 = c.newItem();
		Item it3 = c.newItem();
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("test", PinColor.DEFAULT));
		it1.setTags(tags);
		it1.set(Field.NAME, "Item1");
		tags = new HashSet<Tag>();
		tags.add(new Tag("test", PinColor.DEFAULT));
		it2.setTags(tags);
		it2.set(Field.NAME, "Item2");
		it3.set(Field.NAME, "Item3");
		it1.save();
		it2.save();
		it3.save();
		Tag t = it1.getTags().iterator().next();
		Set<Item> testItems = Item.getItemsOfTag(t, getActivity());
		assertEquals(2, testItems.size());
		c = testItems.iterator().next().getCategory();
		List<Item> items = c.getItems();
		assertTrue(items.contains(it1));
		assertTrue(items.contains(it2));
		assertTrue(items.contains(it3));
	}
	public void testDeleteItem(){
		c.save();
		Item it1 = c.newItem();
		it1.set(Field.NAME, "test1");
		it1.save();
		it1 = c.newItem();
		it1.set(Field.NAME, "test2");
		it1.save();
		Set<Category> cats = Category.getCategories(getActivity());
		c = cats.iterator().next();
		assertEquals(2, c.getItems().size());
		c.getItems().get(0).delete();
		assertEquals(1, c.getItems().size());
		cats = Category.getCategories(getActivity());
		c = cats.iterator().next();
		assertEquals(1, c.getItems().size());
		
	}
}