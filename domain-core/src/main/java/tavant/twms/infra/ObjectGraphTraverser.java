/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.infra;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @author radhakrishnan.j
 */
@SuppressWarnings( { "unchecked", "serial" })
public class ObjectGraphTraverser {
	private Map<Class<? extends Object>, Set<Field>> fieldsInType = new HashMap<Class<? extends Object>, Set<Field>>() {

		@Override
		public Set<Field> get(Object key) {
			Set<Field> set = super.get(key);
			if (set == null) {
				set = new HashSet<Field>();
				super.put((Class<? extends Object>) key, set);
			}
			return set;
		}

	};

	private Set<Class> alreadyCached = new HashSet<Class>();

	private TraversablePathIdentifier defaultTraversablePathIdentifier;

	private TraversableTypeIdentifier defaultTraversableTypeIdentifier;
	
	public interface FieldFilter {
		public boolean isOfInterest(Field field, Object ofObject);
	}

	public interface FieldOperation {
		public void doSomething(Field field, Object ofObject);
	}

	public interface TraversableTypeIdentifier {
		public boolean isTraversable(Class<? extends Object> type);
	}

	public interface TraversablePathIdentifier {
		public boolean isTraversablePath(Object root, String fieldPathFromRoot,
				Field field, Object ofObject);
	}

	@SuppressWarnings("unchecked")
	public void traverse(Object object, FieldFilter fieldFilter,
			FieldOperation fieldOperation) {
		Assert.notNull(fieldFilter, "A field filter needs to be specified");
		Assert.notNull(fieldOperation,
				"A field operation needs to be specified");
		Map<Integer,Object> visitedNodes =  new HashMap<Integer,Object>();
		traverseGraph(object, "", object, defaultTraversableTypeIdentifier,
				defaultTraversablePathIdentifier, fieldFilter, fieldOperation,
				visitedNodes);
	}

	@SuppressWarnings("unchecked")
	public void traverse(Object object, FieldFilter fieldFilter,
			FieldOperation fieldOperation,
			TraversablePathIdentifier traversablePathIdentifier) {
		Assert.notNull(fieldFilter, "A field filter needs to be specified");
		Assert.notNull(fieldOperation,
				"A field operation needs to be specified");
		Assert.notNull(traversablePathIdentifier,
				"A traversable path identifier needs to specified.");
		Map<Integer,Object> visitedNodes =  new HashMap<Integer,Object>();
		traverseGraph(object, "", object, defaultTraversableTypeIdentifier,
				traversablePathIdentifier, fieldFilter, fieldOperation,
				visitedNodes);
	}

	void traverseGraph(Object rootObject, String pathFromRoot, Object ofObject,
			TraversableTypeIdentifier traversableTypeIdentifier,
			TraversablePathIdentifier traversalHelper, FieldFilter fieldFilter,
			FieldOperation fieldOperation, Map<Integer,Object> visitedNodes) {
		if (visitedNodes.containsKey(System.identityHashCode(ofObject))) {
			return;
		} else {
			visitedNodes.put(System.identityHashCode(ofObject),ofObject);
		}
		final Class<? extends Object> type = ofObject.getClass();

		if (Collection.class.isAssignableFrom(type)) {
			Collection collection = (Collection) ofObject;
			for (Object each : collection) {
				traverseGraph(rootObject, pathFromRoot, each,
						traversableTypeIdentifier, traversalHelper,
						fieldFilter, fieldOperation, visitedNodes);
			}
		} else {

			if (!alreadyCached.contains(type)) {
				alreadyCached.add(type);
				ReflectionUtils.doWithFields(type, new ReflectionUtils.FieldCallback() {
					public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
						boolean ignore = field.isEnumConstant() || field.isSynthetic() || Modifier.isStatic(field.getModifiers());
						if( !ignore ) {
							fieldsInType.get(type).add(field);
						}
					}
				});
			}

			Set<Field> allFields = fieldsInType.get(type);
			try {
				for (Field aField : allFields) {
					ReflectionUtils.makeAccessible(aField);
					Object value = aField.get(ofObject);

					if (fieldFilter.isOfInterest(aField, ofObject)) {
						fieldOperation.doSomething(aField, ofObject);
					}

					if (value != null
							&& traversalHelper.isTraversablePath(rootObject,
									pathFromRoot, aField, ofObject)) {
						if (value instanceof Collection) {
							Collection collection = (Collection) value;
							for (Object entry : collection) {
								if ( entry != null ) {
									traverseGraph(rootObject, pathFromRoot,
											entry, traversableTypeIdentifier,
											traversalHelper, fieldFilter,
											fieldOperation, visitedNodes);
								}
							}
						} else {
							traverseGraph(rootObject, pathFromRoot, value,
									traversableTypeIdentifier, traversalHelper,
									fieldFilter, fieldOperation, visitedNodes);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ObjectGraphTraverser() {
		super();
	}

	Map<Class<? extends Object>, Set<Field>> getFieldsInType() {
		return fieldsInType;
	}

	/**
	 * @param defaultTraversableTypeIdentifier
	 *            the defaultTraversableTypeIdentifier to set
	 */
	@Required
	public void setDefaultTraversablePathIdentifier(
			TraversablePathIdentifier defaultTraversablePathIdentifier) {
		this.defaultTraversablePathIdentifier = defaultTraversablePathIdentifier;
	}

	/**
	 * @param defaultTraversablePathIdentifier
	 *            the defaultTraversablePathIdentifier to set
	 */
	@Required
	public void setDefaultTraversableTypeIdentifier(
			TraversableTypeIdentifier defaultTraversableTypeIdentifier) {
		this.defaultTraversableTypeIdentifier = defaultTraversableTypeIdentifier;
	}

	static class TraversedPath {
		private Object root;

		private List<FieldAndValue> traversedFields = new ArrayList<FieldAndValue>();

		public TraversedPath(Object root) {
			super();
			this.root = root;
		}

		/**
		 * @return the root
		 */
		public Object getRoot() {
			return root;
		}

		/**
		 * @return the traversedFields
		 */
		public List<FieldAndValue> getTraversedFields() {
			return Collections.unmodifiableList(traversedFields);
		}
	}

	static class FieldAndValue {
		private Field field;

		private Object value;

		/**
		 * @return the field
		 */
		public Field getField() {
			return field;
		}

		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}

	}
}
