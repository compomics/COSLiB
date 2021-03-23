package com.compomics.coslib.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Genet
 */
public class Extract_inbotResult {

    final String ionbot_result_dir;
    final String mgfdir;
    final String outputfolder;
    final int threads;

    public Extract_inbotResult(String result_dir, String mgfdir, String opFolder, int num_threads) {
        this.ionbot_result_dir = result_dir;
        this.mgfdir = mgfdir;
        this.outputfolder = opFolder;
        this.threads = num_threads;
    }

    public void Extract() throws InterruptedException, IOException {

        File ionbot_dir = new File(this.ionbot_result_dir);
        File mgf_dir = new File(this.mgfdir);

        String n;
        String mgfName;
        String nn;
        String ionbotName;

        File log_file=new File(this.outputfolder + "/" +" log.txt");        
        BufferedWriter bwr_log = new BufferedWriter(new FileWriter(log_file));
   

        if (ionbot_dir.exists() && mgf_dir.exists()) {

            ExecutorService execs = Executors.newFixedThreadPool(this.threads);

            File mgffile;
            File[] list_ionbotfiles = ionbot_dir.listFiles();
            Arrays.sort(list_ionbotfiles);
            File[] list_mgffiles = mgf_dir.listFiles();
            Arrays.sort(list_mgffiles);

            int filecount = 0;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            System.out.println(dtf.format(now) + ": Searching for spectra for ionbot result started ...");
            bwr_log.write(dtf.format(now) + ": Searching for spectra for ionbot result started ...\n");
            for (File ionbot_result : list_ionbotfiles) {
                try {

                    if (ionbot_result.getName().contains("proteins") || ionbot_result.getName().contains("meta") || ionbot_result.getName().contains("stats") || !ionbot_result.getName().endsWith("csv")) {
                        continue;
                    }

                    mgffile = null;
                    nn = ionbot_result.getName();
                    ionbotName = nn.substring(0, nn.indexOf("."));

                    for (File f : mgf_dir.listFiles()) {
                        n = f.getName();
                        mgfName = n.substring(0, n.indexOf("."));

                        if (ionbotName.equals(mgfName)) {
                            mgffile = f;
                        }
                    }
                    if (mgffile != null && mgffile.toString().endsWith("mgf")) {

                        GetIdentifiedSpectra identifiy = new GetIdentifiedSpectra(ionbot_result, mgffile, this.outputfolder, bwr_log);
                        execs.execute(identifiy);

                    } else {
                        System.out.println("no mgf file found for ionbot result file " + ionbot_result.toString());
                    }

                    filecount++;

                } catch (Exception ex) {
                    Logger.getLogger(Extract_inbotResult.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                   

                }
            }

            execs.shutdown();

            try {

                execs.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                
                now = LocalDateTime.now();
                System.out.println(dtf.format(now) + ": Search completed for " + Integer.toString(filecount) + " ionbot results");
                bwr_log.write(dtf.format(now) + ": Search completed for " + Integer.toString(filecount) + " ionbot results"+ "\n");


            } catch (InterruptedException e) {
                now = LocalDateTime.now();
                System.out.println(dtf.format(now) + ": Search completed for " + Integer.toString(filecount) + " ionbot results");

            }finally{
                bwr_log.flush();
                bwr_log.close();
            }

            // execs.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } else {
            System.out.println("Either ionbot result directory or mgf directory not found");
        }

    }

}
