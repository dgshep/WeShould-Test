package we.should.test;

import java.util.LinkedList;
import java.util.List;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.FieldType;
import we.should.list.GenericCategory;
import junit.framework.TestCase;

public class CategoryTest extends TestCase {
	
	public CategoryTest(){
		super();
	}
	
	@Override
	public void setUp(){
		
	}
	
	public void testSetColor(){
		Category c = new GenericCategory("Test", Field.getDefaultFields(), null);
		assertTrue(c.getColor().equals(c.DEFAULT_COLOR));
		c.setColor("FFFFF0");
		assertTrue(c.getColor().equals("FFFFF0"));
		try{
			c.setColor("GZ");
			fail("Should throw iae");
		} catch(IllegalArgumentException success){
			
		}
	}
	public void testConstructor(){
		Category c = new GenericCategory("Test", new LinkedList<Field>(), null);
		assertEquals("Test", c.getName());
	}
	public void testEquals(){
		Category c1 = new GenericCategory("c1", Field.getDefaultFields(), null);
		Category c2 = new GenericCategory("c1", Field.getDefaultFields(), null);
		Category c3 = new GenericCategory("c2", Field.getDefaultFields(), null);
		Field f = new Field("new", FieldType.MultilineTextField);
		List<Field> newFields = new LinkedList<Field>();
		newFields.add(f);
		Category c4 = new GenericCategory("c1", newFields, null);
		assertEquals(c1, c2);
		assertEquals(c1, c1);
		assertFalse(c1.equals(c3));
		assertFalse(c1.equals(c4));
	}
	
}
