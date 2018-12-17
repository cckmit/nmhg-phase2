package tavant.twms.domain.claim.payment.definition;

import java.util.Map;

import junit.framework.TestCase;
import tavant.twms.domain.claim.payment.definition.PaymentSection.LevelMap;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;

public class PaymentSectionTest extends TestCase {

	public void testPettyPrintSimple() {
		PaymentSection paymentSection = new PaymentSection();
		paymentSection.setSection(new Section("OEM Parts"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(1, "Discount"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(2, "Freight"));
		
		Map<String, String> values = paymentSection.getPrettyPrintLineItems();
		//assert values now
		assertEquals(3, values.size());
		assertEquals("OEM Parts", values.get("OEM Parts"));
		assertEquals("Discount Modifier * OEM Parts", values.get("Discount"));
		assertEquals("Freight Modifier * ( OEM Parts + Discount )", values.get("Freight"));
	}

    public void testPettyPrintSimpleUnsortedPaymentLevelVariables() {
		PaymentSection paymentSection = new PaymentSection();
		paymentSection.setSection(new Section("OEM Parts"));
        paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(2, "Freight"));
        paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(1, "Discount"));

		Map<String, String> values = paymentSection.getPrettyPrintLineItems();
		//assert values now
		assertEquals(3, values.size());
		assertEquals("OEM Parts", values.get("OEM Parts"));
		assertEquals("Discount Modifier * OEM Parts", values.get("Discount"));
		assertEquals("Freight Modifier * ( OEM Parts + Discount )", values.get("Freight"));
	}

	public void testPettyPrintMultiLevelTest() {
		PaymentSection paymentSection = new PaymentSection();
		paymentSection.setSection(new Section("OEM Parts"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(10, "Level1 Mod1"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(10, "Level1 Mod2"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(10, "Level1 Mod3"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(25, "Level2 Mod1"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(25, "Level2 Mod2"));
		paymentSection.addPaymentVariableLevel(getPaymentVariableLevel(30, "Level3 Mod1"));

		Map<String, String> values = paymentSection.getPrettyPrintLineItems();
		
		assertEquals(7, values.size());
		assertEquals("OEM Parts", values.get("OEM Parts"));
		assertEquals("Level1 Mod1 Modifier * OEM Parts", values.get("Level1 Mod1"));
		assertEquals("Level1 Mod2 Modifier * OEM Parts", values.get("Level1 Mod2"));
		assertEquals("Level1 Mod3 Modifier * OEM Parts", values.get("Level1 Mod3"));
		assertEquals("Level2 Mod1 Modifier * ( OEM Parts + Level1 Mod1 + Level1 Mod2 + Level1 Mod3 )", values.get("Level2 Mod1"));
		assertEquals("Level2 Mod2 Modifier * ( OEM Parts + Level1 Mod1 + Level1 Mod2 + Level1 Mod3 )", values.get("Level2 Mod2"));
		assertEquals("Level3 Mod1 Modifier * ( OEM Parts + Level1 Mod1 + Level1 Mod2 + Level1 Mod3 + Level2 Mod1 + Level2 Mod2 )", values.get("Level3 Mod1"));
	}
	
	public void testLevelMap() {
		LevelMap levelMap = new PaymentSection.LevelMap();
		levelMap.put(1, "One");
		levelMap.put(5, "Five");
		levelMap.put(7, "Seven");
		assertEquals(5, levelMap.getPreviousKey(6).intValue());
		assertEquals("Five", levelMap.getPreviousValue(6));
		assertEquals(5, levelMap.getClosestKeyLessThanOrEqualTo(6).intValue());
		assertEquals(7, levelMap.getClosestKeyLessThanOrEqualTo(7).intValue());
		assertNull(levelMap.getPreviousKey(0));
	}
	
	private PaymentVariableLevel getPaymentVariableLevel(int level, String name) {
		PaymentVariableLevel paymentVariableLevel = new PaymentVariableLevel();
		paymentVariableLevel.setLevel(level);
		PaymentVariable paymentVariable = new PaymentVariable();
		paymentVariable.setName(name);
		paymentVariableLevel.setPaymentVariable(paymentVariable);
		return paymentVariableLevel;
	}
}
