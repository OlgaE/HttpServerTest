package com.demo;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public class OutputGenerator {

    private OutputStream socketOut;
    private String dirNameSrc;
    private File file;
    private List < File > listDirs;


    public void generateOutput(Output output){
         output.generateHTML(socketOut, dirNameSrc, file, listDirs);
    }
}
