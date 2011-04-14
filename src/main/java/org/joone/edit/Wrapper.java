/*
 * Wrapper.java
 *
 * Created on 26 aprile 2001, 23.24
 */

package org.joone.edit;

import java.util.Vector;

public class Wrapper {
    
    protected Object bean;
    protected Vector changedProperties;
    protected String beanName;
    protected UpdatableFigure figure;
    /** Creates new Wrapper
     * @param b the bean contained by the Wrapper
     * @param name the name of the bean
     */
    public Wrapper(UpdatableFigure uf, Object b, String name) {
        figure = uf;
        bean = b;
        beanName = name;
    }
    
    Object getBean() {
        return bean;
    }
    
    String getBeanName() {
        return beanName;
    }
    
    /** get the properties changed at design time.
     * @return the list of changed properties
     */
    public Vector getChangedProperties() {
        if (changedProperties == null) {
            changedProperties = new Vector();
        }
        return changedProperties;
    }
    
    public void updateFigure() {
        if (figure != null)
            figure.update();
    }
    
}
