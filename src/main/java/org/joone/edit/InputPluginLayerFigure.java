/*
 * InputLayerFigure.java
 *
 * Created on 2 aprile 2001, 1.17
 */

package org.joone.edit;
import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import org.joone.engine.*;
import org.joone.util.*;

/**
 *
 * @author  pmarrone
 */
public class InputPluginLayerFigure extends LayerFigure {
    
    protected ConverterPlugIn myPlugin;
    
    private static final long serialVersionUID = 3814306272962638219L;
    
    /** Creates new InputLayerFigure */
    public InputPluginLayerFigure() {
    }
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myPlugin = (ConverterPlugIn)createLayer();
        
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getPluginLayer().setName(newText);
            }
            public void update() {
                setText(getPluginLayer().getName());
            }
        };
        
        name.setFont(fb);
        name.setText("InputPlugin " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
    }
    
    public void addPostConn(LayerFigure figure) {
        if (!fPostConn.contains(figure))
            fPostConn.addElement(figure);
    }
    
    public boolean addPreConn(LayerFigure layerFigure, InputPatternListener synapse) {
        return false;
    }
    public void addPreConn(LayerFigure layerFigure, AbstractConverterPlugIn plugin) {
        this.getPluginLayer().addPlugIn(plugin);
        if (!fPreConn.contains(layerFigure)) {
            fPreConn.addElement(layerFigure);
        }
    }
    
    public void removePreConn(LayerFigure figure, AbstractConverterPlugIn plugin) {
        fPreConn.removeElement(figure);
        this.getPluginLayer().addPlugIn(null);
    }
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.south(),
        new InputPluginConnection(), Color.magenta));
        return handles;
    }
    
    /** Get the InputLayer object
     * @return the InputLayer object (org.joone.StreamInputSynapse)
     */
    
    public ConverterPlugIn getPluginLayer() {
        return myPlugin;
    }
    
    public boolean canConnect() {
        return true;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        return new Wrapper(this, getPluginLayer(), getPluginLayer().getName());
    }
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn) {
        boolean retValue;
        
        retValue = super.canConnect(start, conn);
        if (!(conn instanceof InputPluginConnection))
            retValue = false;
        if (getPluginLayer().isConnected())
            retValue = false;
        return retValue;
    }
    
}