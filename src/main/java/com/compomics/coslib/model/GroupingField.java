package com.compomics.coslib.model;

import com.compomics.ms2io.model.Charge;
import com.compomics.ms2io.model.Modification;
import java.util.List;

/**
 *this class holds spectra information to group together
 * @author Genet
 */

public class GroupingField {
    
    public String sequence;
    public double mw;
    public Charge charge;
    public List<Modification> modifications;    
            
}
