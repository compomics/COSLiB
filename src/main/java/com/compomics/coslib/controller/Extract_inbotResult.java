package com.compomics.coslib.controller;

import com.compomics.ms2io.controller.MgfWriter;
import com.compomics.ms2io.controller.SpectraWriter;
import com.compomics.ms2io.model.Spectrum;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Genet
 */
public class Extract_inbotResult {

    final String result_dir;

    public Extract_inbotResult(String directory) {
        this.result_dir = directory;

    }

    public void Extract() {

        File directory = new File(this.result_dir);
        File[] files = directory.listFiles();
        
        //2 files will be taken at a time: ionbot csv file and mgf file
        int num_files = files.length/2;
        
        //create threads for each task
        ExecutorService execs = Executors.newFixedThreadPool(num_files);
        ArrayList<Spectrum> extractedSpecs;
        //SpectraWriter[] wr=new SpectraWriter[num_files];
        ArrayList<SpectraWriter> wrs=new ArrayList<>();
        
        for (int f = 0; f < num_files; f++) {
            try {
                File mgffile = files[f];
                File ionbot_result  = files[++f];
                File outputfile= new File(this.result_dir+ "/" +FilenameUtils.removeExtension(ionbot_result.getName()) +".mgf");
                
                GetIdentifiedSpectra identifiy= new GetIdentifiedSpectra(ionbot_result, mgffile);
                Future future = execs.submit(identifiy);
                extractedSpecs=(ArrayList<Spectrum>)future.get();
                
                //instantiate mgf writer with the output file and result list of spectra
                SpectraWriter wr=new MgfWriter(outputfile,extractedSpecs);
                
                //write to the file
                wr.write(); 
                wrs.add(wr);
               
            } catch (InterruptedException ex) {
                Logger.getLogger(Extract_inbotResult.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Extract_inbotResult.class.getName()).log(Level.SEVERE, null, ex);
            }finally{                
                for(SpectraWriter wr:wrs){
                     wr.closeWriter();
                }        
                
            }
        }
        execs.shutdown();
    }

}
