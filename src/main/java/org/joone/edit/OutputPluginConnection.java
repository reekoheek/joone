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

public class OutputPluginConnection extends LayerConnection {
    
    static final long serialVersionUID = 4263238726702655849L;
    
    /** Creates new OutputPluginConnection */
    public OutputPluginConnection() {
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
        if (!(end instanceof OutputPluginLayerFigure))
            retValue = false;
        return retValue;
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from OutputPluginLayerFigure to OutputLayerFigure
         */
        OutputPluginLayerFigure source = (OutputPluginLayerFigure)end;
        OutputConverterPlugIn myPlugin = source.getPluginLayer();
        if (start instanceof OutputLayerFigure) {
            OutputLayerFigure target = (OutputLayerFigure)start;
            target.addPreConn(source, myPlugin);
            source.addPostConn(target);
        }
        else {
            OutputPluginLayerFigure target = (OutputPluginLayerFigure)start;
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
        OutputPluginLayerFigure source = (OutputPluginLayerFigure)end;
        if (start instanceof OutputPluginLayerFigure) {
            OutputPluginLayerFigure target = (OutputPluginLayerFigure)start;
            source.removePostConn(target);
            if (target != null)
                target.removePreConn(source, source.getPluginLayer());
        }
        else {
            OutputLayerFigure target = (OutputLayerFigure)start;
            if (target != null)
                target.removePreConn(source, source.getPluginLayer());
            source.removePostConn(target);
        }
    }
    
}
