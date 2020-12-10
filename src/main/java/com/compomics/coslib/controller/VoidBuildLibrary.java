package com.compomics.coslib.controller;

import com.compomics.coslib.model.Data;
import com.compomics.ms2io.controller.MgfReader;
import com.compomics.ms2io.controller.MgfWriter;
import com.compomics.ms2io.controller.MspReader;
import com.compomics.ms2io.controller.SpectraReader;
import com.compomics.ms2io.model.IndexKey;
import com.compomics.ms2io.model.Peak;
import com.compomics.ms2io.model.Spectrum;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Genet
 */
public class VoidBuildLibrary {
    final Data data;
    final Iterator<IndexKey> iter;
    SpectraReader msReader;
    MgfWriter writer;
    ArrayList<Spectrum> specs = new ArrayList<>();
    ArrayList<Spectrum> outboundSpecs = new ArrayList<>();

    /**
     * constructor for this class
     *
     * @param d data containing the information the input data
     */
    public VoidBuildLibrary(Data d) {
        this.data = d;
        this.iter = data.getIndex().iterator();
        if (this.data.getInputFile().getName().endsWith("mgf")) {
            this.msReader = new MgfReader(this.data.getInputFile(), this.data.getIndex());

        } else if (this.data.getInputFile().getName().endsWith("msp")) {
            this.msReader = new MspReader(this.data.getInputFile(), this.data.getIndex());
        }

        writer = new MgfWriter(new File(""));

    }

    /**
     * start the tasks to build the library
     */
    public void Start() {
        ArrayList<Spectrum> selectedSpectra;
        Map<String, ArrayList<Spectrum>> clusterofSpectra;
        double limit_precursormass = 7;//
        double minpcm = 0;

        while (iter.hasNext()) {  //endless loop, ternimates startIndex>=lengthSpectra  
            //update the start index by number of spectra read that has same pcm

            //read spectra from starting index to PCM<=PCM+tolerance
            selectedSpectra = ReadSamePCMSpecs(minpcm);

            //check if there is remaining spectra from previous read
            if (!outboundSpecs.isEmpty()) {
                selectedSpectra.addAll(outboundSpecs);
                outboundSpecs.clear();
            }
            
            //sort spectra based on precursor mass
            Collections.sort(selectedSpectra);

            //get minimum precursor mass of the spectra in the collection(selectedSpectra)
            minpcm = selectedSpectra.get(0).getPCMass();

            //cluster same spectra: same sequence, charge, modification
            clusterofSpectra = ClusterSameSpecs(selectedSpectra);

            //check and back up spectra in clusters if they should be in the next minpcm+7Da. pcm range
            //clusters with in the range should forward to the next step towards library building
            ClearClusters(clusterofSpectra, minpcm + limit_precursormass);

            //assemble same spectrum in each cluster and procuce one representative
            FilterNwriteClusteredSpectra(clusterofSpectra);

        }
    }

    /**
     * *
     * reads spectra with same precursor mass and with in the tolerance (7 Da.
     * from the ionbot spec)
     *
     * @return spectra having same precursor mass
     */
    private ArrayList<Spectrum> ReadSamePCMSpecs(double minPCM) {
        specs.clear();
        Spectrum spec;
        IndexKey k;
        Long pos;

        //k = this.iter.next();
        //pos = k.getPos();
        //spec = msReader.readAt(pos);
        double endPCM = minPCM + 14; // spec.getPCMass() + 10;//+ this.data.getPrecursorTolerance();
        double currentPCM = minPCM;
        //specs.add(spec);
        while (this.iter.hasNext() && currentPCM <= endPCM) {
            k = this.iter.next();
            pos = k.getPos();
            spec = msReader.readAt(pos);
            currentPCM = spec.getPCMass();
            specs.add(spec);

        }

        return specs;

    }

    /**
     * cluster spectra with the same sequence, charge, molecular mass and
     * modifications
     *
     * @param spectra list of spectra with same and += tolerance precursor mass
     * @return returns clustered spectra
     */
    private Map<String, ArrayList<Spectrum>> ClusterSameSpecs(ArrayList<Spectrum> spectra) {

        Map<String, ArrayList<Spectrum>> clusters = new HashMap<>();

        for (Spectrum spectrum : spectra) {
            String key = spectrum.getSequence() + "; " + spectrum.getCharge_asStr() + "; " + spectrum.getMW() + "; " + spectrum.getModifications_asStr();
            if (!clusters.containsKey(key)) {
                clusters.put(key, new ArrayList<Spectrum>());
            }
            clusters.get(key).add(spectrum);
        }

        return clusters;

    }

    private void FilterNwriteClusteredSpectra(Map<String, ArrayList<Spectrum>> clusteredSpectra) {
        int len;
        int len2;
        int len3;
        int len4;
        int numPeaks;

        ArrayList<Spectrum> similarSpec; //spectra with the same sequence, charge, pcm, mw
        ArrayList<Spectrum> specSmNumPeaks; // group of specs with the same number of peaks from similarSpec 
        Map<Integer, ArrayList<Spectrum>> samePeakSize = new HashMap<>();//map for same peak size similar specs
        Spectrum current_spec = new Spectrum();
        ArrayList<Integer> spec_2bDeleted = new ArrayList<>();
        ArrayList<Spectrum> sameSpecs = new ArrayList<>();
        MatchedPeaks matchObj = new MatchedPeaks();
        ArrayList<Peak> peaks1;
        Map<String, List<Peak>> matchedPeaks = new HashMap<>();

        int matchedPeaksizeRef = 0;
        for (Map.Entry<String, ArrayList<Spectrum>> cs : clusteredSpectra.entrySet()) {
            //get same spectra clustered together
            similarSpec = cs.getValue();

            len = similarSpec.size();

            //if there is only one spectrum in this group, write to the library
            if (len == 1) {
                write2library(similarSpec.get(0));
                break;
            }

            //Else
            //group spectrum with the same number of peaks
            for (Spectrum spec : similarSpec) {
                numPeaks = spec.getNumPeaks();

                if (samePeakSize.containsKey(len)) {
                    samePeakSize.put(numPeaks, new ArrayList<Spectrum>());
                }
                samePeakSize.get(len).add(spec);

            }

            len2 = 0;

            //check for same mz between the grouped spectra
            for (Map.Entry<Integer, ArrayList<Spectrum>> mp : samePeakSize.entrySet()) {
                specSmNumPeaks = mp.getValue();
                len2 = specSmNumPeaks.size();
                if (len2 == 1) {
                    write2library(specSmNumPeaks.get(0));
                    break;
                }
                len3 = specSmNumPeaks.size();

                while (len3 > 0) {
                    current_spec = specSmNumPeaks.get(0);
                    
                    for (int j = 1; j < len3; j++) {
                        Spectrum tempSpec = specSmNumPeaks.get(j);
                        //hard codded fragment tolerance 0.05

                        //get matched peaks within the mass error, if present
                        matchedPeaks = matchObj.getMatchedPeaks(current_spec.getPeakList(), tempSpec.getPeakList(), data.getFragmentTolerance());

                        //take the samllest number of peaks from the spec under comparison
                        matchedPeaksizeRef = current_spec.getNumPeaks() < tempSpec.getNumPeaks() ? current_spec.getNumPeaks() : tempSpec.getNumPeaks();

                        //if number of matched peaks equals or > 70% of number of peaks of one of the two specs, it is considered as same peptide
                        //otherwise skip the process
                        if ((matchedPeaks.size() / matchedPeaksizeRef) < 0.7) {
                            continue;
                        }
//                        peaks1 = (ArrayList) (matchedPeaks.values().toArray()[0]);
//                        if (peaks1.size() == tempSpec.getPeakList().size()) {
                        sameSpecs.add(tempSpec);
                        spec_2bDeleted.add(j);

                        //}
                    }

                    //finally add current_spec to samespec 
                    sameSpecs.add(current_spec);

                    //calculate peaks intensity(average), mass error(avg) and update one of the same spec's intensity 
                    //and write representative spectrum to library
                     write2library(AveragingSpecs(sameSpecs));

                    //after writing, remove elements of samespec
                    sameSpecs.clear();

                    //delete specs from specSmNumpeaks at the location from spec2bdeleted list
                    len4 = spec_2bDeleted.size();
                    for (int k = 0; k < len4; k++) {
                        specSmNumPeaks.remove(spec_2bDeleted.get(k));
                    }

                    //calculate the new element size of specSmNumPeaks after deleting specs
                    len3 = specSmNumPeaks.size();

                }
                spec_2bDeleted.clear(); // clear this list for next group
            }

            samePeakSize.clear();//clear the current group of same peak size specs for next groups
        }
    }

    /**
     * this method calculate the average intensity of the given same specs, make
     * a spectrum and writes to the library
     *
     * @param samespecs same spectra to intensity averaged and write to the
     * library
     */
    private Spectrum AveragingSpecs(ArrayList<Spectrum> samespecs) {

        Spectrum representative_spec = samespecs.get(0);
        int len_specs = samespecs.size();
        int numPeaks = representative_spec.getPeakList().size();
        ArrayList<Peak> newPeakList = new ArrayList<>();
        double intensity;
        double mz = 0;

        Peak pk;
        for (int i = 0; i < numPeaks; i++) {
            intensity = 0;

            for (int k = 0; k < len_specs; k++) {

                intensity += samespecs.get(k).getPeakList().get(i).getIntensity();
                mz += samespecs.get(k).getPeakList().get(i).getMz();
            }

            pk = samespecs.get(0).getPeakList().get(i);
            pk.setIntensity(intensity / len_specs);
            pk.setMz(mz / len_specs);

            newPeakList.add(pk);

        }
        representative_spec.setPeakList(newPeakList);
       
        return representative_spec;

    }

    private void write2library(Spectrum spec) {

        //write the spectrum to the library 
        writer.write(spec);

    }

    private void ClearClusters(Map<String, ArrayList<Spectrum>> clusterofSpectra, double boundaryPCM) {
        Spectrum leadingSpec;
        String cluster;
        for (Iterator<String> iterator = clusterofSpectra.keySet().iterator(); iterator.hasNext();) {
            cluster = iterator.next();
            leadingSpec = clusterofSpectra.get(cluster).get(0);// cluster.getValue().get(0);
            if (leadingSpec.getPCMass() > boundaryPCM) {
                outboundSpecs.addAll(clusterofSpectra.get(cluster));
                iterator.remove();

            }

        }
    }
    
}
