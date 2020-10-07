package com.compomics.coslib.controller;

import com.compomics.ms2io.model.Peak;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Genet
 */
public class MatchedPeaks {

    public Map getMatchedPeaks(ArrayList<Peak> filteredExpMS2_1, ArrayList<Peak> filteredExpMS2_2, double fragTolerance) {
        List<Peak> mPeaks_2 = new ArrayList<>(); //matched peaks from filteredExpMS2_2
        List<Peak> mPeaks_1 = new ArrayList<>(); //matched peaks from filteredExpMS2_1
        Map<String, List<Peak>> map = new TreeMap<>();

        for (int i = 0; i < filteredExpMS2_1.size(); i++) {
            Peak p1 = filteredExpMS2_1.get(i);
            double mz_p1 = p1.getMz();
            double diff = fragTolerance;// Based on Da.. not ppm...
            boolean found = false;
            Peak matchedPeak_2 = null;
            for (Peak peak_expMS2_2 : filteredExpMS2_2) {
                double tmp_mz_p2 = peak_expMS2_2.getMz(),
                        tmp_diff = (tmp_mz_p2 - mz_p1);

                if (Math.abs(tmp_diff) < diff) {
                    matchedPeak_2 = peak_expMS2_2;
                    diff = Math.abs(tmp_diff);
                    found = true;
                } else if (diff == tmp_diff) {
                    // so this peak is indeed in between of two peaks
                    // So, just the one on the left side is being chosen..
                }

            }
            if (found && !mPeaks_2.contains(matchedPeak_2)) {
                mPeaks_2.add(matchedPeak_2);
                mPeaks_1.add(p1);
            }

        }

        map.put("Matched Peaks1", mPeaks_1);
        map.put("Matched Peaks2", mPeaks_2);

        return map;
    }
}
