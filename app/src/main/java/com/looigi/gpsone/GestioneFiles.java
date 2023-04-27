package com.looigi.gpsone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class GestioneFiles {
    private static final GestioneFiles ourInstance = new GestioneFiles();

    public static GestioneFiles getInstance() {
        return ourInstance;
    }

    private GestioneFiles() {
    }

    public void CopiaFile(String srcF, String dstF) throws IOException {
        File src = new File(srcF);
        File dst = new File(dstF);
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public Integer ScriveFile(String Path, String fileName, String CosaScrivere) {
        try {
            File newFolder = new File(Path);
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                File file = new File(Path, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }

                Writer out = new BufferedWriter(new FileWriter(file, true), 1024);
                out.write(CosaScrivere);
                out.close();

                return 0;
            } catch (Exception ex) {
                // System.out.println("ex: " + ex);
                return -1;
            }
        } catch (Exception e) {
            // System.out.println("e: " + e);
            return -2;
        }
    }

    public boolean EliminaFile(String Path, String fileName) {
        File file = new File(Path + "/" + fileName);
        return file.delete();
    }

    public boolean EliminaFileUnico(String fileName) {
        if (EsisteFile(fileName)) {
            File file = new File(fileName);
            return file.delete();
        } else {
            return false;
        }
    }

    public boolean EsisteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public String LeggeFile(String Path, String fileName) {
        File file = new File(Path + '/' + fileName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            return "ERROR: " + e.getMessage();
        }

        return text.toString();
    }

    public String LeggeFileUnico(String fileName) {
        File file = new File(fileName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            return "ERROR: " + e.getMessage();
        }

        return text.toString();
    }

    public int DimensioniFile(String sFile) {
        File file = new File(sFile);
        return Integer.parseInt(String.valueOf(file.length() / 1024));
    }

    public Date DataFile(String sFile) {
        File file = new File(sFile);
        return new Date(file.lastModified());
    }

    public void CreaCartelle(String Path) {
        String[] Pezzetti = Path.split("/");
        String DaCreare = "";

        for (int i = 0; i < Pezzetti.length; i++) {
            if (!Pezzetti[i].isEmpty()) {
                DaCreare += "/" + Pezzetti[i];
                File newFolder = new File(DaCreare);
                if (!newFolder.exists()) {
                    newFolder.mkdir();
                }
            }
        }
    }
}
