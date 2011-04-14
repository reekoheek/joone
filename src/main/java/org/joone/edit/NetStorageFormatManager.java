 
package org.joone.edit;

import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * The NetStorageFormatManager is a contains NetStorageFormats.
 * It is not a Singleton because it could be necessary to deal with different
 * format managers, e.g. one for importing NeuralNets, one for exporting NeuralNets.
 * If one NetStorageFormat matches the file extension of the NeuralNet file, then this
 * NetStorageFormat can be used to store or restore the NeuralNet.
 *
 * @see StorageFormat
 */
public class NetStorageFormatManager {

	/**
	 * Vector containing all registered storage formats
	 */
	private Vector myStorageFormats;
	
	/**
	 * Default storage format that should be selected in a javax.swing.JFileChooser
	 */
	private NetStorageFormat myDefaultStorageFormat;
	
	/**
	 * Create a new NetStorageFormatManager.
	 */
	public NetStorageFormatManager() {
		myStorageFormats = new Vector();
	}
	
	/**
	 * Add a NetStorageFormat that should be supported by this NetStorageFormatManager.
	 *
	 * @param newStorageFormat new NetStorageFormat to be supported
	 */
	public void addStorageFormat(NetStorageFormat newStorageFormat) {
		myStorageFormats.add(newStorageFormat);
	}

	/**
	 * Remove a StorageFormat that should no longer be supported by this StorageFormatManager.
	 * The StorageFormat is excluded in when search for a StorageFormat.
	 *
	 * @param oldStorageFormat old NetStorageFormat no longer to be supported
	 */
	public void removeStorageFormat(NetStorageFormat oldStorageFormat) {
		myStorageFormats.remove(oldStorageFormat);
	}
	
	/**
	 * Test, whether a NetStorageFormat is supported by this StorageFormat
	 */
	public boolean containsStorageFormat(NetStorageFormat checkStorageFormat){
		return myStorageFormats.contains(checkStorageFormat);
	}
	
	/**
	 * Set a NetStorageFormat as the default storage format which is selected in a
	 * javax.swing.JFileChooser. The default storage format must be already
	 * added with addStorageFormat. Setting the default storage format to null
	 * does not automatically remove the StorageFormat from the list of
	 * supported StorageFormats.
	 *
	 * @param newDefaultStorageFormat StorageFormat that should be selected in a JFileChooser
	 */
	public void setDefaultStorageFormat(NetStorageFormat newDefaultStorageFormat) {
		myDefaultStorageFormat = newDefaultStorageFormat;
	}
	
	/**
	 * Return the NetStorageFormat which is used as selected file format in a javax.swing.JFileChooser
	 *
	 * @return default storage format
	 */
	public NetStorageFormat getDefaultStorageFormat() {
		return myDefaultStorageFormat;
	}
	
	/**
	 * Register all FileFilters supported by NetStorageFormats
	 *
	 * @param fileChooser javax.swing.JFileChooser to which FileFilters are added
	 */
	public void registerFileFilters(JFileChooser fileChooser) {
		Iterator formatsIterator = myStorageFormats.iterator();
		while (formatsIterator.hasNext()) {
			fileChooser.addChoosableFileFilter(((NetStorageFormat)formatsIterator.next()).getFileFilter());
		}

		// set a current activated file filter if a default storage Format has been defined
		if (getDefaultStorageFormat() != null) {
			fileChooser.setFileFilter(getDefaultStorageFormat().getFileFilter());
		}
	}

	/**
	 * Find a NetStorageFormat that can be used according to a FileFilter to store a NeuralNet
	 * in a file or restore it from a file respectively.
	 *
	 * @param findFileFilter FileFilter used to identify a NetStorageFormat
	 * @return NetStorageFormat, if a matching file extension could be found, false otherwise
	 */
	public NetStorageFormat findStorageFormat(FileFilter findFileFilter) {
		Iterator formatsIterator = myStorageFormats.iterator();
		NetStorageFormat currentStorageFormat = null;
		while (formatsIterator.hasNext()) {
			currentStorageFormat = (NetStorageFormat)formatsIterator.next();
			if (currentStorageFormat.getFileFilter().equals(findFileFilter)) {
				return currentStorageFormat;
			}
		}
		
		return null;
	}
}