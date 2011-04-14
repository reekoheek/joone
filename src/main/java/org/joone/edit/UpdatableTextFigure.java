/*
 * UpdatableTextFigure.java
 *
 * Created on 28 aprile 2001, 20.20
 */

package org.joone.edit;

import CH.ifa.draw.figures.TextFigure;

/**
 * Abstract class created to make mandatory the declaration of method update() for the TextFigure class
 * @see LayerFigure
 */
public abstract class UpdatableTextFigure extends TextFigure implements UpdatableFigure {
    private static final long serialVersionUID = 7880675038736706857L;
    
}
