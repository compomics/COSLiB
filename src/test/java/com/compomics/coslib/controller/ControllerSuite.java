/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coslib.controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Genet
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({com.compomics.coslib.controller.GetIdentifiedSpectraTest.class, com.compomics.coslib.controller.IndexSpectraTest.class, com.compomics.coslib.controller.ProcessClustersTest.class, com.compomics.coslib.controller.BuildLibraryTest.class, com.compomics.coslib.controller.Extract_inbotResultTest.class, com.compomics.coslib.controller.MatchedPeaksTest.class})
public class ControllerSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
}
