package com.looigi.gpsone;

import android.database.Cursor;
import android.location.Location;
import android.widget.TextView;

import com.looigi.gpsone.Notifiche.GestioneNotifiche;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    private static Utility instance = null;
    private db_dati dbGpsPos;
    private int ProgressivoDBGPS;
    private Location locazioneAttuale;

    private Utility() {
    }

    public static Utility getInstance() {
        if(instance == null) {
            instance = new Utility();
        }

        return instance;
    }

    public void setDbGpsPos(db_dati dbGpsPos) {
        this.dbGpsPos = dbGpsPos;
    }

    public String PrendeErroreDaException(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        return TransformError(errors.toString());
    }

    private String TransformError(String error) {
        String Return = error;

        if (Return.length() > 250) {
            Return = Return.substring(0, 247) + "...";
        }
        Return = Return.replace("\n", " ");

        return Return;
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

    public void ScriveData() {
        VariabiliGlobali.getInstance().getTxtData().setText(VariabiliGlobali.getInstance().getOggi());
    }

    public void ScriveSezioni() {
        TextView txtSezione = VariabiliGlobali.getInstance().getTxtSezione();

        if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() == -1) {
            txtSezione.setText("Tutte (" + (VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato() + 1) + ")");
        } else {
            txtSezione.setText("Sezione " + (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() + 1) + "/" +
                    (VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato() + 1));
        }
    }

    public void ScriveDatiAVideo() {
        String Titolo = "Attivo: " + VariabiliGlobali.getInstance().isServizioGPS() + " - Ultima rilevazione: " + VariabiliGlobali.getInstance().getUltimoPunto();
        String Titolo2 = "Punti rilevati: " + VariabiliGlobali.getInstance().getPuntiDisegnati() + " Sezioni: " + (VariabiliGlobali.getInstance().getSezioniGiorno() + 1);

        /*Notifica.getInstance().setTitolo(Titolo);
        Notifica.getInstance().setTitolo2(Titolo2);
        Notifica.getInstance().setContext(MainActivity.getAppContext());

        Notifica.getInstance().AggiornaNotifica(); */
        GestioneNotifiche.getInstance().setTitolo(Titolo);
        GestioneNotifiche.getInstance().setTitolo2(Titolo2);
        GestioneNotifiche.getInstance().AggiornaNotifica();

        /* if (VariabiliGlobali.getInstance().getViewNotifica() != null) {
            VariabiliGlobali.getInstance().getViewNotifica().setTextViewText(R.id.txtTitoloNotifica, Titolo);
            VariabiliGlobali.getInstance().getViewNotifica().setTextViewText(R.id.txtTitoloNotifica2, Titolo2);
            if (VariabiliGlobali.getInstance().isServizioGPS()) {
                VariabiliGlobali.getInstance().getViewNotifica().setImageViewResource(R.id.imgPlayStop, R.drawable.icona_stop);
            } else {
                VariabiliGlobali.getInstance().getViewNotifica().setImageViewResource(R.id.imgPlayStop, R.drawable.icona_play);
            }

            Notifica.getInstance().AggiornaNotifica();
        } */

        /* if (VariabiliGlobali.getInstance().getTxtCoords() != null) {
            if (locGPS != null) {
                VariabiliStatiche.getInstance().getTxtCoords().setText("qCxCy: " +
                        Integer.toString(ProgressivoDBGPS) + "\nMm: " +
                        Integer.toString(ProgressivoDBMM) + "\nLP: " +
                        Double.toString(locGPS.getLatitude()) + " - " + Double.toString(locGPS.getLongitude())+
                        "\nm: "+Float.toString(VariabiliStatiche.getInstance().getKmPercorsi()));
            } else {
                VariabiliStatiche.getInstance().getTxtCoords().setText("qCxCy: " +
                        Integer.toString(ProgressivoDBGPS) + "\nMm: " +
                        Integer.toString(ProgressivoDBMM)+ "\nm: "+
                        Float.toString(VariabiliStatiche.getInstance().getKmPercorsi()));
            }
        } */
    }

    public void ScriveValoriCoordinate(Location loc) {
        locazioneAttuale = loc;

        ScriveDatiAVideo();

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String oggi = formatter.format(todayDate);

        long l = dbGpsPos.aggiungiPosizione(oggi,
                VariabiliGlobali.getInstance().getSezione(),
                Integer.toString(ProgressivoDBGPS),
                Double.toString(loc.getLatitude()),
                Double.toString(loc.getLongitude()),
                Double.toString(loc.getAltitude()),
                Float.toString(loc.getSpeed()),
                Float.toString(loc.getAccuracy()));
        ProgressivoDBGPS++;
    }

    public void DisegnaPercorsoAttualeSuMappa() {
        if (VariabiliGlobali.getInstance().isMascheraAperta()) {
            if (VariabiliGlobali.getInstance().getmMap() != null) {
                VariabiliGlobali.getInstance().getmMap().clear();

                // DataDiVisualizzazioneMappa = Calendar.getInstance().getTime();

                // txtDataMappa.setText(oggi);

                // Utility.getInstance().ScriveKM();
                String Sezione = "";
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() != -1) {
                    Sezione = Integer.toString(VariabiliGlobali.getInstance().getSezioneDaVisualizzare());
                }

                Cursor cP = null;
                Cursor c1 = dbGpsPos.ottieniValoriGPSPerData(VariabiliGlobali.getInstance().getGiornoVisualizzato(), Sezione);
                if (c1 != null && c1.moveToFirst()) {
                    cP = c1;
                }

                DisegnaMappa dm = new DisegnaMappa();
                dm.DisegnaMappa(true, cP, locazioneAttuale);
                dm.CentraMappa();

                if (c1 != null)
                    c1.close();
                if (cP != null)
                    cP.close();
            }
        }
    }

    /* public void InstanziaNotifica() {
        Notifica.getInstance().RimuoviNotifica();

        Log.getInstance().ScriveLog("Instanzia notifica");

        Notifica.getInstance().setContext(MainActivity.getAppContext());
        Notifica.getInstance().setTitolo("Attivo: " + VariabiliGlobali.getInstance().isServizioGPS() + " - Ultima rilevazione: " + VariabiliGlobali.getInstance().getUltimoPunto());

        Notifica.getInstance().CreaNotifica();
    } */
}
