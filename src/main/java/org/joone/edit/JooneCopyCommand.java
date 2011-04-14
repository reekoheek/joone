
package org.joone.edit;

import java.util.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

public class JooneCopyCommand extends CopyCommand {

    public JooneCopyCommand(String name, DrawingView view) {
        super(name, view);
    }

    public boolean isExecutable() {
        // Disable if Joone components selected.
        if (fView.selectionCount() > 0) {
            FigureEnumeration fe = fView.selectionElements();
            try {
                while (true) {
                    Figure f = fe.nextFigure();
                    if (f instanceof ConcreteGenericFigure) {
                        return false;
                    }
                }
            } catch (NoSuchElementException e) {
                // End of enumeration.
            }

            return true;
        } else {
            return false;
        }
    }
}


