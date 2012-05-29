/**
 * 
 */
package we.should.test;

import java.util.List;

import we.should.list.Field;
import we.should.list.FieldType;
import we.should.list.GenericCategory;
import junit.framework.TestCase;

/**
 * @author Davis
 *
 */
public class FieldTest extends TestCase {
	List<Field> fields;
	/**
	 * 
	 */
	public FieldTest() {
		super();
	}

	/**
	 * @param name
	 */
	public FieldTest(String name) {
		super(name);
		
	}
	public void testEquals(){
		Field test1 = new Field("name", FieldType.MultilineTextField);
		Field test2 = new Field("name", FieldType.MultilineTextField);
		Field test3 = new Field("name", FieldType.MultilineTextField);
		assertEquals(test1, test2);
		assertEquals(test3, test2);
		assertEquals(test1, test3);
		assertEquals(test1, test1);
		assertFalse(test1.equals(null));

	}
	public void testGet(){
		Field test = new Field("Field", FieldType.TextField);
		assertEquals("Field", test.getName());
		assertEquals(FieldType.TextField, test.getType());
	}

}
