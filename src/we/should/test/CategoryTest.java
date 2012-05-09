package we.should.test;

import we.should.list.Category;
import we.should.list.Field;
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
		Category c = new GenericCategory("Blue", Field.getDefaultFields());
		assertTrue(c.getColor().equals(c.DEFAULT_COLOR));
		c.setColor("FFFFF0");
		assertTrue(c.getColor().equals("FFFFF0"));
		try{
			c.setColor("GZ");
			fail("Should through iae");
		} catch(IllegalArgumentException success){
			
		}
	}
}
