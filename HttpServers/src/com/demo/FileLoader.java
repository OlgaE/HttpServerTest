package com.demo;

import org.apache.commons.io.IOUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;

public class FileLoader {

    public void loadFile(OutputStream socketOut, File newFile){
        String outLine = "Mime type of " + newFile.getName() + " is " +
                new MimetypesFileTypeMap().getContentType(newFile) + "\r\n";
        String typeName = "Content-Type: " + new MimetypesFileTypeMap().getContentType(newFile) + "\r\n";

        try {
            socketOut.write("HTTP/1.0 200 OK\r\n".getBytes());
            socketOut.write(typeName.getBytes());
            socketOut.write(outLine.getBytes());

            String fileSize = "Content-Length: " + newFile.length() + "\r\n";
            socketOut.write(fileSize.getBytes());
            socketOut.write("\r\n".getBytes());

            InputStream inFile = new FileInputStream(newFile.getAbsolutePath());
            IOUtils.copy(inFile, socketOut);

            socketOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
