
package org.joone.edit;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import org.joone.net.NeuralNet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A XMLNetStorageFormat is a XML based file format to store and restore
 * NeuralNets. It uses XStream serialization mechanism to store NeuralNets.
 * The XMLNetStorageFormat has the file extension
 * "xml" (e.g. my_net.xml).
 *
 */
public class XMLNetStorageFormat implements NetStorageFormat {
    
    /**
     * FileFilter for a javax.swing.JFileChooser which recognizes files with the
     * extension ".xml"
     */
    private FileFilter myFileFilter;
    
    /**
     * File extension
     */
    private String myFileExtension;
    
    /**
     * Description of the file type when displaying the FileFilter
     */
    private String myFileDescription;
    
    /**
     * Create a StandardNetStorageFormat for storing and restoring NeuralNets.
     */
    public XMLNetStorageFormat() {
        setFileExtension(createFileExtension());
        setFileDescription(createFileDescription());
        setFileFilter(createFileFilter());
    }
    
    /**
     * Factory method to create the file extension recognized by the FileFilter for this
     * StandardStorageFormat. The XMLNetStorageFormat has the file extension "xml"
     * (e.g. my_net.xml).
     *
     * @return new file extension
     */
    protected String createFileExtension() {
        return myFileExtension = "xml";
    }
    
    /**
     * Set the file extension for the storage format
     *
     * @param file extension
     */
    public void setFileExtension(String newFileExtension) {
        myFileExtension = newFileExtension;
    }
    
    /**
     * Return the file extension for the storage format
     *
     * @return file extension
     */
    public String getFileExtension() {
        return myFileExtension;
    }
    
    /**
     * Factory method to create a file description for the file type when displaying the
     * associated FileFilter.
     *
     * @return new file description
     */
    public String createFileDescription() {
        return "XML Serialized NeuralNet (" + getFileExtension() + ")";
    }
    
    /**
     * Set the file description for the file type of the storage format
     *
     * @param newFileDescription description of the file type
     */
    public void setFileDescription(String newFileDescription) {
        myFileDescription = newFileDescription;
    }
    
    /**
     * Return the file description for the file type of the storage format
     *
     * @return description of the file type
     */
    public String getFileDescription() {
        return myFileDescription;
    }
    
    /**
     * Factory method to create a FileFilter that accepts file with the appropriate
     * file exention used by a javax.swing.JFileChooser. Subclasses can override this
     * method to provide their own file filters.
     *
     * @return FileFilter for this StorageFormat
     */
    protected FileFilter createFileFilter() {
        return new FileFilter() {
            public boolean accept(File checkFile) {
                // still display directories for navigation
                if (checkFile.isDirectory()) {
                    return true;
                } else {
                    return checkFile.getName().endsWith("." + myFileExtension);
                }
            }
            
            public String getDescription() {
                return getFileDescription();
            }
        };
    }
    
    /**
     * Set the FileFilter used to identify NeuralNet files with the correct file
     * extension for this NetStorageFormat.
     *
     * @param newFileFilter FileFilter for this StorageFormat
     */
    public void setFileFilter(FileFilter newFileFilter) {
        myFileFilter = newFileFilter;
    }
    
    /**
     * Return the FileFilter used to identify NeuralNet files with the correct file
     * extension for this NetStorageFormat.
     *
     * @return FileFilter for this StorageFormat
     */
    public FileFilter getFileFilter() {
        return myFileFilter;
    }
    
    /**
     * Store a NeuralNet under a given name. If the file name does not have the correct
     * file extension, then the file extension is added.
     *
     * @param fileName file name of the NeuralNet under which it should be stored
     * @param saveNeuralNet NeuralNet to be saved
     * @return file name with correct file extension
     */
    public String store(String fileName, NeuralNet saveNeuralNet) throws IOException {
        FileOutputStream stream = new FileOutputStream(adjustFileName(fileName));
        XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library
        String xml = xstream.toXML(saveNeuralNet);
        xml = "<?xml version=\"1.0\"?>\n" + "<!-- XML Neural Network - Joone v."+NeuralNet.getVersion()+"-->\n" + xml;
        stream.write(xml.getBytes());
        stream.close();
        return adjustFileName(fileName);
    }
    
    /**
     * Restore a NeuralNet from a file with a given name.
     *
     * @param name of the file in which the NeuralNet has been saved
     * @return restored NeuralNet
     */
    public NeuralNet restore(String fileName) throws IOException {
        try {
            FileInputStream stream = new FileInputStream(fileName);
            ObjectInput input = new ObjectInputStream(stream);
            return (NeuralNet)input.readObject();
        } catch (ClassNotFoundException exception) {
            throw new IOException("Could not restore NeuralNet '" + fileName +"': class not found!");
        }
    }
    
    /**
     * Test, whether two NetStorageFormats are the same. They are the same if they both support the
     * same file extension.
     *
     * @return true, if both StorageFormats have the same file extension, false otherwise
     */
    public boolean equals(Object compareObject) {
        if (compareObject instanceof XMLNetStorageFormat) {
            return getFileExtension().equals(((XMLNetStorageFormat)compareObject).getFileExtension());
        } else {
            return false;
        }
    }
    
    /**
     * Adjust a file name to have the correct file extension.
     *
     * @param testFileName file name to be tested for a correct file extension
     * @return testFileName + file extension if necessary
     */
    protected String adjustFileName(String testFileName) {
        if (!hasCorrectFileExtension(testFileName)) {
            return testFileName + "." + getFileExtension();
        } else {
            return testFileName;
        }
    }
    
    /**
     * Test whether the file name has the correct file extension
     *
     * @return true, if the file has the correct extension, false otherwise
     */
    protected boolean hasCorrectFileExtension(String testFileName) {
        return testFileName.endsWith("." + getFileExtension());
    }
    
    
}