package com.demo;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class HtmlBuilder implements Output {

    public void generateHTML(OutputStream socketOut, String dirNameSrc, File file, List<File> listDirs){
        try (OutputStreamWriter out = new OutputStreamWriter(socketOut, "UTF-8")) {

            out.write("HTTP/1.0 200 OK\r\n");
            out.write("\r\n");

            out.write("<html><head>");
            out.write("<title>Java School</title>");
            out.write("</head><body>");

            out.write("<table>");
            out.write("<tr><th>Name</th><th>Date modified</th><th>Size</th></tr>");

            String parent = file.getParent();
            if (parent.contains(dirNameSrc)) {
                String parentName = parent.substring(dirNameSrc.length());

                if (parentName.length() == 0) {
                    parentName = "\\" + parentName;
                }

                if (parentName.equals("\\")) {
                    out.write("<tr><td><a href = " + parentName + "> ..  </a></td></tr>");
                } else {
                    out.write("<tr><td><a href = " + parentName + "/> ..  </a></td></tr>");
                }
            }

            String href;
            for (File dir : listDirs) {

                if (dir.isDirectory()) {
                    String parentName = dir.getParent();
                    String subString = parentName.substring(dirNameSrc.length());

                    href = subString + "\\" + dir.getName();
                    out.write("<tr><td><a href = " + href + "/> " + dir.getName() + " </a></td></tr>");
                } else {

                    out.write("<tr><td><a href = \"" + dir.getName() + "\">" + dir.getName() + "</td>");

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    out.write("<td style = \"width : 50%\"; align=\"center\">" + sdf.format(dir.lastModified()) + "</td>");
                    out.write("<td style = \"width : 10%\"; align=\"center\">" + dir.length() + "</td></tr>");
                }
            }

            out.write("</table></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
