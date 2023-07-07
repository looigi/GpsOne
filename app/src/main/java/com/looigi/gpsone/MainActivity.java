package com.looigi.gpsone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.looigi.gpsone.AutoStart.RunServiceOnBoot;
import com.looigi.gpsone.Notifiche.GestioneNotifiche;
import com.looigi.gpsone.Notifiche.InstanziamentoNotifica;
// import com.looigi.gpsone.gps.LocationService;

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

        Intent intent1 = new Intent(MainActivity.this, RunServiceOnBoot.class);
        startService(intent1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
            );
            startActivityForResult(intent, 124);
        }

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
            // Utility.getInstance().InstanziaNotifica();

            Intent intent = new Intent(this, InstanziamentoNotifica.class);
            startService(intent);
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
        VariabiliGlobali.getInstance().setTxtSezione(txtSezione);
        Utility.getInstance().ScriveSezioni();

        View fgmMappa = (View) findViewById(R.id.map);

        TextView txtData = (TextView) findViewById(R.id.txtData);
        VariabiliGlobali.getInstance().setTxtData(txtData);

        ImageView imgIndietroSezione = (ImageView) findViewById(R.id.imgIndietroSezione);
        ImageView imgAvantiSezione = (ImageView) findViewById(R.id.imgAvantiSezione);
        Switch switchTutteSezioni = (Switch) findViewById(R.id.switchTutteSezioni);

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

                    db_dati db = new db_dati();
                    String rit = db.contaValoriGPSPerData(oggi);
                    String[] r = rit.split(";");
                    // VariabiliGlobali.getInstance().setPuntiDisegnati(Integer.parseInt(r[0]));
                    VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(Integer.parseInt(r[1]));
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(-1);

                    imgAvantiSezione.setVisibility(LinearLayout.GONE);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);
                    switchTutteSezioni.setChecked(true);

                    Utility.getInstance().ScriveSezioni();
                    Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
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

                    db_dati db = new db_dati();
                    String rit = db.contaValoriGPSPerData(oggi);
                    String[] r = rit.split(";");
                    // VariabiliGlobali.getInstance().setPuntiDisegnati(Integer.parseInt(r[0]));
                    VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(Integer.parseInt(r[1]));
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(-1);

                    imgAvantiSezione.setVisibility(LinearLayout.GONE);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);
                    switchTutteSezioni.setChecked(true);

                    Utility.getInstance().ScriveSezioni();
                    Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
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

        LinearLayout layAccuracy = (LinearLayout) findViewById(R.id.layAccuracy);
        LinearLayout layGPSBetter = (LinearLayout) findViewById(R.id.layGPSBetter);
        Switch switchAccuracy = (Switch) findViewById(R.id.switchAccuracy);
        Switch switchGPSBetter = (Switch) findViewById(R.id.switchGPSBetter);

        switchAccuracy.setChecked(VariabiliGlobali.getInstance().isAccuracy());
        EditText edtAccuracy= (EditText) findViewById(R.id.edtAccuracy);
        edtAccuracy.setText(Integer.toString(VariabiliGlobali.getInstance().getAccuracyValue()));
        edtAccuracy.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                VariabiliGlobali.getInstance().setAccuracyValue(Integer.parseInt(edtAccuracy.getText().toString()));

                db_dati db = new db_dati();
                db.scriveImpostazioni();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        if (VariabiliGlobali.getInstance().isAccuracy()) {
            layAccuracy.setVisibility(LinearLayout.VISIBLE);
            layGPSBetter.setVisibility(LinearLayout.GONE);
            VariabiliGlobali.getInstance().setGPSBetter(false);
            switchGPSBetter.setChecked(false);
        } else {
            layAccuracy.setVisibility(LinearLayout.GONE);
            layGPSBetter.setVisibility(LinearLayout.VISIBLE);
            VariabiliGlobali.getInstance().setGPSBetter(true);
        }
        switchAccuracy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               VariabiliGlobali.getInstance().setAccuracy(isChecked);

               if (isChecked) {
                   layAccuracy.setVisibility(LinearLayout.VISIBLE);
                   layGPSBetter.setVisibility(LinearLayout.GONE);
                   VariabiliGlobali.getInstance().setGPSBetter(false);
                   switchGPSBetter.setChecked(false);
               } else {
                   layAccuracy.setVisibility(LinearLayout.GONE);
                   layGPSBetter.setVisibility(LinearLayout.VISIBLE);
                   VariabiliGlobali.getInstance().setGPSBetter(true);
               }

               db_dati db = new db_dati();
               db.scriveImpostazioni();
           }
        });

        EditText edtTempoGPS = (EditText) findViewById(R.id.edtTempoGPS);
        edtTempoGPS.setText(Integer.toString(VariabiliGlobali.getInstance().getTEMPO_GPS()));
        edtTempoGPS.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                VariabiliGlobali.getInstance().setTEMPO_GPS(Integer.parseInt(edtTempoGPS.getText().toString()));

                db_dati db = new db_dati();
                db.scriveImpostazioni();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        EditText edtDistanzaGPS = (EditText) findViewById(R.id.edtDistanzaGPS);
        edtDistanzaGPS.setText(Integer.toString(VariabiliGlobali.getInstance().getDISTANZA_GPS()));
        edtDistanzaGPS.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                VariabiliGlobali.getInstance().setDISTANZA_GPS(Integer.parseInt(edtDistanzaGPS.getText().toString()));

                db_dati db = new db_dati();
                db.scriveImpostazioni();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        if (VariabiliGlobali.getInstance().isGPSBetter()) {
            layGPSBetter.setVisibility(LinearLayout.VISIBLE);
            layAccuracy.setVisibility(LinearLayout.GONE);
            VariabiliGlobali.getInstance().setAccuracy(false);
            switchAccuracy.setChecked(false);
        } else {
            layGPSBetter.setVisibility(LinearLayout.GONE);
            layAccuracy.setVisibility(LinearLayout.VISIBLE);
            VariabiliGlobali.getInstance().setAccuracy(false);
        }
        switchGPSBetter.setChecked(VariabiliGlobali.getInstance().isGPSBetter());
        switchGPSBetter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VariabiliGlobali.getInstance().setGPSBetter(isChecked);

                if (isChecked) {
                    layGPSBetter.setVisibility(LinearLayout.VISIBLE);
                    layAccuracy.setVisibility(LinearLayout.GONE);
                    VariabiliGlobali.getInstance().setAccuracy(false);
                    switchAccuracy.setChecked(false);
                } else {
                    layGPSBetter.setVisibility(LinearLayout.GONE);
                    layAccuracy.setVisibility(LinearLayout.VISIBLE);
                    VariabiliGlobali.getInstance().setAccuracy(false);
                }

                db_dati db = new db_dati();
                db.scriveImpostazioni();
            }
        });

        imgAvantiSezione.setVisibility(LinearLayout.GONE);
        imgIndietroSezione.setVisibility(LinearLayout.GONE);

        switchTutteSezioni.setChecked(true);
        switchTutteSezioni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(-1);
                    imgAvantiSezione.setVisibility(LinearLayout.GONE);
                    imgIndietroSezione.setVisibility(LinearLayout.GONE);
                } else {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato());
                    imgAvantiSezione.setVisibility(LinearLayout.VISIBLE);
                    imgIndietroSezione.setVisibility(LinearLayout.VISIBLE);
                }

                Utility.getInstance().ScriveSezioni();
                Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
            }
        });

        imgIndietroSezione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() > 0) {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(VariabiliGlobali.getInstance().getSezioneDaVisualizzare() - 1);
                }

                Utility.getInstance().ScriveSezioni();
                Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
            }
        });

        imgAvantiSezione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() < VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato()) {
                    VariabiliGlobali.getInstance().setSezioneDaVisualizzare(VariabiliGlobali.getInstance().getSezioneDaVisualizzare() + 1);
                }

                Utility.getInstance().ScriveSezioni();
                Utility.getInstance().DisegnaPercorsoAttualeSuMappa();
            }
        });

        ImageView imgUscita = (ImageView) findViewById(R.id.imgChiudi);
        imgUscita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GestioneNotifiche.getInstance().RimuoviNotifica();

                System.exit(0);
            }
        });

        Button btnEliminaPercorso = (Button) findViewById(R.id.btnEliminaPercorso);
        btnEliminaPercorso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String altroMessaggio = "";
                if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() == -1) {
                    altroMessaggio = "Si vuole eliminare il percorso completo del giorno ?";
                } else {
                    altroMessaggio = "Si vuole eliminare la sezione " + (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() + 1) + "/" +
                            (VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato() + 1) + " del giorno ?";
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Eliminazione percorso");
                alert.setMessage(altroMessaggio);
                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sezione = "";
                        if (VariabiliGlobali.getInstance().getSezioneDaVisualizzare() > -1) {
                            sezione = Integer.toString(VariabiliGlobali.getInstance().getSezioneDaVisualizzare());
                        }
                        db_dati db = new db_dati();
                        db.eliminaPercorso(VariabiliGlobali.getInstance().getGiornoVisualizzato(), sezione);

                        Utility.getInstance().DisegnaPercorsoAttualeSuMappa();

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
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
            VariabiliGlobali.getInstance().setSezioneDaVisualizzare(-1);
        }

        Utility.getInstance().ScriveData();
        Utility.getInstance().ScriveSezioni();
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