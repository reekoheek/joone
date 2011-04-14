/*
 * ToolElement.java
 *
 * Created on 26 marzo 2001, 0.02
 */

package org.joone.edit;

import java.util.*;
/**
 *
 * @author  pmarrone
 */
public class ToolElement {
    
    protected Hashtable params; 
    protected String type;
    
    /** Creates new ToolElement */
    public ToolElement() {
        params = new Hashtable();
    }
    public ToolElement(String newType)
    {
        this();
        type = newType;
    }
    
    public Hashtable getParams() {
        return params;
    }
    
    public void setParam(Object key, Object value) {
        if (!params.containsKey(key))
            params.put(key, value);
    }
    
    public Object getParam(Object key) {
        return params.get(key);
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String newType) {
        type = newType;
    }
    
}
