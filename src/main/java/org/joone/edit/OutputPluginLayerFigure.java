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
import org.joone.util.*;

/**
 *
 * @author  pmarrone
 */
public class OutputPluginLayerFigure extends LayerFigure {
    
    static final long serialVersionUID = 931739721859387757L;
    protected OutputConverterPlugIn myPlugin;
        
    /** Creates new InputLayerFigure */
    public OutputPluginLayerFigure() {
    }
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myPlugin = (OutputConverterPlugIn)createLayer();
        
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
        name.setText("OutputPlugin " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
    }
    
    public void addPostConn(LayerFigure figure) {
        if (!fPostConn.contains(figure))
            fPostConn.addElement(figure);
    }
    
    public void addPreConn(LayerFigure layerFigure, OutputConverterPlugIn plugin) {
        this.getPluginLayer().setNextPlugIn(plugin);
        if (!fPreConn.contains(layerFigure)) {
            fPreConn.addElement(layerFigure);
        }
    }
    
    public void removePreConn(LayerFigure figure, OutputConverterPlugIn plugin) {
        fPreConn.removeElement(figure);
        this.getPluginLayer().addPlugIn(null);
    }
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.south(),
        new OutputPluginConnection(), Color.magenta));
        return handles;
    }
    
    /** Get the InputLayer object
     * @return the InputLayer object (org.joone.StreamInputSynapse)
     */
    
    public OutputConverterPlugIn getPluginLayer() {
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
        if (!(conn instanceof OutputPluginConnection))
            retValue = false;
        if (getPluginLayer().isConnected())
            retValue = false;
        return retValue;
    }
    
}