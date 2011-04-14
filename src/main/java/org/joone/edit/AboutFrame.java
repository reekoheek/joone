package org.joone.edit;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

import org.joone.log.*;
import org.joone.net.NeuralNet;
import CH.ifa.draw.util.Iconkit;
import CH.ifa.draw.framework.HJDError;
/**
 * This class creates an About box centered on the coordinates provided.
 *
 * @author Harry Glasgow
 */
public class AboutFrame extends JDialog
{
    private static final ILogger log = LoggerFactory.getLogger (AboutFrame.class);
    /**
     * The array of lines of text to display.
     * Do not use '\n' new lines in the text as the auto-sizing will not cope.
     */
    private static final String [] ABOUT_TEXT = {
        "Joone - Java Object Oriented Neural Engine",
        "http://www.joone.org",
        "pmarrone@users.sourceforge.net"
    };

    /**
     * The array of lines of developers to display.
     * Do not use '\n' new lines in the text as the auto-sizing will not cope.
     */
    private static final String [] HALL_OF_FAME = {
        "Joone was developed by Paolo Marrone,",
        "with the valuable collaboration of:",
        "   Gavin Alford, Mark Allen, Jan Boonen, Yan Cheng Cheok,",
        "   Ka-Hing Cheung, Andrea Corti, Huascar Fiorletta, Firestrand,",
        "   Pascal Deschenes, Jan Erik Garshol, Harry Glasgow,",
        "   Jack Hawkins, Nathan Hindley, Olivier Hussenet,",
        "   Boris Jansen, Shen Linlin, Casey Marshall, Julien Norman,",
        "   Christian Ribeaud, Anat Rozenzon, Trevis Silvers,",
        "   Paul Sinclair, Thomas Lionel Smets, Jerry R.Vos"
    };

    /**
     * The array of lines of external packages to display.
     * Do not use '\n' new lines in the text as the auto-sizing will not cope.
     */
    private static final String [] EXTERNAL_PACK = {
        "Joone uses the following external packages:",
        "   JHotDraw - http://sourceforge.net/projects/jhotdraw",
        "   BeanShell - http://www.beanshell.org",
        "   Groovy - http://groovy.codehaus.org",
        "   jEdit-Syntax - http://sourceforge.net/projects/jedit-syntax",
        "   Log4j -  http://jakarta.apache.org/log4j",
        "   HSSF-POI - http://jakarta.apache.org/poi",
        "   L2FProd - http://common.l2fprod.com",
        "   NachoCalendar - http://nachocalendar.sourceforge.net",
        "   XStream - http://xstream.codehaus.org",
        "   VisAD - http://www.ssec.wisc.edu/~billh/visad.html",
    };

    /**
     * Constructor
     *
     * @param x the x-coordinate to center the frame on.
     * @param y the y-coordinate to center the frame on.
     */
    AboutFrame(Frame owner) {
        setModal(true);
        JRootPane rp = new JRootPane();
        setRootPane(rp);
        rp.setLayout(new BorderLayout());

        Iconkit kit = Iconkit.instance();
        if (kit == null)
            throw new HJDError("Iconkit instance isn't set");
        final Image img = kit.loadImageResource(JoonEdit.DIAGRAM_IMAGES + "jooneShadowSmall.gif");
        /*java.net.URL url = getClass().getResource(JoonEdit.DIAGRAM_IMAGES + "jooneShadowSmall.gif");
        if (url != null) {
            final Image img = Toolkit.getDefaultToolkit().getImage(url.getFile());*/

            if (img != null) {
                Panel q = new Panel() {public void paint(Graphics g) {g.drawImage(img, 0, 0, this);} public Dimension getPreferredSize() {return new Dimension(84, 87);}};
                rp.add(q, BorderLayout.WEST);
            }
        //}
        Button b = new Button("   OK   ");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        Panel p = new Panel();
        p.add(b);
        rp.add(p, BorderLayout.SOUTH);

        TextBlock tb = new TextBlock(ABOUT_TEXT, HALL_OF_FAME, EXTERNAL_PACK);
        rp.add(tb, BorderLayout.CENTER);

        setTitle("About Joone");
        setBackground(Color.white);
        setResizable(false);
        pack();
    }

    /**
     * Places the AboutFrame in the center of the owner frame.
     */
    public void place(int x, int y) {
        setLocation(x - getWidth()/2, y - getHeight()/2);
    }

    /**
     * Inner class to render the multi line block of text.
     */
    class TextBlock extends Canvas {

        /** Padding around the text area. */
        private static final int TEXT_PADDING = 5;

        /** The set of text lines to display */
        private String [] lines;

        /**
         * Constructor. Inserts in the Editor and Engine Version line.
         *
         * @param linesArg the text to display.
         * @param hallOfFameArg the list of developers to display.
         */
        public TextBlock(String [] linesArg, String[] hallOfFameArg, String[] extPack) {

            // Get the NeuralNet version by Reflection so that this does not fall over in a
            // steaming heap for old NeuralNet versions that don't have the getVersion method.
            String neuralNetVersion = "unknown";
            Integer neuralNetNumericVersion = null;
            try {
                Object o = new NeuralNet();
                Method m = o.getClass().getMethod("getVersion", new Class[0]);
                if (m != null) {
                    neuralNetVersion = (String)m.invoke(o, new Object[0]);
                }
                m = o.getClass().getMethod("getNumericVersion", new Class[0]);
                if (m != null) {
                    neuralNetNumericVersion = (Integer)m.invoke(o, new Object[0]);
                }
            } catch (IllegalAccessException iae)
            {
                log.warn ( "IllegalAccessException getting NeuralNetwork version. Message is " + iae.getMessage(),
                           iae );
            } catch (InvocationTargetException ite)
            {
                log.warn ( "InvocationTargetException getting NeuralNetwork version. Message is : " + ite.getMessage(),
                           ite );
            } catch (NoSuchMethodException nsme)
            {
                log.warn ( "NoSuchMethodException getting NeuralNetwork version. Do not panic. Message is : " + nsme.getMessage(),
                           nsme );
            }

            // Add the box text.
            Collection col = new ArrayList();
            for (int i = 0; i < linesArg.length; i++) {
                col.add(linesArg[i]);
            }

            // Add the version lines.
            col.add("");
            col.add("Editor version: " + JoonEdit.getVersion());

            col.add("Engine version: " +
            neuralNetVersion);
            if (neuralNetNumericVersion == null ||
            neuralNetNumericVersion.intValue() < JoonEdit.getNumericRecommendedEngineVersion()) {
                col.add("Engine version " +
                JoonEdit.getRecommendedEngineVersion() +
                " or above recommended");
            }

            // Add the developers names.
            col.add("");
            for (int i = 0; i < hallOfFameArg.length; i++) {
                col.add(hallOfFameArg[i]);
            }

            // Add the developers names.
            col.add("");
            for (int i = 0; i < extPack.length; i++) {
                col.add(extPack[i]);
            }

            // Convert to String array.
            lines = new String [col.size()];
            Iterator iter = col.iterator();
            int i = 0;
            while (iter.hasNext()) {
                lines[i++] = (String)iter.next();
            }
        }

        /**
         * Paint method is overriden to draw the text.
         *
         * @param g the graphics object to do the painting.
         */
        public void paint(Graphics g){
            FontMetrics fm = getFontMetrics(getFont());
            int lineAscent = fm.getAscent();
            int lineHeight = fm.getHeight();

            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], TEXT_PADDING, i * lineHeight + lineAscent + TEXT_PADDING);
            }
        }

        /**
         * Method to return the preferred size,
         * so that pack will automatically compress the fame.
         *
         * @return the prefered size of the canvas.
         */
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int lineHeight = fm.getHeight();

            // Find the length of the longest line.
            int width = 0;
            for (int i = 0; i < lines.length; i++) {
                if (fm.stringWidth(lines[i]) > width) {
                    width = fm.stringWidth(lines[i]);
                }
            }

            return new Dimension(
            width + 2 * TEXT_PADDING,
            lines.length * lineHeight + 2 * TEXT_PADDING);
        }
    }
}