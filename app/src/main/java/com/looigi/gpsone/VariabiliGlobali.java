package com.looigi.gpsone;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;


import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.GoogleMap;

public class VariabiliGlobali {
    private static VariabiliGlobali instance = null;

    private VariabiliGlobali() {
    }

    public static VariabiliGlobali getInstance() {
        if(instance == null) {
            instance = new VariabiliGlobali();
        }

        return instance;
    }

    // private Activity FragmentActivityPrincipale;
    // private Context context;
    private final String NomeApplicazione = "GpsOne";
    private final String PercorsoDIR = Environment.getExternalStorageDirectory().getPath()+"/LooigiSoft/GpsOne";
    private boolean ePartito = false;
    private Location locGPS;
    private boolean GiaEntratoInMappa = false;
    private boolean azionaDebug = true;
    private int PuntiDisegnati;
    private NotificationCompat.Builder notification;
    private boolean disegnatoPrimoPunto = false;
    private String ultimoPunto = "";
    private RemoteViews viewNotifica;
    private String oggi;
    private boolean ImpostazioniAperte = false;
    private boolean ServizioGPS = true;
    private boolean MascheraAperta = true;
    private int Sezione = 1;
    private int SezioniGiorno = 0;
    private int SezioneDaVisualizzare = 0;
    private int SezioniGiornoVisualizzato = 0;
    private String GiornoVisualizzato = "";

    // Oggetti a video
    // private ImageView imgGps;
    /* private Switch sAccuracy;
    private Switch sGPSBetter;
    private EditText EdtMetriGPS;
    private EditText EdtAccuracy; */
    private GoogleMap mMap = null;
    private TextView txtData;
    // Oggetti a video

    // Valori impostazioni
    private int TEMPO_GPS = 1000;
    private int DISTANZA_GPS = 1;
    private boolean Accuracy = true;
    private boolean GPSBetter = true;
    private int AccuracyValue = 35;
    // Valori impostazioni

    /* public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    } */

    public String getGiornoVisualizzato() {
        return GiornoVisualizzato;
    }

    public void setGiornoVisualizzato(String giornoVisualizzato) {
        GiornoVisualizzato = giornoVisualizzato;
    }

    public int getSezioniGiornoVisualizzato() {
        return SezioniGiornoVisualizzato;
    }

    public void setSezioniGiornoVisualizzato(int sezioniGiornoVisualizzato) {
        SezioniGiornoVisualizzato = sezioniGiornoVisualizzato;
    }

    public int getSezioneDaVisualizzare() {
        return SezioneDaVisualizzare;
    }

    public void setSezioneDaVisualizzare(int sezioneDaVisualizzare) {
        SezioneDaVisualizzare = sezioneDaVisualizzare;
    }

    public int getSezioniGiorno() {
        return SezioniGiorno;
    }

    public void setSezioniGiorno(int sezioniGiorno) {
        SezioniGiorno = sezioniGiorno;
    }

    public int getSezione() {
        return Sezione;
    }

    public void setSezione(int sezione) {
        Sezione = sezione;
    }

    public boolean isMascheraAperta() {
        return MascheraAperta;
    }

    public void setMascheraAperta(boolean mascheraAperta) {
        MascheraAperta = mascheraAperta;
    }

    public boolean isImpostazioniAperte() {
        return ImpostazioniAperte;
    }

    public void setImpostazioniAperte(boolean impostazioniAperte) {
        ImpostazioniAperte = impostazioniAperte;
    }

    public String getOggi() {
        return oggi;
    }

    public void setOggi(String oggi) {
        this.oggi = oggi;
    }

    public TextView getTxtData() {
        return txtData;
    }

    public void setTxtData(TextView txtData) {
        this.txtData = txtData;
    }

    public RemoteViews getViewNotifica() {
        return viewNotifica;
    }

    public void setViewNotifica(RemoteViews viewNotifica) {
        this.viewNotifica = viewNotifica;
    }

    public String getUltimoPunto() {
        return ultimoPunto;
    }

    public void setUltimoPunto(String ultimoPunto) {
        this.ultimoPunto = ultimoPunto;
    }

    public boolean isDisegnatoPrimoPunto() {
        return disegnatoPrimoPunto;
    }

    public void setDisegnatoPrimoPunto(boolean disegnatoPrimoPunto) {
        this.disegnatoPrimoPunto = disegnatoPrimoPunto;
    }

    public NotificationCompat.Builder getNotification() {
        return notification;
    }

    public void setNotification(NotificationCompat.Builder notification) {
        this.notification = notification;
    }

    public int getPuntiDisegnati() {
        return PuntiDisegnati;
    }

    public void setPuntiDisegnati(int puntiDisegnati) {
        PuntiDisegnati = puntiDisegnati;
    }

    public boolean isAzionaDebug() {
        return azionaDebug;
    }

    public void setAzionaDebug(boolean azionaDebug) {
        this.azionaDebug = azionaDebug;
    }

    public boolean isGiaEntratoInMappa() {
        return GiaEntratoInMappa;
    }

    public void setGiaEntratoInMappa(boolean giaEntratoInMappa) {
        GiaEntratoInMappa = giaEntratoInMappa;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }


    public int getAccuracyValue() {
        return AccuracyValue;
    }

    public void setAccuracyValue(int accuracyValue) {
        AccuracyValue = accuracyValue;

        SharedPreferences sharedPref = MainActivity.getAppActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Detector.AccuracyValue", this.AccuracyValue);
        editor.apply();
    }

    public boolean isServizioGPS() {
        return ServizioGPS;
    }

    public void setServizioGPS(boolean servizioGPS) {
        ServizioGPS = servizioGPS;
    }

    /* public EditText getEdtMetriGPS() {
        return EdtMetriGPS;
    }

    public void setEdtMetriGPS(EditText edtMetriGPS) {
        EdtMetriGPS = edtMetriGPS;
    }

    public EditText getEdtAccuracy() {
        return EdtAccuracy;
    }

    public void setEdtAccuracy(EditText edtAccuracy) {
        EdtAccuracy = edtAccuracy;
    }

    public Switch getsGPSBetter() {
        return sGPSBetter;
    }

    public void setsGPSBetter(Switch sGPSBetter) {
        this.sGPSBetter = sGPSBetter;
    }

    public Switch getsAccuracy() {
        return sAccuracy;
    }

    public void setsAccuracy(Switch sAccuracy) {
        this.sAccuracy = sAccuracy;
    } */

    public Location getLocGPS() {
        return locGPS;
    }

    public void setLocGPS(Location locGPS) {
        this.locGPS = locGPS;
    }

    public boolean isGPSBetter() {
        return GPSBetter;
    }

    public void setGPSBetter(boolean GPSBetter) {
        this.GPSBetter = GPSBetter;

        SharedPreferences sharedPref = MainActivity.getAppActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("Detector.GPSBetter", GPSBetter);
        editor.apply();
    }

    public boolean isAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(boolean accuracy) {
        Accuracy = accuracy;

        SharedPreferences sharedPref = MainActivity.getAppActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("Detector.Accuracy", Accuracy);
        editor.apply();
    }

    public int getTEMPO_GPS() {
        return TEMPO_GPS;
    }

    public void setTEMPO_GPS(int TEMPO_GPS) {
        this.TEMPO_GPS = TEMPO_GPS;
    }

    public int getDISTANZA_GPS() {
        return DISTANZA_GPS;
    }

    public void setDISTANZA_GPS(int DISTANZA_GPS) {
        this.DISTANZA_GPS = DISTANZA_GPS;

        SharedPreferences sharedPref = MainActivity.getAppActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Detector.DistanzaGPS", this.DISTANZA_GPS);
        editor.apply();
    }

    /* public ImageView getImgGps() {
        return imgGps;
    }

    public void setImgGps(ImageView imgGps) {
        this.imgGps = imgGps;
    } */

    public boolean isePartito() {
        return ePartito;
    }

    public void setePartito(boolean ePartito) {
        this.ePartito = ePartito;
    }

    public String getNomeApplicazione() {
        return NomeApplicazione;
    }

    public String getPercorsoDIR() {
        return PercorsoDIR;
    }

    /* public Activity getFragmentActivityPrincipale() {
        return FragmentActivityPrincipale;
    }

    public void setFragmentActivityPrincipale(Activity fragmentActivityPrincipale) {
        FragmentActivityPrincipale = fragmentActivityPrincipale;
    } */

}
