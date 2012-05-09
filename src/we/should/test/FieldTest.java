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
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public FieldTest(String name) {
		super(name);
		
	}
	public void testToDB(){
		assertEquals(Field.ADDRESS.getName() + ":" + 1, Field.ADDRESS.toDB());
		Field test = new Field(Field.ADDRESS.toDB());
		assertEquals(Field.ADDRESS.getName(), test.getName());
		assertEquals(Field.ADDRESS.getType(), test.getType());
	}
	public void testNewField(){
		Field test = new Field("name", FieldType.Rating);
		assertEquals(test.getName(), "name");
		assertEquals(test.getType(), FieldType.Rating);
		Field test2 = new Field(test.toDB());
		assertEquals(test.getName(), test2.getName());
		assertEquals(test.getType(), test2.getType());
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

}
