/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coslib.controller;

import com.compomics.coslib.controller.Extract_inbotResult;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Genet
 */
public class Extract_inbotResultTest {
    
    public Extract_inbotResultTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of Extract method, of class Extract_inbotResult.
     */
    @Test
    public void testExtract() throws IOException {
        try {
            System.out.println("Extract");
            Extract_inbotResult instance = null;
            instance.Extract();
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Extract_inbotResultTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
