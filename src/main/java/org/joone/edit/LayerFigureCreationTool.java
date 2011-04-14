package org.joone.edit;


import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import java.util.*;

import org.joone.log.*;

/**
 * A more efficient version of the generic object creation
 * tool that is not based on cloning.
 */

public class LayerFigureCreationTool extends CreationTool 
{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (LayerFigureCreationTool.class);
    
    protected Hashtable params;
    String objType;
    DrawingView view;
    public LayerFigureCreationTool(DrawingView view, String type) {
        super(view);
        params = new Hashtable();
        objType = type;
        this.view = view;
    }
    
    /**
     * Creates a new Figure.
     */
    protected Figure createFigure() {
        try {
            Class cLayer = Class.forName(objType);
            ConcreteGenericFigure lf = (ConcreteGenericFigure)cLayer.newInstance();
            lf.setParams(params);
            // Each component is provided with a pointer to the parent NeuralNet object
            NeuralNetDrawing nnd = (NeuralNetDrawing)view.drawing();
            lf.setParam("NeuralNet", nnd.getNeuralNet());
            lf.initialize();
            return lf;
        } catch (ClassNotFoundException cnfe) 
        {
            log.warn ( "ClassNotFoundException thrown. Message is : " + cnfe.getMessage(), 
                       cnfe);
        } catch (InstantiationException ie) {
            log.warn ( "InstantiationException thrown. Message is : " + ie.getMessage(), 
                       ie);
        } catch (IllegalAccessException iae) {
            log.warn ( "IllegalAccessException thrown. Message is : " + iae.getMessage(), 
                       iae);
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