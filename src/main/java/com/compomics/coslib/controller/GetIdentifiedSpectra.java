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
import java.util.ArrayList;
import java.util.Optional;
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

        int count = 0;
        try {
            //read file here and instantiate indexer 
            ArrayList<IndexKey> fileIndex = new ArrayList<>();
            Indexer giExp = new Indexer(this.mgfFile);

            //Generate index and add to fileIndex
            fileIndex.addAll(giExp.generate());

            //sort the index based on scan number in accending order
            // fileIndex.sort((spec1, spec2) -> spec1.getScanNum().compareTo(spec2.getScanNum()));
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
            Spectrum matched_spec=null;
            Modification mod;
            int modPos;
            char modified_aa;
            String modName;
            String unimode;

            String sequence;
            int seqLen;
            String protein;
            ArrayList<Modification> mods;
            String modification;
            String[] modMain;
            String[] temp;
            
            int len_temp;

            if(specReader == null){
                return null;
            }
            
            while ((line = br.readLine()) != null) {
                count++;

                mods = new ArrayList<>();
                psm = line.split(",");
                sequence = psm[4];
                seqLen=sequence.length();
                protein = psm[30];
                scannum = Integer.parseInt(psm[1]);
                modification = psm[5];

                //if modification present
                if (!modification.isEmpty()) {
                    temp = modification.split("\\|");
                    len_temp = temp.length;

                    //loop for the number of modification specified in the result
                    for (int k = 0; k < len_temp - 1;) {
                        modPos = Integer.parseInt(temp[k]);
                        if (modPos != 0) {
                            modPos--;//change index to zero based
                        } else {// value of 0 is for N-term modification and already 0

                        }

                        if(modPos > seqLen-1){
                            //this is for the case of C-Terminal modification
                            //where modified aa position given outside of the index
                            modPos=seqLen-1;
                        }
                        modified_aa = sequence.charAt(modPos);
                        String delimiter = "\\s+|]\\s*|\\[\\s*";
                        modMain = temp[++k].split(delimiter);
                        if(modMain.length>1){
                            unimode = modMain[1];
                            modName = modMain[2];
                        }else{
                            modName = modMain[0];
                            unimode="";
                        }
                        
                        mod = new Modification(modPos, modified_aa, modName, unimode);
                        mods.add(mod);
                        k++;
                    }

                }

                for (int i = newLoc; i < num_spectra; i++) {
                    matched_spec = specReader.readAt(fileIndex.get(i).getPos());
                    
                    if (Integer.parseInt(matched_spec.getScanNumber()) ==  scannum){
                        //set spectrum protein match and modification
                        matched_spec.setSequence(sequence);
                        matched_spec.setProtein(protein);
                        matched_spec.setModification(mods);
                        newLoc=i;
                        break;
                    }

                }
                
                //get matched spectrum from list of spectra in mgf file
                // Spectrum s=fileIndex.get
                
//               matched_spec = getSpectrum(fileIndex, scannum, specReader);

//                //set spectrum protein match and modification
//                matched_spec.setSequence(sequence);
//                matched_spec.setProtein(protein);
//                matched_spec.setModification(mods);

                //add spectrum to list of identified spectra 
                identifiedSpecs.add(matched_spec);

            }

        } catch (Exception e) {

            System.out.println(e.toString() + ", this is the index: " + Integer.toString(count));

        }

        return identifiedSpecs;
    }

    private Spectrum getSpectrum(ArrayList<IndexKey> indxlist, int scannum, SpectraReader rd) {

        Optional<IndexKey> matchingObject = indxlist.stream().
                filter(p -> p.getScanNum().equals(scannum)).
                findFirst();

        
        Spectrum spec = rd.readAt(matchingObject.get().getPos());

        return spec;
    }

}
