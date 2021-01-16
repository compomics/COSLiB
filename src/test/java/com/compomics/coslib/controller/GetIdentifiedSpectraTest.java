/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coslib.controller;

import com.compomics.coslib.controller.GetIdentifiedSpectra;
import com.compomics.ms2io.model.Spectrum;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Genet
 */
public class GetIdentifiedSpectraTest {
    
    public GetIdentifiedSpectraTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of call method, of class GetIdentifiedSpectra.
     */
    @Test
    public void testCall() {
        System.out.println("call");
        GetIdentifiedSpectra instance = null;
        ArrayList<Spectrum> expResult = null;
        instance.run();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
