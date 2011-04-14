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
import org.joone.io.*;
import org.joone.util.*;

/**
 *
 * @author  pmarrone
 */
public class InputLayerFigure extends LayerFigure {
    
    protected InputPatternListener myInputLayer;
    
    private static final long serialVersionUID = -6656835765898891646L;
    
    /** Creates new InputLayerFigure */
    public InputLayerFigure() {
    }
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myInputLayer = (InputPatternListener)createLayer();
        
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getInputLayer().setName(newText);
            }
            public void update() {
                if (myInputLayer.isEnabled())
                    this.setAttribute("TextColor", Color.blue);
                else
                    this.setAttribute("TextColor", Color.gray);
                setText(getInputLayer().getName());
            }
        };
        
        name.setFont(fb);
        name.setText("InputLayer " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
    }
    
    public void addPostConn(LayerFigure figure) {
        if (!fPostConn.contains(figure))
            fPostConn.addElement(figure);
    }
    
    public boolean addPreConn(LayerFigure layerFigure, InputPatternListener synapse) {
        if (!fPreConn.contains(layerFigure)) {
            fPreConn.addElement(layerFigure);
            return true;
        }
        else
            return false;
    }
    
    public void addPreConn(LayerFigure layerFigure, ConverterPlugIn plugin) {
        getInputLayer().addPlugIn(plugin);
        if (!fPreConn.contains(layerFigure)) {
            fPreConn.addElement(layerFigure);
        }
    }
    
    public void removePreConn(LayerFigure figure, ConverterPlugIn plugin) {
        fPreConn.removeElement(figure);
        getInputLayer().addPlugIn(null);
    }
    
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(),
        new InputLayerConnection()));
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.south(),
        new InputPluginConnection(), Color.magenta));
        return handles;
    }
    
    /** Gets the InputLayer object
     * @return the InputLayer object (org.joone.StreamInputSynapse)
     */
    public StreamInputSynapse getInputLayer() {
        return (StreamInputSynapse)myInputLayer;
    }
    
    public boolean canConnect() {
        return true;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        return new Wrapper(this, getInputLayer(), getInputLayer().getName());
    }
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn) {
        boolean retValue;
        StreamInputSynapse sis = (StreamInputSynapse)myInputLayer;
        retValue = false;
        if ((conn instanceof DesiredLayerConnection) && (!sis.isInputFull()))
            retValue = true;
        if ((conn instanceof ValidationLayerConnection) && (!sis.isInputFull()))
            retValue = true;
        return retValue;
    }
    
}