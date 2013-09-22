package com.demo;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public interface Output {

    public void generateHTML(OutputStream socketOut, String dirNameSrc, File file, List<File> listDirs);
}
