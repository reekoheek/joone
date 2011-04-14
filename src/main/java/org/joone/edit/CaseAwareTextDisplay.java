package org.joone.edit;

import java.awt.*;
import java.beans.*;
import java.io.Serializable;

/**
 * It is a subclass of <code>java.awt.Canvas</code> and displays text.
 * It has several properties of various sorts. The font property is a simple
 * (unbound) property that is inherited from <code>Canvas</code>. The other
 * properties inherited from <code>Canvas</code> are foreground and background.
 * This Bean overrides the property setters, converting them to bound and constrained properties.
 * The <code>CaseAwareTextDisplay</code> Bean also implements some new bound properties:
 * text, topMargin, leftMargin, and textCase. The text property contains the text
 * to be shown starting at the pixel offsets indicated by the topMargin and leftMargin properties.
 * The text is painted using the inherited font, foreground, and background properties. The textCase
 * property is maintained by the Bean as an int and indicates whether the text is to be displayed as
 * typed -- in uppercase, lowercase, or initial caps.
 * 
 * @author christian ribeaud <christian.ribeaud@genedata.com>
 * @see JooneFileChooserEditor
 */
public class CaseAwareTextDisplay extends Canvas
 implements Serializable {
	// Class constants
	public static final int AS_IS = 0;
	public static final int UPPERCASE = 1;
	public static final int LOWERCASE = 2;
	public static final int FIRST_IN_CAPS = 3;

	public static final String TEXT = "text";
	public static final String TOP_MARGIN = "topMargin";
	public static final String LEFT_MARGIN = "leftMargin";
	public static final String FOREGROUND = "foreground";
	public static final String BACKGROUND = "background";
	public static final String TEXT_CASE = "textCase";
	public static final String FONT = "font";
	
	static final long serialVersionUID = -6141230100503340864L;
	
	// Instance variables
	// The instance variables for font, foreground and background color
	// are inherited from Canvas.
	protected String text = "default text";
	protected int topMargin = 4;
	protected int leftMargin = 4;
	protected int textCase = AS_IS;

	protected PropertyChangeSupport propertyListenerSupport;
	protected VetoableChangeSupport vetoListenerSupport;

	// Constructor
	// All beans must have a no-argument constructor.
	public CaseAwareTextDisplay() {
		this.setSize(180, 30);
		propertyListenerSupport = new PropertyChangeSupport(this);
		vetoListenerSupport = new VetoableChangeSupport(this);
	}

	// Getters and setters.
	// All properties (except font - which is inherited from Canvas)
	// are bound. Foreground and background color are both bound
	// and constrained.
	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldValue = this.text;
		this.text = text;
		firePropertyChange(TEXT, oldValue, this.text);
	}

	public int getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(int pixels) {
		int oldValue = this.topMargin;
		this.topMargin = pixels;
		firePropertyChange(
			TOP_MARGIN,
			new Integer(oldValue),
			new Integer(this.topMargin));
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(int pixels) {
		int oldValue = this.leftMargin;
		this.leftMargin = pixels;
		firePropertyChange(
			LEFT_MARGIN,
			new Integer(oldValue),
			new Integer(this.leftMargin));
	}

	public void setForeground(Color newColor) {
		Color oldValue = getForeground();
		try {
			fireVetoableChange(FOREGROUND, oldValue, newColor);
			// Set new color only when change not vetoed.
			super.setForeground(newColor);
			// Inform bound beans of property change
			firePropertyChange(FOREGROUND, oldValue, newColor);
			repaint();
		} catch (PropertyVetoException e) {
		}
	}

	public void setBackground(Color newColor) {
		Color oldValue = getBackground();
		try {
			fireVetoableChange(BACKGROUND, oldValue, newColor);
			// Set new color only when change not vetoed.
			super.setBackground(newColor);
			// Inform bound beans of property change
			firePropertyChange(BACKGROUND, oldValue, newColor);
			repaint();
		} catch (PropertyVetoException e) {
		}
	}

	public int getTextCase() {
		return textCase;
	}

	public void setTextCase(int textCase) {
		int oldValue = this.textCase;
		this.textCase = textCase;
		firePropertyChange(
			TEXT_CASE,
			new Integer(oldValue),
			new Integer(this.textCase));
		repaint();
	}

	// Paint method overrides paint in Canvas.
	public void paint(Graphics g) {
		// Convert the text to the proper case
		String convertedText = getText();
		if (convertedText == null)
			convertedText = " ";

		switch (textCase) {
			case UPPERCASE :
				convertedText = convertedText.toUpperCase();
				break;
			case LOWERCASE :
				convertedText = convertedText.toLowerCase();
				break;
			case FIRST_IN_CAPS :
				convertedText = convertedText.toLowerCase();
				char[] temp = convertedText.toCharArray();
				char previous = ' ';
				for (int i = 0; i < temp.length; i++) {
					if (previous == ' ')
						temp[i] = Character.toUpperCase(temp[i]);
					previous = temp[i];
				}
				convertedText = new String(temp);
				break;
			case AS_IS :
			default :
				// Text should be explicitly left as is, or the value for
				// the property is unknown, so make no change to the case
				// of the text.
		}

		// Paint the text
		Rectangle r = getBounds();

		Font font = getFont();
		if (font == null)
			font = new Font("Dialog", Font.PLAIN, 12);
		// FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        FontMetrics fm = javax.swing.text.StyleContext.getDefaultStyleContext ().getFontMetrics (font);

		int x = leftMargin;
		int y = topMargin + fm.getAscent();
		
		Color color = getBackground() == null?Color.white:getBackground();
		g.setColor(color);
		g.fillRect(0, 0, r.width, r.height);
		// g.clearRect(r.x, r.y, r.width, r.height);

		color = getForeground() == null?Color.black:getForeground();
		g.setColor(color);
		g.setFont(getFont());
		g.drawString(convertedText, x, y);
	}

	// Methods to support bound properties.
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyListenerSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyListenerSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(
		String propertyName,
		Object oldValue,
		Object newValue) {
		propertyListenerSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	// Methods to support constrained properties.
	public void addVetoableChangeListener(VetoableChangeListener vetoListener) {
		vetoListenerSupport.addVetoableChangeListener(vetoListener);
	}

	public void removeVetoableChangeListener(VetoableChangeListener vetoListener) {
		vetoListenerSupport.removeVetoableChangeListener(vetoListener);
	}

	protected void fireVetoableChange(
		String propertyName,
		Object oldValue,
		Object newValue)
		throws PropertyVetoException {
		vetoListenerSupport.fireVetoableChange(propertyName, oldValue, newValue);
	}
}