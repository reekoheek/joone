package org.joone.edit;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.contrib.*;
import CH.ifa.draw.figures.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Delegate mouse selection to a specific TextTool if
 * the figure selected inside a CompositeFigure is a
 * TextFigure
 */
public class DelegationSelectionTool extends CustomSelectionTool {
    
    /**
     * TextTool which will be invoked at the top level container.
     */
    private TextTool myTextTool;
    private PropertySheet myPsp;
    
    /** Costructor
     * @param view The DrawingView
     */
    public DelegationSelectionTool(DrawingView view) {
        super(view);
        setTextTool(new TextTool(view, new TextFigure()));
    }
    
    /**
     * Hook method which can be overriden by subclasses to provide
     * specialised behaviour in the event of a mouse double click.
     * @param e The MouseEvent
     * @param x x cursor position
     * @param y y cursor position
     */
    protected void handleMouseDoubleClick(MouseEvent e, int x, int y) {
        Figure figure = drawing().findFigureInside(e.getX(), e.getY());
        if ((figure != null) && (figure instanceof TextFigure)) {
            TextFigure txtFig = (TextFigure)figure;
            if (!txtFig.readOnly()) {
                getTextTool().activate();
                getTextTool().mouseDown(e, x, y);
            }
        }
        
    }
    public void setPropertyPanel(PropertySheet psp) {
        myPsp = psp;
    }
    
    /** Getter for property panel.
     * @return Value of property myPsp.
     */
    public PropertySheet getPropertyPanel() {
        return myPsp;
    }
    
    /**
     * Hook method which can be overriden by subclasses to provide
     * specialised behaviour in the event of a mouse down.
     * @param e The MouseEvent
     * @param x x cursor position
     * @param y y cursor position
     */
    protected void handleMouseClick(MouseEvent e, int x, int y) {
        deactivate();
    }
    
    /**
     * Terminates the editing of a text figure.
     */
    public void deactivate() {
        super.deactivate();
        if (getTextTool().isActivated()) {
            getTextTool().deactivate();
        }
    }
    
    /**
     * Set the text tool to which double clicks should be delegated. The text tool is shared by
     * all figures upon which this selection tool operates.
     *
     * @param newTextTool delegate text tool
     */
    protected void setTextTool(TextTool newTextTool) {
        myTextTool = newTextTool;
    }
    
    /**
     * Return the text tool to which double clicks are delegated. The text tool is shared by
     * all figures upon which this selection tool operates.
     *
     * @return delegate text tool
     */
    protected TextTool getTextTool() {
        return myTextTool;
    }
    
    /**
     * This method displays a popup menu as defined in ConcreteSelectionTool.
     *
     * @param   figure      Figure for which a popup menu should be displayed
     * @param   x           x coordinate where the popup menu should be displayed
     * @param   y           y coordinate where the popup menu should be displayed
     * @param   component   Component which invoked the popup menu
     */
    protected void showPopupMenu(Figure figure, int x, int y, Component comp) {
        deactivate();
        
        // The figure parameter is a JHotDraw figure, not a ConcreteGenericFigure.
        // So we actually have to find figure2 from the drawing.
        Figure figure2 = drawing().findFigure(x, y);
        
        if (figure2 != null) {
            if (figure2 instanceof ConcreteGenericFigure) {
                // Show the popup for the figure.
                ((ConcreteGenericFigure)figure2).getPopupMenu(myPsp, view()).show(comp, x, y);
            }
            if (figure2 instanceof LayerConnection) {
                javax.swing.JPopupMenu pop = ((LayerConnection)figure2).getPopupMenu(myPsp, view());
                if (pop != null)
                    // Show the popup for the figure.
                    pop.show(comp, x, y);
            }
        }
    }
}