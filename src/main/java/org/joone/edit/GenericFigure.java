/*
 * GenericFigure.java
 *
 * Created on 26 aprile 2001, 23.14
 */

package org.joone.edit;

import CH.ifa.draw.framework.*;
public interface GenericFigure {
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn);
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper();
    /** Used to get a parameter by name
     * @return the parameter's value
     * @param key the parameter's name
     */
    public Object getParam(Object key);
    /** Sets a parameter of the layer
     * @param key the parameter's name
     * @param newParam The parameter's value
     */
    public void setParam(Object key, Object newParam);
    
    /** Used to set all parameters of the layer
     * @param newParams java.util.Hashtable
     */
    public void setParams(java.util.Hashtable newParams);
    
}

