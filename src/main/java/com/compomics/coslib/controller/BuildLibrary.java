package com.compomics.coslib.controller;

import com.compomics.coslib.model.Data;
import com.compomics.ms2io.controller.MgfReader;
import com.compomics.ms2io.controller.MspReader;
import com.compomics.ms2io.controller.SpectraReader;
import com.compomics.ms2io.model.IndexKey;
import com.compomics.ms2io.model.Peak;
import com.compomics.ms2io.model.Spectrum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.compomics.ms2io.controller.MgfWriter;
import java.io.File;
import java.util.Collections;

/**
 *
 * @author Genet
 */
public class BuildLibrary {

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
    public BuildLibrary(Data d) {
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

            //validate each spectra in each clusters
            ValidateClusters(clusterofSpectra);

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

        ArrayList<Spectrum> similarSpec; //spectra with the same sequence, charge, pcm, mw
        Spectrum current_spec = new Spectrum();
        ArrayList<Integer> spec_2bDeleted = new ArrayList<>();
        ArrayList<Spectrum> sameSpecs = new ArrayList<>();
        MatchedPeaks matchObj = new MatchedPeaks();
        Map<String, List<Peak>> matchedPeaks = new HashMap<>();
        double maxMatchedRatio = 0;
        double ratio;

        int matchedPeaksizeRef = 0;
        for (Map.Entry<String, ArrayList<Spectrum>> cs : clusteredSpectra.entrySet()) {
            //get same spectra clustered together
            similarSpec = cs.getValue();

            len = similarSpec.size();

            while (len > 0) {
                current_spec = similarSpec.get(0);

                //add current_spec to samespec 
                sameSpecs.add(current_spec);

                for (int j = 1; j < len; j++) {
                    Spectrum tempSpec = similarSpec.get(j);
                    //hard codded fragment tolerance 0.05

                    maxMatchedRatio = 0;
                    //compare tempspec, read from the cluster, with each spectrum in the sameSpec and if matched meaks are more than 70% add to this list
                    for (Spectrum ss : sameSpecs) {
                        //get matched peaks within the mass error, if present
                        matchedPeaks = matchObj.getMatchedPeaks(ss.getPeakList(), tempSpec.getPeakList(), data.getFragmentTolerance());

                        //take the samllest number of peaks from the spec under comparison
                        matchedPeaksizeRef = current_spec.getNumPeaks() < tempSpec.getNumPeaks() ? current_spec.getNumPeaks() : tempSpec.getNumPeaks();
                        ratio = matchedPeaks.size() / matchedPeaksizeRef;
                        if (maxMatchedRatio < ratio) {
                            maxMatchedRatio = ratio;
                        }
                    }

                    //if number of matched peaks to any spec in cluster equals or > 70% of number of peaks of one of the two specs, it is considered as same peptide
                    //otherwise skip the process
                    if (maxMatchedRatio < 0.7) {
                        continue;
                    }
//                        peaks1 = (ArrayList) (matchedPeaks.values().toArray()[0]);
//                        if (peaks1.size() == tempSpec.getPeakList().size()) {
                    sameSpecs.add(tempSpec);
                    spec_2bDeleted.add(j);
                    //}
                }

                //from the sameSpecs, calculate peaks intensity(average), mass error(average) and update one of the same spec's intensity 
                //and write representative spectrum to library
                write2library(AveragingSpecs(sameSpecs));

                //after writing, remove elements of samespec
                sameSpecs.clear();

                //delete specs from specSmNumpeaks at the location from spec2bdeleted list
                len2 = spec_2bDeleted.size();
                for (int k = 0; k < len2; k++) {
                    similarSpec.remove(spec_2bDeleted.get(k));
                }

                //calculate the new element size of specSmNumPeaks after deleting specs
                len = similarSpec.size();
                spec_2bDeleted.clear(); // clear this list for next group

            }

            // }
            similarSpec.clear();//clear the current group of similar spectra
        }
    }

    /**
     * this method calculate the average intensity of the given same specs and
     * produce a representative spectrum
     *
     * @param samespecs same spectra to intensity averaged and write to the
     * library
     */
    private Spectrum AveragingSpecs(ArrayList<Spectrum> samespecs) {
        Spectrum representative_spec = samespecs.get(0);
        int len_specs_original = samespecs.size();
        double pc_mass=0;
        for(Spectrum s:samespecs){
            pc_mass+=s.getPCMass();
        }
        pc_mass=pc_mass/len_specs_original;
        representative_spec.setPCMass(pc_mass);        
        
        ArrayList<Peak> newPeakList = new ArrayList<>();
        double intensity;
        double mz;

        List<Double> mzs = new ArrayList<>();
        Peak pk;
        double minmz;
        int peak_occurence;
        int len_specs = len_specs_original;
        List<Integer> delIndex ;
        int indx;
        
        
        while (len_specs > 0) {

            for (int k = 0; k < len_specs; k++) {

                if (!samespecs.get(k).getPeakList().isEmpty()) {
                    mzs.add(samespecs.get(k).getPeakList().get(0).getMz());
                } else {
                    samespecs.remove(k);
                    len_specs = samespecs.size();
                }

            }

            minmz = Collections.min(mzs);
            indx = 0;
            delIndex = new ArrayList<>();
            peak_occurence = 0;
            intensity = 0;
            mz = 0;
            for (double mmz : mzs) {
                if (mmz <= minmz + data.getFragmentTolerance()){;// && mmz >= minmz - data.getFragmentTolerance()) {
                    intensity += samespecs.get(indx).getPeakList().get(0).getIntensity();
                    mz += mmz;// samespecs.get(indx).getPeakList().get(i).getMz();
                    delIndex.add(indx);
                    peak_occurence++;
                }
                indx++;
            }

            //if peak occurence is less than 50%, discard
            if (peak_occurence / len_specs_original >= 0.5) {
                pk = new Peak(mz / peak_occurence, intensity / peak_occurence, "\"?\"");
                newPeakList.add(pk);
            }

            //remove peaks that are already processed to create new substitute peak
            for (int i = 0; i < peak_occurence; i++) {
                samespecs.get(i).getPeakList().remove(0);
            }

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

    private void ValidateClusters(Map<String, ArrayList<Spectrum>> clusters) {

        List<Double> list_fdr;
        for (Map.Entry<String, ArrayList<Spectrum>> cs : clusters.entrySet()) {
            //get same spectra clustered together
            List<Spectrum> similarSpec = cs.getValue();
            int decoy = 0;
            int target = 0;
            
            double fdr_calculated = 0;

            list_fdr=new ArrayList<>();
            
            for (Spectrum s : similarSpec) {//for (ComparisonResult r : result) {

                if (!s.getComment().contains("Decoy") && !s.getComment().contains("decoy")) {
                    target++;

                } else {
                    decoy++;
                }

                fdr_calculated = decoy / (double) (target);
                fdr_calculated = (double) Math.round(fdr_calculated * 100000d) / 100000d;
                list_fdr.add(fdr_calculated);
            }
            
            
            Collections.reverse(list_fdr);
            
            double fdrLocalMin = list_fdr.get(0);

            int count=0;
            for (Double fdr : list_fdr) {
                double fdrCurrent = fdr;
                if (fdrCurrent > fdrLocalMin) {
                    list_fdr.add(count, fdrLocalMin);
                } else {
                    fdrLocalMin = fdrCurrent;
                }

                count++;
            }
            
            Collections.reverse(list_fdr);            
            int c=0;
            for(Double d : list_fdr){
                if(d>0.01 || (similarSpec.get(c).getComment().contains("decoy") || similarSpec.get(c).getComment().contains("Decoy") ) ){
                    
                    similarSpec.remove(c);
                }
                c++;
            }
        }

    }
}
