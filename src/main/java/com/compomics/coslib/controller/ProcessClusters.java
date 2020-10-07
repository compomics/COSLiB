package com.compomics.coslib.controller;

import com.compomics.ms2io.model.Spectrum;
import java.util.ArrayList;

/**
 *
 * @author Genet
 */
public class ProcessClusters implements Runnable{

    ArrayList<ArrayList<Spectrum>> clusteredSimilarSpecs;
    
    private void DropDuplicates(){
        CalculatePeakOccurence();
        //To do ... check for same spectrum and drop
        // droping spectra occures after calculated peak occurence in each given list of specta
        //first peaks with small occurence value (<0.3) and smaller than mz value of x and intensity y are removed
        //
        
    }
    
    private void CalculatePeakOccurence(){
        
    }
    
    private void CalculateMAD(){
        
    }
    
    private void WritetoFile(){
        
    }
    
    
    
    @Override
    public void run() {
        
        DropDuplicates();
        CalculateMAD();
        WritetoFile();
        
    }
    
    
}
