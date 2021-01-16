package com.compomics.coslib;

import com.compomics.coslib.controller.BuildLibrary;
import com.compomics.coslib.controller.Extract_inbotResult;
import com.compomics.coslib.controller.IndexSpectra;
import java.io.File;
import com.compomics.coslib.model.Data;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Genet
 */
public class MainClass {

    public static void main(String[] args) throws IOException {

        try {

            validate(args);

            String ionbot_result_dir = args[0];
            String mgf_dir = args[1];
            String outputfolder = args[2];
            int num_threads = Integer.parseInt(args[3]);

//            
//            String mgf_dir = "C://human_hcd/testfolder/part";
//            String ionbot_result_dir = "C://human_hcd/testfolder/ionbot";
//            String outputfolder=  "C://human_hcd/testfolder";         
//            String mgf_dir = "C://human_hcd/testfile_coslib_small/secondfolder";
//            String ionbot_result_dir = "C://human_hcd/testfile_coslib_small/firstfolder";
//            String outputfolder= "C://human_hcd/testfile_coslib_small";
//            int num_threads = 2;
//            String mgf_dir = "D://testfile_coslib/partmgf";
//            String ionbot_result_dir = "D://testfile_coslib/partionbot";
//            String outputfolder= "D://testfile_coslib";
//            int num_threads = 2;
            Extract_inbotResult ex = new Extract_inbotResult(ionbot_result_dir, mgf_dir, outputfolder, num_threads);
            ex.Extract();

//        File fI = new File("");
//        File fO = new File("");
//        double precTolerance = 0;
//        double fragTolerance = 0;
//        Data d = new Data(fI, fO, precTolerance, fragTolerance);
//        
//        IndexSpectra spec = new IndexSpectra(d);
//        spec.ReadData();
//        
//        BuildLibrary build = new BuildLibrary(d);
//        build.Start();    
        } catch (InterruptedException ex1) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    private static void validate(String[] arguments) {

        if (arguments.length != 4) {
            System.out.println("Invalid number of arguments, toal of " + arguments.length+ " found \n"
                    + "Arguments ----\n  ionbotresult_directory mgf_directory output_directory numberof_threads");
            System.exit(0);
        }
        if (!isInteger(arguments[3])) {
            System.out.println("Invalid last argument type, expected Integer \n + \"Arguments ----\n "
                    + " ionbotresult_directory mgf_directory output_directory numberof_threads");
            System.exit(0);
        }
        
    }

    private static boolean isInteger(String str) {
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
}
