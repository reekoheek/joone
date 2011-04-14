/*
 * SpecialConnectionHandle.java
 *
 * Created on 31 maggio 2001, 23.24
 */

package org.joone.edit;

import java.awt.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.framework.*;

/**
 * A SpecialConnectionHandle is a ConnectionHandle with a rect shape and a custom color
 *
 */
public class SpecialConnectionHandle extends ConnectionHandle {

    private Color ovalColor;
    /** Creates new SpecialConnectionHandle */
    public SpecialConnectionHandle(Figure owner, Locator l, ConnectionFigure prototype, Color color) {
        super(owner, l, prototype);
        ovalColor = color;
    }

    /**
     * Draws the special connection handle, by default the outline of a
     * colored rectangle.
     */
    public void draw(Graphics g) {
        Rectangle r = displayBox();
        g.setColor(ovalColor);
        g.drawRect(r.x, r.y, r.width, r.height);
    }
    
}
