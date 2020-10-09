package com.compomics.coslib.controller;

import com.compomics.ms2io.controller.MgfWriter;
import com.compomics.ms2io.controller.SpectraWriter;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        
        for (int f = 0; f < num_files; f++) {
            File ionbot_result = files[f];
            File mgffile = files[++f];
            File outputfile= new File(this.result_dir+ "/" +FilenameUtils.removeExtension(ionbot_result.getName()) +".mgf");
            
            GetIdentifiedSpectra identifiy= new GetIdentifiedSpectra(ionbot_result, mgffile);
            
            //instantiate mgf writer with the output file and result list of spectra
            SpectraWriter wr=new MgfWriter(outputfile, execs.submit(identifiy));
            
            //write to the file
            wr.write();
            wr.closeWriter();  
        }
    }

}
