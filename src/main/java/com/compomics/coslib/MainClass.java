package com.compomics.coslib;

import com.compomics.coslib.controller.BuildLibrary;
import com.compomics.coslib.controller.IndexSpectra;
import java.io.File;
import com.compomics.coslib.model.Data;

/**
 *
 * @author Genet
 */
public class MainClass {

    public static void main(String[] args) {

        File fI = new File("");
        File fO = new File("");
        double precTolerance = 0;
        double fragTolerance = 0;
        Data d = new Data(fI, fO, precTolerance, fragTolerance);
        
        IndexSpectra spec = new IndexSpectra(d);
        spec.ReadData();
        
        BuildLibrary build = new BuildLibrary(d);
        build.Start();

  

        
        
        

    }

}
