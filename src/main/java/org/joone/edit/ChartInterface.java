/*
 * ChartInterface.java
 *
 * Created on 23 aprile 2003, 18.51
 */

package org.joone.edit;

import org.joone.engine.*;
/**
 * This interface declares the methods needed to implement a
 * multi-series charting component.
 * @author  P.Marrone
 */
public interface ChartInterface extends OutputPatternListener {
    public void fwdPut(Pattern pattern, ChartingHandle handle);
    public void removeHandle(ChartingHandle handle);
    public int getMaxXaxis();
    public void setMaxXaxis(int maxXaxis);
    public double getMaxYaxis();
    public void setMaxYaxis(double maxYaxis);
}
