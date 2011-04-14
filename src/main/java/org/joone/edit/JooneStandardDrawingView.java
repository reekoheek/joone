package org.joone.edit;

import CH.ifa.draw.standard.*;
import CH.ifa.draw.framework.*;

/**
 * This class extends StandardDrawingView to determin whether the drawing has been modified.
 * The modified attribute is used by JoonEdit to see whether to prompt for Save.
 */
public  class JooneStandardDrawingView extends StandardDrawingView {

    /** Is the drawing modified since last save? */
    private boolean modified = false;

    /**
     * Constructs the view.
     */
    public JooneStandardDrawingView(DrawingEditor editor, int width, int height) {
        super(editor, width, height);
    }

    /**
     * Method to redraw drawing and also to set the drawing as modified.
     */
    public void repairDamage() {
        super.repairDamage();
        modified = true;
    }

    /**
     * See if drawing is modified.
     * @return whether the drawing is modified.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Clear the modified status e.g. after a Save.
     */
    public void setModified(boolean mod) {
        modified = mod;
    }
}
