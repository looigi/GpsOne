package com.looigi.gpsone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.looigi.gpsone.gps.LocationService;
import com.looigi.gpsone.notifiche.Notifica;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean ciSonoPermessi;
    private static Context context;
    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();
        MainActivity.activity = this;

        // VariabiliGlobali.getInstance().setContext(this);
        // VariabiliGlobali.getInstance().setFragmentActivityPrincipale(this);

        db_dati dbgps = new db_dati();
        dbgps.CreazioneTabelle();
        Utility.getInstance().setDbGpsPos(dbgps);

        if (!VariabiliGlobali.getInstance().isePartito()) {
            Log.getInstance().EliminaFileLog();
            Log.getInstance().ScriveLog(">>>>>>>>>>>>>>>>>>>>>>>>NUOVA SESSIONE<<<<<<<<<<<<<<<<<<<<<<<<");

            Permessi p = new Permessi();
            ciSonoPermessi = p.ControllaPermessi();

            if (ciSonoPermessi) {
                EsegueEntrata();
            }
        } else {
            EsegueEntrata();
        }
    }

    public static Context getAppContext() {
        if (MainActivity.context != null) {
            Log.getInstance().ScriveLog("Context del main activity NON NULLO");
        } else {
            Log.getInstance().ScriveLog("Context del main activity NULLO ");
        }

        return MainActivity.context;
    }

    public static Activity getAppActivity() {
        if (MainActivity.context != null) {
            Log.getInstance().ScriveLog("Activity del main activity NON NULLO");
        } else {
            Log.getInstance().ScriveLog("Activity del main activity NULLO ");
        }

        return MainActivity.activity;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.getInstance().ScriveLog("***Resume activity***");

        MainActivity.context = getApplicationContext();
        MainActivity.activity = this;

        VariabiliGlobali.getInstance().setMascheraAperta(true);

        Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
        Utility.getInstance().ScriveDatiAVideo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!ciSonoPermessi) {
            int index = 0;
            Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
            for (String permission : permissions) {
                PermissionsMap.put(permission, grantResults[index]);
                index++;
            }

            EsegueEntrata();
        }
    }

    private void EsegueEntrata() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db_dati dbgps = new db_dati();
        dbgps.CreazioneTabelle();
        dbgps.caricaImpostazioni();
        // dbgps.PulisceDati();

        if (!VariabiliGlobali.getInstance().isePartito()) {
            Utility.getInstance().InstanziaNotifica();

            startForegroundService(new Intent(getApplicationContext(), LocationService.class));
        }

        /* VariabiliGlobali.getInstance().getFragmentActivityPrincipale().startService(new Intent(
                VariabiliGlobali.getInstance().getFragmentActivityPrincipale(),
                ServizioBackground.class)); */

        ImpostaOggetti();
    }

    private void ImpostaOggetti() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String oggi = formatter.format(todayDate);
        VariabiliGlobali.getInstance().setOggi(oggi);
        VariabiliGlobali.getInstance().setGiornoVisualizzato(oggi);
        VariabiliGlobali.getInstance().setSezioneDaVisualizzare(0);

        TextView txtSezione = (TextView) findViewById(R.id.txtSezione);
        Utility.getInstance().ScriveSezioni(txtSezione);

        View fgmMappa = (View) findViewById(R.id.map);

        TextView txtData = (TextView) findViewById(R.id.txtData);
        VariabiliGlobali.getInstance().setTxtData(txtData);

        ImageView imgIndietroSezione = (ImageView) findViewById(R.id.imgIndietroSezione);
        ImageView imgAvantiSezione = (ImageView) findViewById(R.id.imgAvantiSezione);

        ImageView imgIndietro = (ImageView) findViewById(R.id.imgIndietro);
        imgIndietro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Datella = VariabiliGlobali.getInstance().getGiornoVisualizzato();
                String[] dd = Datella.split("/");
                Datella = dd[2] + "-" + dd[1] + "-" + dd[0] + " 00:00:00";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(Datella, formatter);
                    LocalDateTime yesterdayDate = dateTime.minusDays(1);
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String oggi = formatter2.format(yesterdayDate);
                    VariabiliGlobali.getInstance().setGiornoVisualizzato(oggi);
                    VariabiliGlobali.getInstance().getTxtData().setText(oggi);
                    Utility.getInstance().DisegnaPercorsoAttualeSuMappa();

                    db_dati db = new db_dati();
                    String rit = db.contaValoriGPSPerData(oggi);
                    String[] r = rit.split(";");
                    // VariabiliGlobali.getInstance().setPuntiDisegnati(Integer.parseInt(r[0]));
                    VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(Integer.parseInt(r[1]));
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(0);

                    imgAvantiSezione.setVisibility(LinearLayout.VISIBLE);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);

                    Utility.getInstance().ScriveSezioni(txtSezione);
                } catch (Exception e) {
                    Log.getInstance().ScriveLog("Errore nella conversione della data: " + Utility.getInstance().PrendeErroreDaException(e));
                }
            }
        });
        ImageView imgAvanti = (ImageView) findViewById(R.id.imgAvanti);
        imgAvanti.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Datella = VariabiliGlobali.getInstance().getGiornoVisualizzato();
                String[] dd = Datella.split("/");
                Datella = dd[2] + "-" + dd[1] + "-" + dd[0] + " 00:00:00";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(Datella, formatter);
                    LocalDateTime yesterdayDate = dateTime.plusDays(1);
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String oggi = formatter2.format(yesterdayDate);
                    VariabiliGlobali.getInstance().setGiornoVisualizzato(oggi);
                    VariabiliGlobali.getInstance().getTxtData().setText(oggi);
                    Utility.getInstance().DisegnaPercorsoAttualeSuMappa();

                    db_dati db = new db_dati();
                    String rit = db.contaValoriGPSPerData(oggi);
                    String[] r = rit.split(";");
                    // VariabiliGlobali.getInstance().setPuntiDisegnati(Integer.parseInt(r[0]));
                    VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(Integer.parseInt(r[1]));
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(0);

                    imgAvantiSezione.setVisibility(LinearLayout.VISIBLE);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);

                    Utility.getInstance().ScriveSezioni(txtSezione);
                } catch (Exception e) {
                    Log.getInstance().ScriveLog("Errore nella conversione della data: " + Utility.getInstance().PrendeErroreDaException(e));
                }
            }
        });

        // IMPOSTAZIONI
        LinearLayout layImpostazioni = (LinearLayout) findViewById(R.id.layImpostazioni);
        ImageView imgLinguetta = (ImageView) findViewById(R.id.imgLinguettaSinistra);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 2;
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams params = layImpostazioni.getLayoutParams();
        params.width = (width * 2) / 4;
        layImpostazioni.setLayoutParams(params);

        VariabiliGlobali.getInstance().setImpostazioniAperte(false);
        layImpostazioni.setVisibility(LinearLayout.GONE);
        imgLinguetta.setX(0);
        imgLinguetta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VariabiliGlobali.getInstance().isImpostazioniAperte()) {
                    VariabiliGlobali.getInstance().setImpostazioniAperte(false);
                    layImpostazioni.setVisibility(LinearLayout.GONE);
                    imgLinguetta.setX(0);
                } else {
                    VariabiliGlobali.getInstance().setImpostazioniAperte(true);
                    layImpostazioni.setVisibility(LinearLayout.VISIBLE);
                    int doveX = (width * 2) / 4;
                    imgLinguetta.setX(doveX);
                }
            }
        });

        Switch switchAccuracy = (Switch) findViewById(R.id.switchAccuracy);
        switchAccuracy.setChecked(VariabiliGlobali.getInstance().isAccuracy());
        switchAccuracy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               VariabiliGlobali.getInstance().setAccuracy(isChecked);

               db_dati db = new db_dati();
               db.scriveImpostazioni();
           }
        });

        Switch switchGPSBetter = (Switch) findViewById(R.id.switchGPSBetter);
        switchGPSBetter.setChecked(VariabiliGlobali.getInstance().isGPSBetter());
        switchGPSBetter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VariabiliGlobali.getInstance().setGPSBetter(isChecked);

                db_dati db = new db_dati();
                db.scriveImpostazioni();
            }
        });

        imgAvantiSezione.setVisibility(LinearLayout.VISIBLE);
        imgIndietroSezione.setVisibility(LinearLayout.GONE);

        imgIndietroSezione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() > 0) {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(VariabiliGlobali.getInstance().getSezioneDaVisualizzare() - 1);
                    imgAvantiSezione.setVisibility(LinearLayout.VISIBLE);
                } else {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(0);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);
                }

                Utility.getInstance().ScriveSezioni(txtSezione);
            }
        });

        imgAvantiSezione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() < VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato()) {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(VariabiliGlobali.getInstance().getSezioneDaVisualizzare() + 1);
                    imgIndietroSezione.setVisibility(LinearLayout.VISIBLE);
                } else {
                    imgAvantiSezione.setVisibility(LinearLayout.GONE);
                }

                Utility.getInstance().ScriveSezioni(txtSezione);
            }
        });

        ImageView imgUscita = (ImageView) findViewById(R.id.imgChiudi);
        imgUscita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Notifica.getInstance().RimuoviNotifica();

                System.exit(0);
            }
        });

        if (!VariabiliGlobali.getInstance().isePartito()) {
            db_dati db = new db_dati();
            String rit = db.contaValoriGPSPerData(oggi);
            String[] r = rit.split(";");
            VariabiliGlobali.getInstance().setPuntiDisegnati(Integer.parseInt(r[0]));
            VariabiliGlobali.getInstance().setSezioniGiorno(Integer.parseInt(r[1]));
            VariabiliGlobali.getInstance().setSezione(Integer.parseInt(r[1]));

            VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(Integer.parseInt(r[1]));
            VariabiliGlobali.getInstance().setSezioneDaVisualizzare(0);
        }

        Utility.getInstance().ScriveData();
        Utility.getInstance().ScriveSezioni(txtSezione);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag()!=null) {
                    String tag = marker.getTag().toString();
                    String c[] = tag.split(";",-1);
                }

                return false;
            }
        });

        VariabiliGlobali.getInstance().setmMap(googleMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.getInstance().ScriveLog("--->Attività distrutta<---");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        Log.getInstance().ScriveLog("--->Tasto premuto: " + keyCode + "<---");

        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                VariabiliGlobali.getInstance().setMascheraAperta(false);
                moveTaskToBack(true);

                return false;
        }

        return false;
    }
}