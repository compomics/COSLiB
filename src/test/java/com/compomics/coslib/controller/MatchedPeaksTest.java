/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coslib.controller;

import com.compomics.coslib.controller.MatchedPeaks;
import com.compomics.ms2io.model.Peak;
import java.util.ArrayList;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Genet
 */
public class MatchedPeaksTest {
    
    public MatchedPeaksTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getMatchedPeaks method, of class MatchedPeaks.
     */
    @Test
    public void testGetMatchedPeaks() {
        System.out.println("getMatchedPeaks");
        ArrayList<Peak> filteredExpMS2_1 = null;
        ArrayList<Peak> filteredExpMS2_2 = null;
        double fragTolerance = 0.0;
        MatchedPeaks instance = new MatchedPeaks();
        Map expResult = null;
        Map result = instance.getMatchedPeaks(filteredExpMS2_1, filteredExpMS2_2, fragTolerance);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
