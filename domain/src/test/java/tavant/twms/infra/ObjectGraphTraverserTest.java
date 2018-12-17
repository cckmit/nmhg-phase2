package tavant.twms.infra;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.infra.ObjectGraphTraverser.FieldFilter;
import tavant.twms.infra.ObjectGraphTraverser.FieldOperation;
import tavant.twms.infra.ObjectGraphTraverser.TraversablePathIdentifier;

import com.domainlanguage.money.Money;

public class ObjectGraphTraverserTest extends DomainRepositoryTestCase {
	private ObjectGraphTraverser objectGraphTraverser;
	private DefaultTraversableTypeIdentifier defaultTraversableTypeIdentifier;

	
	
	/**
	 * @param defaultTraversableTypeIdentifier the defaultTraversableTypeIdentifier to set
	 */
	@Required
	public void setDefaultTraversableTypeIdentifier(
			DefaultTraversableTypeIdentifier defaultTraversableTypeIdentifier) {
		this.defaultTraversableTypeIdentifier = defaultTraversableTypeIdentifier;
	}

	/**
	 * @param objectGraphTraverser the objectGraphTraverser to set
	 */
	@Required
	public void setObjectGraphTraverser(ObjectGraphTraverser objectGraphTraverser) {
		this.objectGraphTraverser = objectGraphTraverser;
	}

	public void testTraverse() {
		Money _11 = Money.dollars(11);
		Money _21 = Money.dollars(21);
		TransportInvoice invoice = new TransportInvoice(_11,_21);
		
		Money _41 = Money.dollars(41);
		Money _42 = Money.dollars(42);
		TransportInvoice anotherInvoice = new TransportInvoice(_41,_42);
		invoice.add(anotherInvoice);
		Money _61 = Money.dollars(61);
		anotherInvoice.addMoney(_61);
		
		Money _51 = Money.dollars(51);
		invoice.addMoney(_51);
		
		Money _31 = Money.dollars(31);
		Money _12 = Money.dollars(12);
		Money _22 = Money.dollars(22);
		RailRoadInvoice railRoadInvoice = new RailRoadInvoice(_31,_12,_22,invoice);
		final GenericsUtil genericsUtil = new GenericsUtil();
		final Set<FieldAndOwner> valuesVisited = new LinkedHashSet<FieldAndOwner>();
		objectGraphTraverser.traverse(railRoadInvoice, new FieldFilter(){
			
			public boolean isOfInterest(Field field, Object ofObject) {
				Class<?> fieldType = field.getType();
				boolean moneyType = fieldType.equals(Money.class);
				boolean collectionType = genericsUtil.isCollectionType(fieldType);
				Type genericType = field.getGenericType();
				boolean parameterizedType = genericsUtil.isParameterizedType(genericType);
				return moneyType || ( collectionType && parameterizedType && Money.class.equals(genericsUtil.getParameterizedCollectionContentType((ParameterizedType)genericType)));
			}
			
		}, new FieldOperation(){
			public void doSomething(Field field, Object ofObject) {
				try {
					Object value = field.get(ofObject);
					valuesVisited.add(new FieldAndOwner(value,ofObject));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		},new TraversablePathIdentifier(){
			public boolean isTraversablePath(Object root, String fieldPathFromRoot, Field aField, Object ofObject) {
				boolean traversablePath = defaultTraversableTypeIdentifier.isTraversablePath(root, fieldPathFromRoot, aField, ofObject);
				if( !traversablePath ) {
					Type genericType = aField.getGenericType();
					Class<?> type = aField.getType();
					boolean collectionType = genericsUtil.isCollectionType(type);
					boolean parameterizedType = genericsUtil.isParameterizedType(genericType);
					Class<? extends Object> innerMostElementType = parameterizedType ? genericsUtil.getParameterizedCollectionContentType((ParameterizedType)genericType) : null;
					boolean isMoney = Money.class.equals(innerMostElementType);
					//Check if its a collection of money.
					return collectionType && 
							parameterizedType && 
							isMoney;
				}
				return true;
			}
			
		});
		
		Set<Object> allMoneyFields = new HashSet<Object>();
		Set<Object> allOwners = new HashSet<Object>();
		for(FieldAndOwner each : valuesVisited ) {
			allMoneyFields.add(each.getValue());
			allOwners.add(each.getOwner());
		}
		assertEquals(10,allMoneyFields.size());
		assertTrue(allMoneyFields.contains(_11));
		assertTrue(allMoneyFields.contains(_12));
		assertTrue(allMoneyFields.contains(_21));
		assertTrue(allMoneyFields.contains(_22));
		assertTrue(allMoneyFields.contains(_31));
		assertTrue(allMoneyFields.contains(_41));
		assertTrue(allMoneyFields.contains(_42));
		assertTrue(allMoneyFields.contains(invoice.otherAmounts));
		assertTrue(allMoneyFields.contains(anotherInvoice.otherAmounts));
		assertTrue(allMoneyFields.contains(railRoadInvoice.otherAmounts));
		
		assertEquals(3,allOwners.size());
		assertTrue(allOwners.contains(invoice));
		assertTrue(allOwners.contains(anotherInvoice));
		assertTrue(allOwners.contains(railRoadInvoice));
	}

	public static class FieldAndOwner {
		private Object value;
		private Object owner;
		
		public FieldAndOwner(Object value, Object owner) {
			super();
			this.value = value;
			this.owner = owner;
		}
		/**
		 * @return the owner
		 */
		public Object getOwner() {
			return owner;
		}
		/**
		 * @param owner the owner to set
		 */
		public void setOwner(Object owner) {
			this.owner = owner;
		}
		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(Object value) {
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((owner == null) ? 0 : owner.hashCode());
			result = PRIME * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final FieldAndOwner other = (FieldAndOwner) obj;
			if (owner == null) {
				if (other.owner != null)
					return false;
			} else if (!owner.equals(other.owner))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "("+owner+","+value+")";
		}
	}
	
	public static class Invoice {
		private Money amount;
		
		@SuppressWarnings("unused")
		private static Logger logger = LogManager.getLogger(Invoice.class);
		
		/**
		 * @return the amount
		 */
		public Money getMoney() {
			return amount;
		}

		/**
		 * @param amount the amount to set
		 */
		public void setMoney(Money amount) {
			this.amount = amount;
		}

		public Invoice(Money amount) {
			super();
			this.amount = amount;
		}
		
		
	}
	
	public static class TransportInvoice extends Invoice {
		private Money transportSpecificAmount;
		protected Set<Invoice> someInvoices = new HashSet<Invoice>();
		protected Set<Money> otherAmounts = new HashSet<Money>();
		
		/**
		 * @return the transportSpecificAmount
		 */
		public Money getTransportSpecificAmount() {
			return transportSpecificAmount;
		}

		/**
		 * @param transportSpecificAmount the transportSpecificAmount to set
		 */
		public void setTransportSpecificAmount(Money money1) {
			this.transportSpecificAmount = money1;
		}

		public TransportInvoice(Money amount,Money money1) {
			super(amount);
			this.transportSpecificAmount = money1;
		}
		
		public void add(Invoice rootType) {
			someInvoices.add(rootType);
		}
		
		public void addMoney(Money newMoney) {
			otherAmounts.add(newMoney);
		}
	}
	
	public static class RailRoadInvoice extends TransportInvoice {
		
		private Money railRoadSpecificAmount;
		private Invoice otherInvoice;
		
		/**
		 * @return the railRoadSpecificAmount
		 */
		public Money getRailRoadSpecificAmount() {
			return railRoadSpecificAmount;
		}

		/**
		 * @param railRoadSpecificAmount the railRoadSpecificAmount to set
		 */
		public void setRailRoadSpecificAmount(Money money2) {
			this.railRoadSpecificAmount = money2;
		}

		/**
		 * @return the otherInvoice
		 */
		public Invoice getOtherInvoice() {
			return otherInvoice;
		}

		/**
		 * @param otherInvoice the otherInvoice to set
		 */
		public void setOtherInvoice(Invoice anotherRoot) {
			this.otherInvoice = anotherRoot;
		}

		public RailRoadInvoice(Money amount,Money money1,Money money2, Invoice anotherRoot) {
			super(amount,money1);
			this.railRoadSpecificAmount = money2;
			this.otherInvoice = anotherRoot;
		}

		
		
	}
}
