/*
 * TimeSeriesForecasting.java
 *
 * Created on 10 giugno 2005, 21.31
 */

package org.joone.samples.engine.timeseries;

import java.io.File;
import org.joone.engine.DelayLayer;
import org.joone.engine.FullSynapse;
import org.joone.engine.Layer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.Synapse;
import org.joone.engine.TanhLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.net.NeuralNet;

/**
 *
 * @author paolo
 */
public class TimeSeriesForecasting implements NeuralNetListener {
    static String path = "org/joone/samples/engine/timeseries/";
    static String fileName = path+"timeseries.txt";
    
    int trainingPatterns = 300;
    int epochs = 1000;
    int temporalWindow = 10;
    
    DelayLayer input;
    SigmoidLayer hidden;
    TanhLayer output;
    TeachingSynapse trainer;
    NeuralNet nnet;
    
    /** Creates a new instance of TimeSeriesForecasting */
    public TimeSeriesForecasting() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TimeSeriesForecasting me = new TimeSeriesForecasting();
        me.createNet();
        System.out.println("Training...");
        me.train();
        me.interrogate(path+"results1.txt");
        me.interrogate(path+"results2.txt");
        System.out.println("Done.");
    }
    
    private void createNet() {
        input = new DelayLayer();
        hidden = new SigmoidLayer();
        output = new TanhLayer();
        
        input.setTaps(temporalWindow-1);
        input.setRows(1);
        hidden.setRows(15);
        output.setRows(1);
        
        connect(input, new FullSynapse(), hidden);
        connect(hidden, new FullSynapse(), output);
        
        input.addInputSynapse(createDataSet(fileName, 1, trainingPatterns, "1"));
        
        trainer = new TeachingSynapse();
        trainer.setDesired(createDataSet(fileName, 2, trainingPatterns+1, "1"));
        
        output.addOutputSynapse(trainer);
        
        nnet = new NeuralNet();
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
    }
    
    private void train() {
        Monitor mon = nnet.getMonitor();
        mon.setLearningRate(0.2);
        mon.setMomentum(0.7);
        mon.setTrainingPatterns(trainingPatterns);
        mon.setTotCicles(epochs);
        mon.setPreLearning(temporalWindow);
        mon.setLearning(true);
        mon.addNeuralNetListener(this);
        nnet.start();
        mon.Go();
        nnet.join();
    }
    
    private void interrogate(String outputFile) {
        Monitor mon = nnet.getMonitor();
        input.removeAllInputs();
        int startRow = trainingPatterns - temporalWindow;
        input.addInputSynapse(createDataSet(fileName, startRow+1, startRow+20, "1"));
        output.removeAllOutputs();
        FileOutputSynapse fOutput = new FileOutputSynapse();
        fOutput.setFileName(outputFile);
        output.addOutputSynapse(fOutput);
        mon.setTrainingPatterns(20);
        mon.setTotCicles(1);
        mon.setLearning(false);
        nnet.start();
        mon.Go();
        nnet.join();
    }
    
    private void connect(Layer layer1, Synapse syn, Layer layer2) {
        layer1.addOutputSynapse(syn);
        layer2.addInputSynapse(syn);
    }
    
    private FileInputSynapse createDataSet(String fileName, int firstRow, int lastRow, String advColSel) {
        FileInputSynapse fInput = new FileInputSynapse();
        fInput.setInputFile(new File(fileName));
        fInput.setFirstRow(firstRow);
        fInput.setLastRow(lastRow);
        fInput.setAdvancedColumnSelector(advColSel);
        return fInput;
    }
    
    public void cicleTerminated(org.joone.engine.NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        int epoch = mon.getTotCicles() - mon.getCurrentCicle();
        if ((epoch > 0) && ((epoch % 100) == 0)) {
            System.out.println("Epoch:"+epoch+" RMSE="+mon.getGlobalError());
        }
    }
    
    public void errorChanged(org.joone.engine.NeuralNetEvent e) {
    }
    
    public void netStarted(org.joone.engine.NeuralNetEvent e) {
    }
    
    public void netStopped(org.joone.engine.NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        if (mon.isLearning()) {
            int epoch = mon.getTotCicles() - mon.getCurrentCicle();
            System.out.println("Epoch:"+epoch+" last RMSE="+mon.getGlobalError());
        }
    }
    
    public void netStoppedError(org.joone.engine.NeuralNetEvent e, String error) {
    }
}
