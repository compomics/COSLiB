/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coslib.model;

import com.compomics.ms2io.model.IndexKey;
import java.io.File;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Genet
 */
public class DataTest {
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getInputFile method, of class Data.
     */
    @Test
    public void testGetInputFile() {
        System.out.println("getInputFile");
        Data instance = null;
        File expResult = null;
        File result = instance.getInputFile();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOuFile method, of class Data.
     */
    @Test
    public void testGetOuFile() {
        System.out.println("getOuFile");
        Data instance = null;
        File expResult = null;
        File result = instance.getOuFile();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIndex method, of class Data.
     */
    @Test
    public void testSetIndex() {
        System.out.println("setIndex");
        ArrayList<IndexKey> index = null;
        Data instance = null;
        instance.setIndex(index);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIndex method, of class Data.
     */
    @Test
    public void testGetIndex() {
        System.out.println("getIndex");
        Data instance = null;
        ArrayList<IndexKey> expResult = null;
        ArrayList<IndexKey> result = instance.getIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPrecursorTolerance method, of class Data.
     */
    @Test
    public void testGetPrecursorTolerance() {
        System.out.println("getPrecursorTolerance");
        Data instance = null;
        double expResult = 0.0;
        double result = instance.getPrecursorTolerance();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentTolerance method, of class Data.
     */
    @Test
    public void testGetFragmentTolerance() {
        System.out.println("getFragmentTolerance");
        Data instance = null;
        double expResult = 0.0;
        double result = instance.getFragmentTolerance();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
