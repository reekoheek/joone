
package org.joone.edit;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;

import org.joone.log.*;
import org.joone.edit.inspection.InspectionFrame;
import org.joone.inspection.Inspectable;

/** Graphic representation of a generic figure in the net
 */
public abstract class ConcreteGenericFigure extends CompositeFigure implements GenericFigure, UpdatableFigure 
{
    
    /**
     * Logger
     * */
    public static final ILogger log = LoggerFactory.getLogger(ConcreteGenericFigure.class);
    
    protected static final int BORDER = 3;
    protected Rectangle fDisplayBox;
    protected java.util.Hashtable params;
    protected static int numLayers = 0;
    private static final long serialVersionUID=-6051314440171003005L;
    
    public ConcreteGenericFigure() {
    }
    
    protected void basicMoveBy(int x, int y) {
        fDisplayBox.translate(x, y);
        super.basicMoveBy(x, y);
    }
    
    public Rectangle displayBox() {
        return new Rectangle(
        fDisplayBox.x,
        fDisplayBox.y,
        fDisplayBox.width,
        fDisplayBox.height);
    }
    
    public void basicDisplayBox(Point origin, Point corner) {
        fDisplayBox = new Rectangle(origin);
        fDisplayBox.add(corner);
        layout();
    }
    
    private void drawBorder(Graphics g) {
        super.draw(g);
        
        Rectangle r = displayBox();
        
        Figure f = figureAt(0);
        Rectangle rf = f.displayBox();
        g.setColor(Color.gray);
        g.drawLine(r.x, r.y+rf.height+2, r.x+r.width, r.y + rf.height+2);
        g.setColor(Color.white);
        g.drawLine(r.x, r.y+rf.height+3, r.x+r.width, r.y + rf.height+3);
        
        g.setColor(Color.white);
        g.drawLine(r.x, r.y, r.x, r.y + r.height);
        g.drawLine(r.x, r.y, r.x + r.width, r.y);
        g.setColor(Color.gray);
        g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
        g.drawLine(r.x , r.y + r.height, r.x + r.width, r.y + r.height);
    }
    
    public void draw(Graphics g) {
        drawBorder(g);
        super.draw(g);
    }
    
    public Vector handles() {
        Vector handles = new Vector();
        handles.addElement(new NullHandle(this, RelativeLocator.northWest()));
        handles.addElement(new NullHandle(this, RelativeLocator.northEast()));
        handles.addElement(new NullHandle(this, RelativeLocator.southWest()));
        handles.addElement(new NullHandle(this, RelativeLocator.southEast()));
        return addHandles(handles);
    }
    
    protected abstract Vector addHandles(Vector handles);
    
    public void initialize() {
        fDisplayBox = new Rectangle(0, 0, 0, 0);
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        TextFigure type = new TextFigure();
        type.setFont(fb);
        type.setText((String)getParam("type"));
        type.setAttribute("TextColor", Color.white);
        type.setReadOnly(true);
        add(type);
        initContent();
    }
    
    protected abstract void initContent();
    
    private void layout() {
        Point partOrigin = new Point(fDisplayBox.x, fDisplayBox.y);
        partOrigin.translate(BORDER, BORDER);
        Dimension extent = new Dimension(0, 0);
        
        FigureEnumeration k = figures();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();
            
            Dimension partExtent = f.size();
            Point corner = new Point(
            partOrigin.x+partExtent.width,
            partOrigin.y+partExtent.height);
            f.basicDisplayBox(partOrigin, corner);
            
            extent.width = Math.max(extent.width, partExtent.width);
            extent.height += partExtent.height;
            partOrigin.y += partExtent.height;
        }
        fDisplayBox.width = extent.width + 2*BORDER;
        fDisplayBox.height = extent.height + 2*BORDER;
    }
    
    private boolean needsLayout() {
        Dimension extent = new Dimension(0, 0);
        
        FigureEnumeration k = figures();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();
            extent.width = Math.max(extent.width, f.size().width);
        }
        int newExtent = extent.width + 2*BORDER;
        return newExtent != fDisplayBox.width;
    }
    
    public void update(FigureChangeEvent e) {
        if (needsLayout()) {
            layout();
            changed();
        }
    }
    
    public void update() {
        FigureEnumeration k = figures();
        while (k.hasMoreElements()) {
            Figure f = k.nextFigure();
            if (f instanceof UpdatableFigure) {
                UpdatableFigure uf = (UpdatableFigure)f;
                uf.update();
            }
        }
        layout();
        changed();
    }
    
    public void figureChanged(FigureChangeEvent e) {
        update(e);
    }
    
    
    public void figureRemoved(FigureChangeEvent e) {
        update(e);
    }
    
    public void notifyPostConn() {
    }
    
    
    public void write(StorableOutput dw) {
        super.write(dw);
        dw.writeInt(fDisplayBox.x);
        dw.writeInt(fDisplayBox.y);
        dw.writeInt(fDisplayBox.width);
        dw.writeInt(fDisplayBox.height);
    }
    
    
    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        fDisplayBox = new Rectangle(
        dr.readInt(),
        dr.readInt(),
        dr.readInt(),
        dr.readInt());
        layout();
    }
    
    public Insets connectionInsets() {
        Rectangle r = fDisplayBox;
        int cx = r.width/2;
        int cy = r.height/2;
        return new Insets(cy, cx, cy, cx);
    }
    
    
    /** Creates the Layer object represented
     * @return the Layer created
     */
    protected Object createLayer() {
        Object ly;
        String msg;
        try {
            Class cLayer = Class.forName((String) getParam("class"));
            ly = cLayer.newInstance();
            return ly;
        } catch (ClassNotFoundException cnfe) 
        {
            msg = "ClassNotFoundException  was thrown. Message is : " + cnfe.getMessage();
            log.warn ( msg, cnfe);
        } catch (InstantiationException ie) 
        {
            msg = "InstantiationException was thrown. Message is : " + ie.getMessage();
            log.warn (msg, ie);
        } catch (IllegalAccessException iae) 
        {
            msg = "IllegalAccessException was thrown. Message is : " + iae.getMessage();
            log.warn ( msg, iae);
        }
        return null;
    }
    
    /** Used to get a parameter by name
     * @return the parameter's value
     * @param key the parameter's name
     */
    public Object getParam(Object key) {
        return params.get(key);
    }
    
    
    /** Sets a parameter of the layer
     * @param key the parameter's name
     * @param newParam The parameter's value
     */
    public void setParam(Object key, Object newParam) {
        params.put(key, newParam);
    }
    
    /** Used to set all parameters of the layer
     * @param newParams java.util.Hashtable
     */
    public void setParams(java.util.Hashtable newParams) {
        params = newParams;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public abstract Wrapper getWrapper();
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn) {
        return false;
    }
    
    /** Get the Layer object
     * @return the Layer object
     */
    public abstract Object getLayer();
    
    
    /**
     * Get the popup menu for this figure.
     * Put here all the popup menu items common to all the figures;
     * override this method to differentiate the menu items.
     * @param ps the property sheet to use in the Properties option.
     * @todo is there a better way to set the minimum size?
     * @return the popup menu.
     */
    public JPopupMenu getPopupMenu(final PropertySheet ps, final DrawingView view) {
        JPopupMenu jpm = new JPopupMenu();
        JMenuItem jmi = new JMenuItem("Properties");
        jmi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ps != null) {
                    Wrapper wrap = getWrapper();
                    ps.setTarget(wrap);
                    ps.pack();
                    // Make sure the panel is wide enough to fit the title.
                    if (ps.getSize().width < 250) {
                        ps.setSize(new Dimension(250, ps.getSize().height));
                    }
                    ps.show();
                }
            }
        });
        jpm.add(jmi);

        jmi = new JMenuItem("Delete");
        jmi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeleteCommand("Delete", view).execute();
            }
        });
        jpm.add(jmi);

        if (getWrapper().getBean() instanceof Inspectable) {
            jmi = new JMenuItem("Inspect");
            jmi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Inspectable inspectable = (Inspectable) getWrapper().getBean();
                    InspectionFrame iFrame = new InspectionFrame(inspectable);
                    iFrame.show();
                }
            });
            jpm.add(jmi);
        }

        return jpm;
    }
}