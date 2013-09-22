package com.demo;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewThread implements Runnable {

    private String dirNameSrc;
    private OutputStream socketOut;
    private InputStream socketIn;

    public NewThread(Socket socket, String dirNameSrc) throws Throwable {
        this.socketOut = socket.getOutputStream();
        this.socketIn = socket.getInputStream();
        this.dirNameSrc = dirNameSrc;
    }

    public void run() {

        //FileLoader fileLoader = new FileLoader();

        try (BufferedReader buf = new BufferedReader(new InputStreamReader(socketIn))) {
            StringBuilder sb = new StringBuilder();

            int input;
            while ((input = buf.read()) != -1 && input != 10 && input != 13) {
                sb.append((char) input);
            }
            String data = sb.toString();
            String ar[] = data.split(" ");

            String cmd = "";
            if (ar.length > 1) {
                cmd = ar[1].trim();
            }

            cmd = URLDecoder.decode(cmd, "UTF-8");
            System.out.println("cmd: " + cmd);

            String dirName = dirNameSrc + cmd;
            File newFile = new File(dirName);

            if (dirName.length() != 0 && newFile.getCanonicalPath().contains(dirNameSrc)) {

                if (dirName.substring(dirName.length() - 1).equals("/") || dirName.substring(dirName.length() - 1).equals("/\\")) {
                    if (newFile.isDirectory()) {

                        File[] fileArray = newFile.listFiles();
                        List<String> fileList = new ArrayList<>();
                        if (fileArray != null) {
                            for (File f : fileArray) {
                                fileList.add(f.getName());
                            }
                        }
                        if (fileList.contains("index.html")) {
                            System.out.println("Directory: reading index.html file..");
                            readIndexHtml(newFile);
                        } else {
                            System.out.println("Directory: reading..");
                            checkingDirs(dirNameSrc, newFile);
                        }

                        System.out.println("Directory: done.");
                    } else {
                        System.out.println("No directory found.");

                        socketOut.write("HTTP/1.0 404 Directory not found.\r\n".getBytes());
                        socketOut.write("\r\n".getBytes());
                        socketOut.write("<html><body><h1>404 Directory not found.</h1></body></html>".getBytes());
                        socketOut.close();

                        System.out.println("No directory found: " + newFile);
                    }
                } else {
                    if (newFile.exists()) {
                        System.out.println("File: reading.. " + newFile.getName());
                        new FileLoader().loadFile(socketOut, newFile);

                        System.out.println("File: done.");
                    } else {
                        socketOut.write("HTTP/1.0 404 File not found.\r\n".getBytes());
                        socketOut.write("\r\n".getBytes());
                        socketOut.write("<html><body><h1>404 File not found.</h1></body></html>".getBytes());
                        socketOut.close();

                        System.out.println("No file found: " + newFile);
                    }
                }
                System.out.println();
            } else {
                socketOut.write("HTTP/1.0 503 Service unavailable.\r\n".getBytes());
                socketOut.write("\r\n".getBytes());
                socketOut.write("<html><body><h1>503 Service unavailable.</h1></body></html>".getBytes());
                socketOut.close();
            }

        } catch (IOException e) {

            try {
                socketOut.write("HTTP/1.0 501\r\n".getBytes());
                socketOut.write("\r\n".getBytes());
                String mess = "<html><body><h1>" + e.getMessage() + "</h1></body></html>";
                socketOut.write(mess.getBytes());
                socketOut.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void readIndexHtml(File indexDir) {

        String fName = null;
        try {
            fName = indexDir.getCanonicalPath() + File.separator+"index.html";
        } catch (IOException e) {
            e.printStackTrace();
        }
        File index = new File(fName);

        try (InputStream fromFile = new FileInputStream(index.getAbsolutePath())) {
            IOUtils.copy(fromFile, socketOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkingDirs(String dirNameSrc, File file) {

        try {
            System.out.println("Directory: " + file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<File> listDirs = new ArrayList<>();

        File[] files = file.listFiles();
        if (files != null) {
            Collections.addAll(listDirs, files);
        }
        Collections.sort(listDirs, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && !o2.isDirectory()) {
                    return -1;
                } else if (!o1.isDirectory() && o2.isDirectory()) {
                    return 1;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });

        //HtmlBuilder htmlGen = new HtmlBuilder();
        //new HtmlBuilder().generateHTML(socketOut, dirNameSrc, file, listDirs);
        //Output out = new HtmlBuilder();
        OutputGenerator out = new OutputGenerator();
        out.generateOutput(new HtmlBuilder());
    }
}
