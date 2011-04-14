/*
 * SynapseConnectionTool.java
 *
 * Created on 17 agosto 2002, 19.29
 */

package org.joone.edit;

import CH.ifa.draw.standard.*;
import CH.ifa.draw.framework.*;
import java.util.Hashtable;

import org.joone.log.*;

/**
 *
 * @author  PMLMAPA
 */
public class SynapseCreationTool extends ConnectionTool {
    
    public static final ILogger log = LoggerFactory.getLogger (SynapseCreationTool.class);
    protected Hashtable params;
    String objType;
    DrawingView view;
    
    /** Creates a new instance of SynapseConnectionTool */
    public SynapseCreationTool(DrawingView view, String type) {
        super(view, null);
        params = new Hashtable();
        objType = type;
        this.view = view;
    }
    
    protected ConnectionFigure createConnection() {
        try {
            Class cLayer = Class.forName(objType);
            LayerConnection lc = (LayerConnection)cLayer.newInstance();
            lc.setParams(params);
            // Each component is provided with a pointer to the parent NeuralNet object
            NeuralNetDrawing nnd = (NeuralNetDrawing)view.drawing();
            lc.setParam("NeuralNet", nnd.getNeuralNet());
            return lc;
        } catch (ClassNotFoundException cnfe) 
        {
            log.warn ( "ClassNotFoundException exception thrown while ConnectionFigure. Message is : " + cnfe.getMessage(),
                       cnfe );
        } catch (InstantiationException ie) {
            log.warn ( "InstantiationException exception thrown while ConnectionFigure. Message is : " + ie.getMessage(),
                       ie );

        } catch (IllegalAccessException iae) {
            log.warn ( "IllegalAccessException exception thrown while ConnectionFigure. Message is : " + iae.getMessage(),
                       iae );
        }
        return null;
    }    
    /** Used to get a parameter by name
     * @return the parameter's value
     * @param key the parameter's name
     */
    public Object getParam(Object key) {
        return params.get(key);
    }
    
    /** Used to set a parameter of the layer
     * @param key the parameter's name
     * @param newParam the parameter's value
     */
    public void setParam(Object key, Object newParam) {
        params.put(key, newParam);
    }
    
    public void setParams(Hashtable newParams) {
        params = newParams;
    }   
}
