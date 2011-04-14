package com.xinixtech.joone.io;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MyInputSynapseBeanInfo extends SimpleBeanInfo {
	// Bean descriptor
	// GEN-FIRST:BeanDescriptor
	/* lazy BeanDescriptor */
	private static BeanDescriptor getBdescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(
				MyInputSynapse.class, null);
		// GEN-HEADEREND:BeanDescriptor
		// Here you can add code for customizing the BeanDescriptor.
		return beanDescriptor;
	}

	// GEN-LAST:BeanDescriptor

	// Property identifiers //GEN-FIRST:Properties
	private static final String[][] propIds = {
		{"dbUrl", "DB URL"},
		{"dbUsername", "DB Username"},
		{"dbPassword", "DB Password"},
		{"sqlQuery", "SQL Query"},
		{"firstRow", "First Row"},
		{"lastRow", "Last Row"},
		{"maxResults", "Max Results"},
		{"advancedColumnSelector", "Advanced Column Selector"}
	};
	//

	// Property array
	/* lazy PropertyDescriptor */
	private static PropertyDescriptor[] getPdescriptor() {
		PropertyDescriptor[] properties = new PropertyDescriptor[propIds.length];

		try {
			for (int i = 0; i < propIds.length; i++) {
				String s = Character.toUpperCase(propIds[i][0].charAt(0)) + propIds[i][0].substring(1);
				properties[i] = new PropertyDescriptor(propIds[i][1],
						MyInputSynapse.class, "get" + s, "set" + s);
			}
			
		} catch (IntrospectionException e) {
		}// GEN-HEADEREND:Properties

		// Here you can add code for customizing the properties array.

		return properties;
	}

	// GEN-LAST:Properties
	// EventSet identifiers//GEN-FIRST:Events
	// EventSet array

	/* lazy EventSetDescriptor */
	private static EventSetDescriptor[] getEdescriptor() {
		EventSetDescriptor[] eventSets = new EventSetDescriptor[0];
		// GEN-HEADEREND:Events
		// Here you can add code for customizing the event sets array.

		return eventSets;
	}

	// GEN-LAST:Events
	// Method identifiers //GEN-FIRST:Methods
	// Method array

	/* lazy MethodDescriptor */
	private static MethodDescriptor[] getMdescriptor() {
		MethodDescriptor[] methods = new MethodDescriptor[0];
		// GEN-HEADEREND:Methods
		// Here you can add code for customizing the methods array.
		return methods;
	}// GEN-LAST:Methods

	private static final int defaultPropertyIndex = -1;// GEN-BEGIN:Idx
	private static final int defaultEventIndex = -1;// GEN-END:Idx

	// GEN-FIRST:Superclass
	// Here you can add code for customizing the Superclass BeanInfo.
	// GEN-LAST:Superclass

	public BeanDescriptor getBeanDescriptor() {
		return getBdescriptor();
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return getPdescriptor();
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return getEdescriptor();
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return getMdescriptor();
	}

	public int getDefaultPropertyIndex() {
		return defaultPropertyIndex;
	}

	public int getDefaultEventIndex() {
		return defaultEventIndex;
	}
}
