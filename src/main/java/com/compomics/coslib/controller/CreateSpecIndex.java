package com.compomics.coslib.controller;

import com.compomics.ms2io.controller.Indexer;
import java.io.File;
import com.compomics.ms2io.model.IndexKey;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Genet
 */
public class CreateSpecIndex implements Runnable {

    final String project_folders;
    final FileWriter output_filewriter;

    public CreateSpecIndex(String projectfolders, FileWriter wr) {
        this.project_folders = projectfolders;
        this.output_filewriter = wr;

    }

    @Override
    public void run() {
        File mgf_dir = new File(this.project_folders + "//generatedMGF");
        File[] file_lists = mgf_dir.listFiles();
        String output_filename = "";
        if (file_lists != null) {
            for (File file : file_lists) {
                if (file.getName().endsWith("mgf")) {
                    try {
                        Indexer giExp = new Indexer(file);
                        List<IndexKey> fileIndex = giExp.generate();
                        for (IndexKey k : fileIndex) {
                            output_filename = Double.toString(k.getPM());
                            int similarfile_count = 0;
                            synchronized (this.output_filewriter) {
                                File opfile = new File(output_filename);
                                while (!opfile.exists()) {
                                    similarfile_count++;
                                    opfile = new File(output_filename + '_' + Integer.toString(similarfile_count));
                                }                                
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CreateSpecIndex.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }

        }
    }

}
