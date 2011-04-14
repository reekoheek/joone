/*
 * TestXML.java
 *
 * Created on 28 aprile 2004, 22.17
 */

package org.joone.samples.engine.xml;

import org.joone.net.*;
import org.joone.engine.*;
import org.joone.io.*;
import java.io.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author  paolo
 */
public class TestXML implements NeuralNetListener, NeuralValidationListener {
    NeuralNet result;
    /** Creates a new instance of TestXML */
    public TestXML() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestXML().elaborate();
    }
    
    private void elaborate() {
        try {
            XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library
//            result = (NeuralNet)xstream.fromXML(new FileReader("/home/paolo/SourceForge/ValidationSample.xml"));
            result = (NeuralNet)xstream.fromXML(new FileReader("/home/paolo/SourceForge/xor.xml"));
            System.out.println("Unmarshaling Done");
            
            result.getMonitor().addNeuralNetListener(this);
            result.getMonitor().setTotCicles(3000);
            result.randomize(0.2);
            result.start();
            result.getMonitor().Go();
            
        } catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
    }
    
    public void cicleTerminated(NeuralNetEvent e) {
    }
    
    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        if ((mon.getCurrentCicle() % 200) == 0) {
            System.out.println("RMSE at epoch "+(mon.getTotCicles()-mon.getCurrentCicle())+": "+mon.getGlobalError());
            if (mon.getValidationPatterns() > 0) {
                NeuralNet newNet = result.cloneNet();
                newNet.removeAllListeners();
                NeuralNetValidator val = new NeuralNetValidator(newNet);
                val.addValidationListener(this);
                val.start();
            }
        }
    }
    
    public void netStarted(NeuralNetEvent e) {
        System.out.println("Running...");
    }
    
    public void netStopped(NeuralNetEvent e) {
        System.out.println("Finished");
    }
    
    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    
    public void netValidated(NeuralValidationEvent e) {
        // Shows the RMSE at the end of the cycle
        NeuralNet NN = (NeuralNet)e.getSource();
        System.out.println("\tValidation error="+NN.getMonitor().getGlobalError());
    }
    
}
