/*
 * InputLayerConnection.java
 *
 * Created on 3 aprile 2001, 0.31
 */

package org.joone.edit;
import CH.ifa.draw.framework.*;
import org.joone.engine.*;
/**
 *
 * @author  pmarrone
 */
public class ChartHandleConnection extends org.joone.edit.LayerConnection {
    
    private static final long serialVersionUID = 2021057399294889866L;
    
    /** Creates new InputLayerConnection */
    public ChartHandleConnection() {
        super();
    }
    
    public boolean canConnect(Figure start, Figure end) {
        boolean retVal;
        retVal = (end instanceof OutputLayerFigure);
        if (retVal) {
            OutputPatternListener target = ((OutputLayerFigure)end).getOutputLayer();
            retVal = (target instanceof ChartInterface);
            if (retVal)  {
                ChartingHandle handle = (ChartingHandle)((ChartHandleLayerFigure)start).getOutputLayer();
                retVal = (handle.getChartSynapse() == null);
            }
        }
        return retVal;
    }
}
