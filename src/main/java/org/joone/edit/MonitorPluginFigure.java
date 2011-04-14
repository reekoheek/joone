/*
 * MonitorPluginFigure.java
 *
 * Created on 31 gennaio 2002, 19.53
 */

package org.joone.edit;

import java.awt.*;
import java.util.*;
import org.joone.util.*;
import org.joone.net.NeuralNet;
import CH.ifa.draw.figures.*;

/**
 *
 * @author  pmarrone
 */
public class MonitorPluginFigure extends ConcreteGenericFigure {

    protected MonitorPlugin myMonitorPlugin;

    private static final long serialVersionUID = 725876265633704876L;

    /** Creates a new instance of MonitorPluginFigure */
    public MonitorPluginFigure() {
        super();
    }

    /** Get the Layer object
     * @return the Layer object
     */
    public Object getLayer() {
        return myMonitorPlugin;
    }

    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        MonitorPlugin ly = (MonitorPlugin)getLayer();
        return new Wrapper(this, ly, ly.getName());
    }

    protected void initContent() {
        myMonitorPlugin = (MonitorPlugin)createLayer();

        Font fb = new Font("Helvetica", Font.BOLD, 12);
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                MonitorPlugin ly = (MonitorPlugin)getLayer();
                ly.setName(newText);
            }
            public void update() {
                MonitorPlugin ly = (MonitorPlugin)getLayer();
                setText(ly.getName());
            }
        };
        name.setFont(fb);
        name.setText("Monitor Plugin " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
    }

    protected Vector addHandles(Vector handles) {
        return handles;
    }
    
    public void initialize() {
        super.initialize();
        ((MonitorPlugin)getLayer()).setNeuralNet((NeuralNet)getParam("NeuralNet"));
    }

}
