/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joone.edit;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * PropertyPanel. <br>
 */
public class PropertyPanel extends JPanel {
    final PropertySheetPanel sheet;
    Wrapper source;
    
    public PropertyPanel() {
        setLayout(new BorderLayout());
        sheet = new PropertySheetPanel();
        sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
        sheet.setToolBarVisible(false);
        sheet.setDescriptionVisible(false);
        sheet.setPreferredSize(new Dimension(60,170));
        JScrollPane scroll = new JScrollPane(sheet);
        add(scroll, BorderLayout.CENTER);
    }
    
    public void setTarget(Wrapper wrapper) {
        source = wrapper;
        final Object target = wrapper.getBean();
        // Remove the old listener
        PropertyChangeListener listener = (PropertyChangeListener)sheet.getClientProperty("listener");
        if (listener != null)
            sheet.removePropertySheetChangeListener(listener);
        
        try {
            BeanInfo beanInfo =  Introspector.getBeanInfo(target.getClass());
            sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            sheet.setProperties(getFilteredProperties(props));
            // Initialize the properties with the value from the object.
            Property[] properties = sheet.getProperties();
            for (int i = 0, c = properties.length; i < c; i++) {
                try {
                    properties[i].readFromObject(target);
                } catch (Exception doNothing) { /* Left intentionally empty */ }
            }
            setListener(target, sheet);
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void update() {
        source.updateFigure();
    }
    
    /**
     * This utility class filters the properties of an object
     * in order to include only the properties that are not hidden,
     * not expert, and have declared both the getter and setter methods.
     * Used to display the correct properties in the PropertySheetPanel.
     */
    private PropertyDescriptor[] getFilteredProperties(PropertyDescriptor[] origin) {
        ArrayList props = new ArrayList();
        for (int i=0; i < origin.length; ++i) {
            PropertyDescriptor prop = origin[i];
            Method getter = prop.getReadMethod();
            Method setter = prop.getWriteMethod();
            if ((!prop.isHidden()) && (!prop.isExpert()) &&
                    (setter != null) && (getter != null))
                props.add(prop);
        }
        return (PropertyDescriptor[])props.toArray(new PropertyDescriptor[props.size()]);
    }
    
    private void setListener(final Object theObject, PropertySheetPanel sheet) {
        // everytime a property change, update the component with it
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property)evt.getSource();
                prop.writeToObject(theObject);
                if (theObject instanceof JComponent)
                    ((JComponent)theObject).repaint();
                source.updateFigure();
            }
        };
        sheet.addPropertySheetChangeListener(listener);
        sheet.putClientProperty("listener", listener);
        sheet.repaint();
    }
    
}
