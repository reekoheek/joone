/*
 * NeuralNetDrawing.java
 *
 * Created on 10 aprile 2001, 0.34
 */

package org.joone.edit;

import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.net.NeuralNet;
import org.joone.util.MonitorPlugin;

public class NeuralNetDrawing extends CH.ifa.draw.standard.StandardDrawing {
    
    private NeuralNet layers;
    
    private static final long serialVersionUID = 4352010555373878830L;
    
    /** Creates new NeuralNetDrawing */
    public NeuralNetDrawing() {
        super();
        layers = new NeuralNet();
    }
    
    public CH.ifa.draw.framework.Figure remove(CH.ifa.draw.framework.Figure figure) {
        CH.ifa.draw.framework.Figure retValue;
        
        retValue = super.remove(figure);
        if (figure instanceof TeacherLayerFigure) {
            if (((TeacherLayerFigure)figure).getOutputLayer() instanceof TeachingSynapse)
                layers.setTeacher(null);
        } else
            if (figure instanceof LayerFigure) {
                LayerFigure lFigure = (LayerFigure)figure;
                Layer layer = (Layer)lFigure.getLayer();
                if (layer != null)
                    layers.removeLayer(layer);
            }
            else
                if (figure instanceof MonitorPluginFigure) {
                    MonitorPluginFigure mpFigure = (MonitorPluginFigure)figure;
                    MonitorPlugin layer = (MonitorPlugin)mpFigure.getLayer();
                    if (layer != null)
                        layers.removeNeuralNetListener(layer);
                }
        
        return retValue;
    }
    
    public CH.ifa.draw.framework.Figure add(CH.ifa.draw.framework.Figure figure) {
        CH.ifa.draw.framework.Figure retValue;
        retValue = super.add(figure);
        if (figure instanceof TeacherLayerFigure) {
            TeacherLayerFigure tlFigure = (TeacherLayerFigure)figure;
            ComparingElement ts = (ComparingElement)tlFigure.getOutputLayer();
            if ((ts != null) && (ts instanceof TeachingSynapse))
                layers.setTeacher(ts);
        } else
            if (figure instanceof LayerFigure) {
                LayerFigure lFigure = (LayerFigure)figure;
                Layer layer = (Layer)lFigure.getLayer();
                if (layer != null)
                    layers.addLayer(layer);
            }
            else
                if (figure instanceof MonitorPluginFigure) {
                    MonitorPluginFigure mpFigure = (MonitorPluginFigure)figure;
                    MonitorPlugin layer = (MonitorPlugin)mpFigure.getLayer();
                    if (layer != null)
                        layers.addNeuralNetListener(layer);
                }
        
        return retValue;
    }
    
    public void replace(CH.ifa.draw.framework.Figure figure,CH.ifa.draw.framework.Figure figure1) {
        super.replace(figure, figure1);
    }
    
    public NeuralNet getNeuralNet() {
        return layers;
    }
    
    /** Setter for property layers.
     * @param newLayers New value of NeuralNet.
     */
    public void setNeuralNet(NeuralNet newLayers) {
        this.layers = newLayers;
    }
    
}
