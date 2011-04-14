/*
 * DesiredLayerConnection.java
 *
 * Created on 31 maggio 2001, 23.41
 */

package org.joone.edit;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import java.awt.Color;
import org.joone.util.*;
import org.joone.io.StreamInputSynapse;

public class InputPluginConnection extends LayerConnection {
    
    private static final long serialVersionUID = 2487660906025538103L;
    
    /** Creates new DesiredLayerConnection */
    public InputPluginConnection() {
        setEndDecoration(null);
        setStartDecoration(new ArrowTip());
        setAttribute("FrameColor", Color.magenta);
    }
    
    /** determines if the two layers can be connected
     * @param start starting layer
     * @param end ending layer
     * @return true if can be connected
     */
    public boolean canConnect(Figure start, Figure end) {
        boolean retValue;
        
        retValue = super.canConnect(start, end);
        if (!(end instanceof InputPluginLayerFigure))
            retValue = false;
        if (start instanceof InputLayerFigure) {
            InputLayerFigure ilf = (InputLayerFigure)start;
            StreamInputSynapse sis = ilf.getInputLayer();
            if (sis.getPlugIn() != null)
                retValue = false;
        }
        if (start instanceof InputPluginLayerFigure) {
            InputPluginLayerFigure ilf = (InputPluginLayerFigure)start;
            ConverterPlugIn cpi = ilf.getPluginLayer();
            if (cpi.getNextPlugIn() != null)
                retValue = false;
        }
        return retValue;
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputPluginLayerFigure to InputLayerFigure
         */
        InputPluginLayerFigure source = (InputPluginLayerFigure)end;
        ConverterPlugIn myPlugin = source.getPluginLayer();
        if (start instanceof InputLayerFigure) {
            InputLayerFigure target = (InputLayerFigure)start;
            target.addPreConn(source, myPlugin);
            source.addPostConn(target);
        }
        else {
            InputPluginLayerFigure target = (InputPluginLayerFigure)start;
            target.addPreConn(source, myPlugin);
            source.addPostConn(target);
        }
        
        source.notifyPostConn();
    }
    
    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputLayerFigure to TeacherLayerFigure
         */
        if (end == null)
            return;
        InputPluginLayerFigure source = (InputPluginLayerFigure)end;
        if (start instanceof InputPluginLayerFigure) {
            InputPluginLayerFigure target = (InputPluginLayerFigure)start;
            source.removePostConn(target);
            if (target != null)
                target.removePreConn(source, source.getPluginLayer());
        }
        else {
            InputLayerFigure target = (InputLayerFigure)start;
            if (target != null)
                target.removePreConn(source, source.getPluginLayer());
            source.removePostConn(target);
        }
    }
    
}
