/*
 * ImageDrawer.java
 *
 * This sample application uses some code written by Jeff Heaton took by the article
 * "Programming Neural Networks in Java" at http://www.jeffheaton.com/ai/index.shtml
 */

package org.joone.samples.editor.som;

/**
 *
 * @author  Administrator
 */
public class ImageDrawer extends javax.swing.JPanel {
    
    private java.awt.Image img = null;
    
    /** Creates a new instance of ImageDrawer */
    public ImageDrawer() {
    }
    
    public void setImageToDraw(java.awt.Image newimg) {
        img = newimg;
        if ( img!= null)
            repaint(0,0,img.getWidth(this),img.getHeight(this));
    }
    
    public java.awt.Image getImageToDraw()
    {
        return(img);
    }
    
    public void paintComponent(java.awt.Graphics g) {
        if ( img!= null )
            g.drawImage((java.awt.Image)img,0,0,img.getWidth(this),img.getHeight(this),java.awt.Color.BLACK,this);
    }
    
}
