package com.looigi.gpsone;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static final Log ourInstance = new Log();

    public static Log getInstance() {
        return ourInstance;
    }

    private Log() {
        File dir = new File(VariabiliGlobali.getInstance().getPercorsoDIR());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void EliminaFileLog() {
        GestioneFiles.getInstance().EliminaFile(VariabiliGlobali.getInstance().getPercorsoDIR(), "Logs.txt");
    }

    public void ScriveLog(String RigaDebug) {
        if (VariabiliGlobali.getInstance().isAzionaDebug()) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
            String strDate = formatter.format(date);

            String Scritta = strDate + ": " + RigaDebug + '\n';
            GestioneFiles.getInstance().ScriveFile(VariabiliGlobali.getInstance().getPercorsoDIR(), "Logs.txt", Scritta);
        }
    }
}
