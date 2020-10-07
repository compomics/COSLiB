package com.compomics.coslib.controller;

import com.compomics.coslib.model.Data;
import com.compomics.ms2io.controller.Indexer;
import com.compomics.ms2io.model.IndexKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Genet
 */
public class IndexSpectra {

    final Data data;
    

    public IndexSpectra(Data d) {
        this.data = d;

    }

    public void ReadData() {
        try {
            //read indexed data here
            Indexer giExp = new Indexer(this.data.getInputFile());
            if(this.data.getInputFile().getName().endsWith("mgf")){
                
            }else if(this.data.getInputFile().getName().endsWith("msp")){
                
            }
                
            
            this.data.setIndex((ArrayList<IndexKey>) giExp.generate());
            
            //sort the index precursor mass accending order
            SortIndex();
        } catch (IOException ex) {
            Logger.getLogger(IndexSpectra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SortIndex() {       
        //Sort indexed input spectra, precursor mass accending order
        
        Collections.sort(this.data.getIndex());
        

    }

}
