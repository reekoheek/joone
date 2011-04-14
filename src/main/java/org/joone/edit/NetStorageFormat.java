package org.joone.edit;

import javax.swing.filechooser.FileFilter;
import java.io.IOException;
import org.joone.net.NeuralNet;

/**
 * Interface to define a storage format. A StorageFormat is a strategy that knows how to store
 * and restore a NeuralNet according to a specific encoding. Typically it can be recognized by
 * a file extension. To identify a valid file format for a NeuralNet an appropriate FileFilter
 * for a javax.swing.JFileChooser component can be requested.
 *
 * @see NeuralNet
 * @see StorageFormatManager
 * @author Wolfram Kaiser
 */
public interface NetStorageFormat {

	/**
	 * Return a FileFilter that can be used to identify files which can be stored and restored
	 * with this Storage Format. Typically, each storage format has its own recognizable file
	 * extension.
	 *
	 * @return FileFilter to be used with a javax.swing.JFileChooser
	 */
	public FileFilter getFileFilter();
	
	/**
	 * Store a NeuralNet under a given name.
	 *
	 * @param fileName file name of the NeuralNet under which it should be stored
	 * @param saveNeuralNet NeuralNet to be saved
	 * @return file name with correct file extension
	 */
	public String store(String fileName, NeuralNet saveNet) throws IOException;
	
	/**
	 * Restore a NeuralNet from a file with a given name. 
	 *
	 * @param name of the file in which the NeuralNet has been saved
	 * @return restored NeuralNet
	 */
	public NeuralNet restore(String fileName) throws IOException;
}
