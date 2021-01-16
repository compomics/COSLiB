package com.compomics.coslib.controller;

import com.compomics.ms2io.controller.Indexer;
import com.compomics.ms2io.controller.MgfReader;
import com.compomics.ms2io.controller.MgfWriter;
import com.compomics.ms2io.controller.SpectraReader;
import com.compomics.ms2io.controller.SpectraWriter;
import com.compomics.ms2io.model.IndexKey;
import com.compomics.ms2io.model.Modification;
import com.compomics.ms2io.model.Spectrum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

/**
 * This callable class creates list of spectra for a given ionbot result file
 * and return to the caller
 *
 * @author Genet
 */
public class GetIdentifiedSpectra implements Runnable {

    final File ionbot_result;
    final File mgfFile;
    final SpectraWriter wr;
    final File outputfile;
    final String folder;
    final BufferedWriter bwr_log;

    public GetIdentifiedSpectra(File ionbotfile, File mgfile, String foldername, BufferedWriter bwrlog) {
        this.ionbot_result = ionbotfile;
        this.mgfFile = mgfile;
        this.folder = foldername;
        outputfile = new File(foldername + "/" + FilenameUtils.removeExtension(this.ionbot_result.getName()) + ".mgf");
        this.wr = new MgfWriter(outputfile);
        this.bwr_log = bwrlog;
    }

    @Override
    public void run() {
        int index_count = 0;
        String line = "";

        int spec_count = 0;
        int ionbot_count = 0;
        BufferedReader br = null;

        try {
            //read file here and instantiate indexer 
            ArrayList<IndexKey> fileIndex = new ArrayList<>();

//             Map<Integer, IndexKey> result1 = list.stream().collect(
//                Collectors.toMap(fileIndex.ge, Hosting::getName));
            Indexer giExp = new Indexer(this.mgfFile);

            //Generate index and add to fileIndex
            fileIndex.addAll(giExp.generate());

            //sort the index based on scan number in accending order            
            //fileIndex.sort((spec1, spec2) -> spec1.getScanNum().compareTo(spec2.getScanNum()));
            //change arraylist of IndexKey to map for simplifying the search based on scan number of the spectrum
            Map<String, IndexKey> indexKeyMap = fileIndex.stream().collect(Collectors.toMap(IndexKey -> IndexKey.getScanNum(), IndexKey -> IndexKey));

            SpectraReader specReader = null;

            //create object to spectrum reader ms2io
            if (this.mgfFile.getName().endsWith("mgf")) {

                specReader = new MgfReader(mgfFile, fileIndex);

            } else if (this.mgfFile.getName().endsWith("msp")) {

            }

            String[] psm;
            String scannum;
            br = new BufferedReader(new FileReader(this.ionbot_result));

            int newLoc = 0;
            int num_spectra = fileIndex.size();
            Spectrum matched_spec = null;
            Modification mod;
//            int modPos;
//            char modified_aa;
//            String modName;
//            String unimode;

            String sequence;
//            int seqLen;
            String protein;
            ArrayList<Modification> mods;
            String modification;
            IndexKey ik;
//            String[] modMain;
//            String[] temp;
//
//            String delimiter;
//            int len_temp;

            //reading the column name and do nothing
            line = br.readLine();

            String[] scanstring;
            String tempscan;
            spec_count = 0;
            ionbot_count = 0;
            while ((line = br.readLine()) != null) {

                psm = line.split(",");
                //check if result's best_psm is 1, if not continue to next line
                if (!isInteger(psm[8]) && Integer.parseInt(psm[8]) != 1) {
                    continue;
                }

                sequence = psm[4];
//                seqLen = sequence.length();
                protein = psm[30];
                scanstring = psm[0].split(" ");
                tempscan = scanstring[scanstring.length - 1];
                scannum = tempscan.substring(tempscan.indexOf("=") + 1); // Integer.parseInt(psm[1]);
                modification = psm[5];

                index_count++;
                mods = new ArrayList<>();
                mod = new Modification(modification);
                mods.add(mod);

                //new way of searching for indexkey from list which is converted to map
                ik = indexKeyMap.get(scannum);
                if (ik != null) {
                    matched_spec = specReader.readAt(ik.getPos());
                    matched_spec.setSequence(sequence);
                    matched_spec.setProtein(protein);
                    matched_spec.setModification(mods);

                    wr.write(matched_spec);
                    spec_count++; // count spectrum match found for ionbot result
                }

                ionbot_count++;//count total ionbot result

                //if modification present
//                if (!modification.isEmpty()) {
//                    temp = modification.split("\\|");
//                    len_temp = temp.length;
//
//                    //loop for the number of modification specified in the result
//                    for (int k = 0; k < len_temp - 1;) {
//                        modPos = Integer.parseInt(temp[k]);
//                        if (modPos != 0) {
//                            modPos--;//change index to zero based
//                        } else {// value of 0 is for N-term modification and already 0
//
//                        }
//
//                        if (modPos > seqLen - 1) {
//                            //this is for the case of C-Terminal modification
//                            //where modified aa position given outside of the index
//                            modPos = seqLen - 1;
//                        }
//                        modified_aa = sequence.charAt(modPos);
//                        delimiter = "\\s+|]\\s*|\\[\\s*";
//                        modMain = temp[++k].split(delimiter);
//                        if (modMain.length > 1) {
//                            unimode = modMain[1];
//                            modName = modMain[2];
//                        } else {
//                            modName = modMain[0];
//                            unimode = "";
//                        }
//
//                        mod = new Modification(modPos, modified_aa, modName, unimode);
//                        mods.add(mod);
//                        k++;
//                    }
//
//                }
//                for (int i = newLoc; i < num_spectra; i++) {
//                    matched_spec = specReader.readAt(fileIndex.get(i).getPos());
//
//                    
//                    
//
//                    if (isInteger(matched_spec.getScanNumber()) && Integer.parseInt(matched_spec.getScanNumber()) == scannum) {
//                        //set spectrum protein match and modification
//                        matched_spec.setSequence(sequence);
//                        matched_spec.setProtein(protein);
//                        matched_spec.setModification(mods);
//
//                        //write to the file
//                        wr.write(matched_spec);
//                        newLoc = i;
//                        spec_count++;
//                        break;
//                    }
//
//                }
            }

        } catch (Exception e) {

            System.out.println(e.toString() + "\n" + this.ionbot_result.toString() + "\n" + this.mgfFile.toString() + "\n"
                    + line + "  ...at index: " + Integer.toString(index_count));
            try {
                synchronized (this.bwr_log) {
                    this.bwr_log.write(e.toString() + "\n" + this.ionbot_result.toString() + "\n" + this.mgfFile.toString() + "\n" + line + "  ...at index: " + Integer.toString(index_count) + "\n");
                }

            } catch (IOException ex) {
                Logger.getLogger(GetIdentifiedSpectra.class.getName()).log(Level.SEVERE, null, ex);
            }

        } finally {
            this.wr.closeWriter();

            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(GetIdentifiedSpectra.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
            System.out.println("File: " + this.mgfFile.toString() + " Processed, "
                    + "A total of " + Integer.toString(spec_count) + "/" + Integer.toString(ionbot_count) + " spectra are found");

            try {
                synchronized (this.bwr_log) {
                    this.bwr_log.write("File: " + this.mgfFile.toString() + " Processed, "
                            + "A total of " + Integer.toString(spec_count) + "/" + Integer.toString(ionbot_count) + " spectra are found" + "\n");
                }

            } catch (IOException ex) {
                Logger.getLogger(GetIdentifiedSpectra.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * check if string is parse to integer
     *
     * @param str
     * @return
     */
    private boolean isInteger(String str) {
        try {
            if (str == null) {
                return false;
            } else {
                double d = Integer.parseInt(str);
            }

        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    private Spectrum getSpectrum(ArrayList<IndexKey> indxlist, int scannum, SpectraReader rd) {

        Optional<IndexKey> matchingObject = indxlist.stream().
                filter(p -> p.getScanNum().equals(scannum)).
                findFirst();

        Spectrum spec = rd.readAt(matchingObject.get().getPos());

        return spec;
    }

}
