package org.joone.edit;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.standard.*;

import org.joone.log.*;
import org.joone.edit.inspection.InspectionFrame;
import org.joone.engine.*;
import org.joone.inspection.Inspectable;

/** Graphic representation of a connection between two layers
 */
public class LayerConnection extends LineConnection implements GenericFigure, UpdatableFigure 
{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (LayerConnection.class);
    
        /*
         * Serialization support.
         */
    private static final long serialVersionUID = -1564509302372952850L;
    
    private Hashtable params;
    protected Synapse mySynapse;
    
    
    /** Constructor
     */
    public LayerConnection() {
        setEndDecoration(new ArrowTip());
        setStartDecoration(null);
        params = new Hashtable();
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        LayerFigure source = (LayerFigure)start;
        LayerFigure target = (LayerFigure)end;
        if (end instanceof OutputLayerFigure) {
            OutputLayerFigure olf = (OutputLayerFigure)target;
            OutputPatternListener myListener = olf.getOutputLayer();
            olf.addPreConn(source);
            source.addPostConn(target, myListener);
            source.notifyPostConn();
        } else {
            mySynapse = (Synapse)createSynapse();
            boolean newConn = target.addPreConn(source, mySynapse);
            source.addPostConn(target, mySynapse);
            source.notifyPostConn();
            if ((newConn) && (source.hasCycle(target))) {
                setAttribute("FrameColor", Color.red);
                mySynapse.setLoopBack(true);
            }
            
        }
    }
    
    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        LayerFigure source = (LayerFigure)start;
        LayerFigure target = (LayerFigure)end;
        if (target != null) {
            target.removePreConn(source, getSynapse());
        }
        if (source != null) {
            if (target instanceof OutputLayerFigure)
                source.removePostConn(target, ((OutputLayerFigure)target).getOutputLayer());
            else
                source.removePostConn(target, getSynapse());
        }
    }
    
    /** determines if the two layers can be connected
     * @param start starting layer
     * @param end ending layer
     * @return true if can be connected
     */
    public boolean canConnect(Figure start, Figure end) {
        boolean retVal;
        retVal = (start instanceof ConcreteGenericFigure && end instanceof ConcreteGenericFigure);
        if (retVal) {
            ConcreteGenericFigure fEnd = (ConcreteGenericFigure)end;
            ConcreteGenericFigure fStart = (ConcreteGenericFigure)start;
            retVal = retVal && fEnd.canConnect(fStart, this);
        }
        return retVal;
    }
    
    public Vector handles() {
        Vector handles = super.handles();
        // don't allow to reconnect the starting figure
        handles.setElementAt(
        new NullHandle(this, PolyLineFigure.locator(0)), 0);
        return handles;
    }
    
    
    /** Creates the Synapses object represented by this connection
     * @return the Synapse created
     */
    protected Object createSynapse() {
        Object ly;
        try 
        {
            String cl = (String) getParam("class");
            if (cl == null) // Needed to maintain the backward compatibility
                cl = new String("org.joone.engine.FullSynapse");
            Class cLayer = Class.forName(cl);
            ly = cLayer.newInstance();
            return ly;
        } catch (ClassNotFoundException cnfe) 
        {
            log.warn ( "ClassNotFoundException thrown. Message is : " + cnfe.getMessage(),
                       cnfe ); 
        } catch (InstantiationException ie) 
        {
            log.warn ( "InstantiationException thrown. Message is : " + ie.getMessage(),
                       ie);
        } catch (IllegalAccessException iae) 
        {
            log.warn ( "IllegalAccessException thrown. Message is : " + iae.getMessage(),
                       iae );
        }
        return null;
    }
    
    /** Get the internal Synapse object
     * @return the Synapse represented
     */
    public Synapse getSynapse() {
        return mySynapse;
    }
    
    /** Set the internal Synapse object
     * @param newMySynapse the Synapse to represent
     */
    public void setSynapse(Synapse newMySynapse) {
        mySynapse = newMySynapse;
    }
    
    /** Used to get a parameter by name
     * @return the parameter's value
     * @param key the parameter's name
     */
    public Object getParam(Object key) {
        if (params == null)
            return null;
        return params.get(key);
    }
    /** Sets a parameter of the synapse
     * @param key the parameter's name
     * @param newParam The parameter's value
     */
    public void setParam(Object key, Object newParam) {
        params.put(key, newParam);
    }
    
    /** Used to set all parameters of the synapse
     * @param newParams java.util.Hashtable
     */
    public void setParams(java.util.Hashtable newParams) {
        params = newParams;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        Synapse syn = getSynapse();
        if (syn != null)
            return new Wrapper(this, getSynapse(), getSynapse().getName());
        else
            return null;
    }
    
    public void update() {
    }
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn) {
        return true;
    }
    
    public void draw(Graphics g) {
        Object oldColor = getAttribute("FrameColor");
        if ((mySynapse != null) && (mySynapse.isLoopBack()))
            setAttribute("FrameColor", Color.red);
        else
            setAttribute("FrameColor", oldColor);
        super.draw(g);
        String lbl = (String)getParam("label");
        Synapse syn = getSynapse();
        if ((lbl != null) && (syn != null)) {
            Point p1, p2;
            int i = fPoints.size();
            p1 = (Point) fPoints.elementAt(0);
            p2 = (Point) fPoints.elementAt(i - 1);
            int xMin = p1.x;
            int xMax = p2.x;
            if (xMin < p2.x) {
                xMin = p2.x;
                xMax = p1.x;
            }
            int yMin = p1.y;
            int yMax = p2.y;
            if (yMin < p2.y) {
                yMin = p2.y;
                yMax = p1.y;
            }
            // Calculates the center of the line
            Point p = new Point();
            p.x = xMin + (xMax - xMin)/2;
            p.y = yMin + (yMax - yMin)/2;
            if (syn.isEnabled())
                g.setColor(new Color(255,255,255));
            else
                g.setColor(new Color(170,170,170));
            g.fillRect(p.x-6, p.y-7, 12, 14);
            g.setColor(new Color(0,0,200));
            g.drawRect(p.x-6, p.y-7, 12, 14);
            g.drawString(lbl,p.x-4,p.y+5);
            g.setColor(getFrameColor());
        }
    }
    /**
     * Get the popup menu for this connection.
     * Put here all the popup menu items common to all the connections;
     * override this method to differentiate the menu items.
     * @param ps the property sheet to use in the Properties option.
     * @todo is there a better way to set the minimum size?
     * @return the popup menu.
     */
    public JPopupMenu getPopupMenu(final PropertySheet ps, final DrawingView view) {
        JPopupMenu jpm = new JPopupMenu();
        JMenuItem jmi;
        final Wrapper wrap = getWrapper();
        if (wrap != null) {
            jmi = new JMenuItem("Properties");
            jmi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (ps != null) {
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
        }
        jmi = new JMenuItem("Delete");
        jmi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeleteCommand("Delete", view).execute();
            }
        });
        jpm.add(jmi);
        if ((wrap != null) && (getWrapper().getBean() instanceof Inspectable)) {
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