/*Copyright (c)2006 Tavant Technologies
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

package tavant.twms.infra.xstream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedSet;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author roopali.agrawal
 * 
 */
public class HibernateCollectionConverter implements Converter {
	private Converter listSetConverter;

	private Converter mapConverter;

	private Converter treeMapConverter;

	private Converter treeSetConverter;

	private Converter defaultConverter;

	public HibernateCollectionConverter(ConverterLookup converterLookup) {
		listSetConverter = converterLookup
				.lookupConverterForType(ArrayList.class);
		mapConverter = converterLookup.lookupConverterForType(HashMap.class);
		treeMapConverter = converterLookup
				.lookupConverterForType(TreeMap.class);
		treeSetConverter = converterLookup
				.lookupConverterForType(TreeSet.class);
		defaultConverter = converterLookup.lookupConverterForType(Object.class);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
	 */
	public boolean canConvert(Class type) {
		return PersistentCollection.class.isAssignableFrom(type);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		Object collection = source;
		PersistentCollection col = null;
		
		if (source instanceof PersistentCollection) {
			col = (PersistentCollection) source;
			col.forceInitialization();						
		}

		if(source instanceof PersistentBag){
			collection = new ArrayList((Collection)col);
		}
		else if (source instanceof PersistentSortedSet) {			 
				collection = new TreeSet(((Set) col));
		} else if (source instanceof PersistentSet) {
				collection = new HashSet((Set)col);
		}		
		// delegate the collection to the approapriate converter
		if (listSetConverter.canConvert(collection.getClass())) {
			listSetConverter.marshal(collection, writer, context);
			return;
		}
		if (mapConverter.canConvert(collection.getClass())) {
			mapConverter.marshal(collection, writer, context);
			return;
		}
		if (treeMapConverter.canConvert(collection.getClass())) {
			treeMapConverter.marshal(collection, writer, context);
			return;
		}
		if (treeSetConverter.canConvert(collection.getClass())) {
			treeSetConverter.marshal(collection, writer, context);
			return;
		}

		defaultConverter.marshal(collection, writer, context);
	}

	/**
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		return null;
	}
}