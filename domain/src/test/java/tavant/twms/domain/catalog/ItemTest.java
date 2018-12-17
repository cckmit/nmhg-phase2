package tavant.twms.domain.catalog;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

	public void testIncludeAndIncludes() {
		Item car = new Item();
		Item engine = new Item();
		Item transmissionSystem = new Item();
		Item steering = new Item();
		Item carbuerator = new Item();
		Item valve = new Item();

		car.include(1, engine);
		car.include(1, transmissionSystem);
		car.include(1, steering);
		engine.include(1, carbuerator);
		carbuerator.include(1, valve);

		assertTrue(car.includes(engine));
		assertTrue(car.includes(carbuerator));
		assertTrue(car.includes(valve));
		assertTrue(car.includes(steering));
		assertFalse(steering.includes(engine));
	}

}
