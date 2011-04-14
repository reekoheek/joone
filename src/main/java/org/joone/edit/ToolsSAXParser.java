package org.joone.edit;

import java.io.*;
import java.util.*;

import org.joone.log.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
/** Read an XML file containing the settings wrote in the following form:
 * <SETTING_NAME param1name="param1value" ... paramNname="paramNvalue"/>
 * All the settings are returned within a vector of ToolElement objetcs
 * @author: pmarrone
 */

public class ToolsSAXParser extends DefaultHandler
{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (ToolsSAXParser.class);

    protected Vector elements;

/**
 * ToolsSAXParser constructor.
 */
    public ToolsSAXParser(InputStream xmlFile) {
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFile, this);

        } catch (Throwable t)
        {
            log.error ( "Problem while parsing the XML document with the SAXParser",
                        t);
        }
    }

    public void startElement (String name, String sName, String qName, Attributes attrs)
    throws SAXException
    {
        String eName = sName;
        if ("".equals(eName)) eName = qName;
        ToolElement te = new ToolElement(eName);
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength (); i++) {
                String aName = attrs.getLocalName(i);
                if ("".equals(aName)) aName = attrs.getQName(i);
                te.setParam(aName, attrs.getValue(i));
            }

        //log.debug ("<"+eName+"/>");
        }
        elements.addElement(te);
    }

    public Vector getElements() {
        return elements;
    }

    public void startDocument() throws SAXException {
        elements = new Vector();
    }

}
