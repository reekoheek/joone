/*
 * UpdatableNumberTextFigure.java
 *
 * Created on 28 aprile 2001, 20.20
 */

package org.joone.edit;

import CH.ifa.draw.figures.NumberTextFigure;

/**
 * Abstract class created to make mandatory the declaration of method update() for the NumberTextFigure class
 * @see LayerFigure
 */
public abstract class UpdatableNumberTextFigure extends NumberTextFigure implements UpdatableFigure {
    private static final long serialVersionUID = -5723919030691367698L;
    
}

