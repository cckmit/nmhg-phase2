package tavant.twms.infra;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;

public class GenericsUtilTest extends TestCase {
	private GenericsUtil fixture = new GenericsUtil();
	
	Collection plainCollection;
	Collection<String> strings;
	Set<Money> moneys;
	List<Number> numbers;
	List<List<List<Number>>> listOfListOfListOfNumbers;
	List<List<List<SomeParametricType>>> listOfListOfListOfSomeParametricType;
	
	
	public void testIsCollectionType() throws Exception {
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("plainCollection").getType()));
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("strings").getType()));
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("moneys").getType()));
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("listOfListOfListOfNumbers").getType()));
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("listOfListOfListOfSomeParametricType").getType()));		
		assertFalse(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("fixture").getType()));
	}

	public void testIsParameterizedType() throws Exception{
		assertFalse(fixture.isParameterizedType(GenericsUtilTest.class.getDeclaredField("plainCollection").getGenericType()));
		assertTrue(fixture.isParameterizedType(GenericsUtilTest.class.getDeclaredField("strings").getGenericType()));
		assertTrue(fixture.isParameterizedType(GenericsUtilTest.class.getDeclaredField("moneys").getGenericType()));
		assertTrue(fixture.isParameterizedType(GenericsUtilTest.class.getDeclaredField("listOfListOfListOfNumbers").getGenericType()));
		assertTrue(fixture.isCollectionType(GenericsUtilTest.class.getDeclaredField("listOfListOfListOfSomeParametricType").getType()));		
		assertFalse(fixture.isParameterizedType(GenericsUtilTest.class.getDeclaredField("fixture").getGenericType()));
	}

	public void testGetParameterizedCollectionContentType() throws Exception {
		assertEquals(String.class,fixture.getParameterizedCollectionContentType((ParameterizedType)GenericsUtilTest.class.getDeclaredField("strings").getGenericType()));
		assertEquals(Money.class,fixture.getParameterizedCollectionContentType((ParameterizedType)GenericsUtilTest.class.getDeclaredField("moneys").getGenericType()));
		assertEquals(Number.class,fixture.getParameterizedCollectionContentType((ParameterizedType)GenericsUtilTest.class.getDeclaredField("listOfListOfListOfNumbers").getGenericType()));
		assertEquals(SomeParametricType.class,fixture.getParameterizedCollectionContentType((ParameterizedType)GenericsUtilTest.class.getDeclaredField("listOfListOfListOfSomeParametricType").getGenericType()));
	}

	public static class SomeParametricType<K,V> {
	}
}
