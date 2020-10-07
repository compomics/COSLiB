package com.compomics.coslib.model;

import java.io.File;
import com.compomics.ms2io.model.IndexKey;
import java.util.ArrayList;

/**
 * This class holds the information for the data used to create the library
 *
 * @author Genet
 */
public class Data {

    /* inputFile: spectral file to be used to create library
  outputFile: library file to be created
  Precursor tolerance
  Fragment tolerance
  Index; indexed spectra created from inputFile*/
    
    final File inputFile;
    final File outputFile;   
    final double precursorTolerance;
    final double fragmentTolerance;
    ArrayList<IndexKey> ipIndex;
    
    

    /**
     * *
     * Constructor of this class
     *
     * @param ipfile input spectral file
     * @param opFile library to be created
     * @param pctolerance Precursor tolerance
     * @param fragtolerance Fragment tolerance
     */
    public Data(File ipfile, File opFile, double pctolerance, double fragtolerance) {
        this.inputFile = ipfile;
        this.outputFile = opFile;
        this.precursorTolerance = pctolerance;
        this.fragmentTolerance = fragtolerance;
    }

    /**
     * *
     * getter for input spectral file
     *
     * @return input file
     */
    public File getInputFile() {
        return this.inputFile;
    }

    /**
     * *
     * getter for output library file
     *
     * @return library file to be created
     */
    public File getOuFile() {
        return this.outputFile;
    }

    /**
     * *
     * setter for index for spectra from input file
     *
     * @param index
     */
    public void setIndex(ArrayList<IndexKey> index) {
        this.ipIndex = index;

    }

    /**
     * *
     * getter for spectra index
     *
     * @return index of the spectra file
     */
    public ArrayList<IndexKey> getIndex() {
        return this.ipIndex;
    }
    
    

    /**
     * *
     * getter for precursor tolerance
     *
     * @return precursor tolerance
     */
    public double getPrecursorTolerance() {
        return this.precursorTolerance;
    }

    /**
     * *
     * getter for fragment tolerance
     *
     * @return fragment tolerance
     */
    public double getFragmentTolerance() {
        return this.fragmentTolerance;
    }

}
