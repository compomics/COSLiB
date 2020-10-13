package com.compomics.coslib.controller;

import com.compomics.ms2io.controller.Indexer;
import com.compomics.ms2io.controller.MgfReader;
import com.compomics.ms2io.controller.SpectraReader;
import com.compomics.ms2io.model.IndexKey;
import com.compomics.ms2io.model.Modification;
import com.compomics.ms2io.model.Spectrum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This callable class creates list of spectra for a given ionbot result file
 * and corresponding mgf file and return to the caller
 *
 * @author Genet
 */
public class GetIdentifiedSpectra implements Callable<ArrayList<Spectrum>> {

    final File ionbot_result;
    final File mgfFile;

    public GetIdentifiedSpectra(File ionbotfile, File mgfile) {
        this.ionbot_result = ionbotfile;
        this.mgfFile = mgfile;
    }

    @Override
    public ArrayList<Spectrum> call() {
        ArrayList<Spectrum> identifiedSpecs = new ArrayList<>();

        try {
            //read file here and instantiate indexer 
            ArrayList<IndexKey> fileIndex = new ArrayList<>();
            Indexer giExp = new Indexer(this.mgfFile);
            
            //Generate index and add to fileIndex
            fileIndex.addAll(giExp.generate());

            //sort the index based on scan number in accending order
            fileIndex.sort((spec1, spec2) -> spec1.getScanNum().compareTo(spec2.getScanNum()));

            SpectraReader specReader = null;
            //create object to spectrum reader ms2io
            if (this.mgfFile.getName().endsWith("mgf")) {
                
                specReader = new MgfReader(mgfFile, fileIndex);
                
            } else if (this.mgfFile.getName().endsWith("msp")) {

            }

            String line;

            String[] psm;
            int scannum;
            BufferedReader br = new BufferedReader(new FileReader(this.ionbot_result));

            //reading the column name and do nothing
            line = br.readLine();
            int newLoc = 0;
            int num_spectra = fileIndex.size();
            Spectrum current_spec;
            Modification mod;
            int modPos;
            String modified_aa;
            String modName;
            String unimode;

            ArrayList<Modification> mods;
            String modification;
            String[] modMain;
            String[] temp;
            int len_temp;
            int count =0;
            while ((line = br.readLine()) != null && specReader != null) {
                count++;
                
                mods = new ArrayList<>();
                psm = line.split(",");
                scannum = Integer.parseInt(psm[1]);
                for (int i = newLoc; i < num_spectra; i++) {
                    current_spec = specReader.readAt(fileIndex.get(i).getPos());
                    if (scannum == Integer.parseInt(current_spec.getScanNumber())) {
                        newLoc = i + 1;

                        //set spectrum protein match and modification
                        current_spec.setSequence(psm[4]);
                        current_spec.setProtein(psm[30]);
                        modification = psm[5];

                        //if modification present
                        if (!modification.isEmpty()) {
                            temp = modification.split("|");
                            len_temp = temp.length;

                            //loop for the number of modification specified in the result
                            for (int k = 0; k < len_temp - 1;) {
                                modPos = Integer.parseInt(temp[k]);
                                modified_aa = current_spec.getSequence().substring(modPos);
                                modMain = temp[++k].split("][");
                                unimode = modMain[0];
                                modName = modMain[1];
                                mod = new Modification(modPos, modified_aa, modName, unimode);
                                mods.add(mod);
                                k++;
                            }

                        }
                        current_spec.setModification(mods);
                        identifiedSpecs.add(current_spec);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            
            e.printStackTrace();
            
        }

        return identifiedSpecs;
    }

}
