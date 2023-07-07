package com.looigi.gpsone.gps;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.MainActivity;
import com.looigi.gpsone.Notifiche.GestioneNotifiche;
import com.looigi.gpsone.Utility;
import com.looigi.gpsone.VariabiliGlobali;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyLocationListener implements LocationListener {
    public MyLocationListener listener;
    public LocationManager locationManager;
    private Location previousBestLocation;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    public void AzionaServizio() {
        Log.getInstance().ScriveLog("Apertura servizio Location Service");
        // mContext = getApplicationContext();
        if (locationManager == null) {
            try {
                if (MainActivity.getAppActivity() != null) {
                    locationManager = (LocationManager) MainActivity.getAppActivity().getSystemService(Context.LOCATION_SERVICE);
                    Log.getInstance().ScriveLog("Location Manager creato");
                } else {
                    Log.getInstance().ScriveLog("Location Manager NON creato: Activity non esistente");
                }
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.getInstance().ScriveLog("Create location manager: "+errors.toString());
            }
        }

        getCurrentLocation();
    }

    boolean primoPunto = true;

    @Override
    public void onLocationChanged(@NonNull Location loc) {
        if (VariabiliGlobali.getInstance().isServizioGPS()) {
            float velocita = loc.getSpeed();
            if (velocita > .5 || primoPunto) {
                primoPunto = false;
                if (VariabiliGlobali.getInstance().isAccuracy()) {
                    if (loc.getAccuracy() <= VariabiliGlobali.getInstance().getAccuracyValue()) {
                        if (VariabiliGlobali.getInstance().isGPSBetter()) {
                            if (isBetterLocation(loc, previousBestLocation)) {
                                EsegueScritturaValori(loc);

                                ScriveDistanze(loc);
                                previousBestLocation = loc;
                            }
                        } else {
                            EsegueScritturaValori(loc);

                            ScriveDistanze(loc);
                            previousBestLocation = loc;
                        }
                    }
                } else {
                    if (VariabiliGlobali.getInstance().isGPSBetter()) {
                        if (isBetterLocation(loc, previousBestLocation)) {
                            EsegueScritturaValori(loc);

                            ScriveDistanze(loc);
                            previousBestLocation = loc;
                        }
                    } else {
                        EsegueScritturaValori(loc);

                        ScriveDistanze(loc);
                        previousBestLocation = loc;
                    }
                }
            }
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > VariabiliGlobali.getInstance().getTEMPO_GPS();
        boolean isSignificantlyOlder = timeDelta < -VariabiliGlobali.getInstance().getTEMPO_GPS();
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.getInstance().ScriveLog("Provider disabled");

        getCurrentLocation();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.getInstance().ScriveLog("Provider enabled");

        getCurrentLocation();
    }

    @Override
    public void onStatusChanged(@NonNull String provider, int status, Bundle extras) {
        Log.getInstance().ScriveLog("Provider status changed: "+Integer.toString(status));
    }

    // Location lastLoc;

    private void EsegueScritturaValori(Location loc) {
        // // Log l = new Log(VariabiliGlobali.getInstance().getNomeLogGPS());
        Log.getInstance().ScriveLog("onLocationChanged: " + Double.toString(loc.getLatitude()) + " - " + Double.toString(loc.getLongitude()));
        try {
            /* boolean ok = true;

            if (lastLoc != null) {
                double diffLat = Math.abs(loc.getLatitude() - lastLoc.getLatitude());
                double diffLon = Math.abs(loc.getLongitude() - lastLoc.getLongitude());

                if (diffLat > 2.5 || diffLon > .15) {
                    ok = false;
                }
            }

            if (ok) { */
                NumberFormat f2 = new DecimalFormat("00");
                NumberFormat f3 = new DecimalFormat("000");
                Calendar c = Calendar.getInstance();
                String hour = f2.format(c.get(Calendar.HOUR_OF_DAY));
                String minutes = f2.format(c.get(Calendar.MINUTE));
                String seconds = f2.format(c.get(Calendar.SECOND));
                String mseconds = f3.format(c.get(Calendar.MILLISECOND));

                String dat = hour + ":" + minutes + ":" + seconds + "." + mseconds;

                VariabiliGlobali.getInstance().setUltimoPunto(dat);

                Utility.getInstance().ScriveValoriCoordinate(loc);
                VariabiliGlobali.getInstance().setLocGPS(loc);

                // if (VariabiliGlobali.getInstance().isSeguePercorso()) {
                Utility.getInstance().DisegnaPercorsoAttualeSuMappa();

                Utility.getInstance().ScriveDatiAVideo();
            // }

            // lastLoc = loc;
            // }
        } catch (Exception e) {
            // StringWriter errors = new StringWriter();
            // e.printStackTrace(new PrintWriter(errors));
            Log.getInstance().ScriveLog("Scrittura valori a video: " + Utility.getInstance().PrendeErroreDaException(e));
        }
    }

    private void getCurrentLocation() {
        Log.getInstance().ScriveLog("Get current location");

        try {
            // if (VariabiliGlobali.getInstance().getImgGps()!=null) {
                // VariabiliGlobali.getInstance().getImgGps().setVisibility(LinearLayout.VISIBLE);
                // VariabiliGlobali.getInstance().getViewNotifica().setViewVisibility(R.id.imgCeSegnale, LinearLayout.VISIBLE);
                VariabiliGlobali.getInstance().setCeSegnale(true);
                GestioneNotifiche.getInstance().AggiornaNotifica();
            // }
            assert locationManager != null;
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.getInstance().ScriveLog("Get current location OK. GPS enabled " + isGpsEnabled + " - Network enabled "+isNetworkEnabled);
        } catch (Exception ex) {
            Log.getInstance().ScriveLog("Get current location ERROR: " + ex.getMessage());
            // if (VariabiliGlobali.getInstance().getImgGps()!=null) {
                // VariabiliGlobali.getInstance().getImgGps().setVisibility(LinearLayout.GONE);
            // if (VariabiliGlobali.getInstance().getViewNotifica() != null) {
            //     VariabiliGlobali.getInstance().getViewNotifica().setViewVisibility(R.id.imgCeSegnale, LinearLayout.GONE);
                VariabiliGlobali.getInstance().setCeSegnale(false);
                GestioneNotifiche.getInstance().AggiornaNotifica();
            // }
            // }
            // ex.printStackTrace();
        }

        if (!isGpsEnabled && !isNetworkEnabled) {
            showSettingsAlert();
        }
        // listener = new MyLocationListener();

        try {
            // if (VariabiliGlobali.getInstance().getImgGps()!=null) {
                // VariabiliGlobali.getInstance().getImgGps().setVisibility(LinearLayout.VISIBLE);
            /* if (VariabiliGlobali.getInstance().getViewNotifica() != null) {
                VariabiliGlobali.getInstance().getViewNotifica().setViewVisibility(R.id.imgCeSegnale, LinearLayout.VISIBLE); */
                VariabiliGlobali.getInstance().setCeSegnale(true);
                GestioneNotifiche.getInstance().AggiornaNotifica();
            /* } else {
                Log.getInstance().ScriveLog("ERRORE: Notifica nulla...");
            } */
            // }
            if (isGpsEnabled) {
                // try {
                    // VariabiliGlobali.getInstance().setAccuracyValue(25);
                    // VariabiliGlobali.getInstance().getsGPSBetter().setChecked(true);
                    // VariabiliGlobali.getInstance().setAccuracy(true);
                    // VariabiliGlobali.getInstance().getEdtAccuracy().setText("25");
                    // VariabiliGlobali.getInstance().setDISTANZA_GPS(5, true);
                    // VariabiliGlobali.getInstance().getEdtMetriGPS().setText("5");
                    // VariabiliGlobali.getInstance().getsAccuracy().setChecked(VariabiliGlobali.getInstance().getsAccuracy().isChecked());
                    // VariabiliGlobali.getInstance().setGPSBetter(VariabiliGlobali.getInstance().getsGPSBetter().isChecked(), true);
                    // VariabiliGlobali.getInstance().setAccuracy(VariabiliGlobali.getInstance().isAccuracy());
                    // VariabiliGlobali.getInstance().getBtnSalvaAcc().setEnabled(true);
                    // VariabiliGlobali.getInstance().getEdtAccuracy().setEnabled(true);
                // } catch (Exception  ignored) {

                // }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        VariabiliGlobali.getInstance().getTEMPO_GPS(),
                        VariabiliGlobali.getInstance().getDISTANZA_GPS(),
                        this);
                Log.getInstance().ScriveLog("locationManager istanziato su GPS");

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null){
                    VariabiliGlobali.getInstance().setLocGPS(location);
                    Log.getInstance().ScriveLog("Ultima posizione: " + Double.toString(location.getLatitude()) + "-" + Double.toString(location.getLongitude()));
                }
            }
            if (isNetworkEnabled && !isGpsEnabled) {
                /* try {
                    VariabiliGlobali.getInstance().setGPSBetter(false);
                    VariabiliGlobali.getInstance().setAccuracy(false);
                    VariabiliGlobali.getInstance().setDISTANZA_GPS(35);
                    VariabiliGlobali.getInstance().getEdtMetriGPS().setText("75");
                    VariabiliGlobali.getInstance().getsAccuracy().setChecked(VariabiliGlobali.getInstance().getsAccuracy().isChecked());
                    VariabiliGlobali.getInstance().setGPSBetter(VariabiliGlobali.getInstance().getsGPSBetter().isChecked(), true);
                    VariabiliGlobali.getInstance().setAccuracy(VariabiliGlobali.getInstance().getsAccuracy().isChecked(), true);
                    // VariabiliGlobali.getInstance().getBtnSalvaAcc().setEnabled(false);
                    VariabiliGlobali.getInstance().getEdtAccuracy().setEnabled(false);
                } catch (Exception  ignored) {

                } */

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        VariabiliGlobali.getInstance().getTEMPO_GPS(),
                        VariabiliGlobali.getInstance().getDISTANZA_GPS(),
                        this);
                Log.getInstance().ScriveLog("locationManager istanziato su NETWORK");

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location!=null) {
                    VariabiliGlobali.getInstance().setLocGPS(location);
                    Log.getInstance().ScriveLog("Ultima posizione: " + Double.toString(location.getLatitude()) + "-" + Double.toString(location.getLongitude()));
                }
            }

            if (!isNetworkEnabled && !isGpsEnabled) {
                // if (VariabiliGlobali.getInstance().getImgGps()!=null) {
                    // VariabiliGlobali.getInstance().getImgGps().setVisibility(LinearLayout.GONE);
                // if (VariabiliGlobali.getInstance().getViewNotifica() != null) {
                    VariabiliGlobali.getInstance().setCeSegnale(false);
                    // VariabiliGlobali.getInstance().getViewNotifica().setViewVisibility(R.id.imgCeSegnale, LinearLayout.GONE);
                    GestioneNotifiche.getInstance().AggiornaNotifica();
                // }
                Log.getInstance().ScriveLog("Nessun provider impostato");
            }

            Utility.getInstance().ScriveDatiAVideo();
        } catch (SecurityException e) {
            Log.getInstance().ScriveLog("locationManager ERROR: " + e.getMessage());
            // if (VariabiliGlobali.getInstance().getImgGps()!=null) {
                // VariabiliGlobali.getInstance().getImgGps().setVisibility(LinearLayout.GONE);
            // if (VariabiliGlobali.getInstance().getViewNotifica() != null) {
                // VariabiliGlobali.getInstance().getViewNotifica().setViewVisibility(R.id.imgCeSegnale, LinearLayout.GONE);
            VariabiliGlobali.getInstance().setCeSegnale(false);
            GestioneNotifiche.getInstance().AggiornaNotifica();
            // }
            // e.printStackTrace();
        }
    }

    private void ScriveDistanze(Location loc) {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String oggi = formatter.format(todayDate);

        /* if (VariabiliGlobali.getInstance().getDbGpsPos()==null) {
            db_dati dbgps = new db_dati();
            dbgps.CreazioneTabelle();
            VariabiliGlobali.getInstance().setDbGpsPos(dbgps);
        }

        if (VariabiliGlobali.getInstance().getDbGpsPos()!=null) {
            if (loc != null && previousBestLocation != null) {
                float distanceInMeters = loc.distanceTo(previousBestLocation);
                float km = VariabiliGlobali.getInstance().getKmPercorsi() + distanceInMeters;
                VariabiliGlobali.getInstance().getDbGpsPos().aggiornaDistanza(oggi, Float.toString(km));
            }
        } else {
            // Log l = new Log(VariabiliGlobali.getInstance().getNomeLogGPS());
            Log.getInstance().ScriveLog("Scrittura distanze su db: DB non aperto");
        } */
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void showSettingsAlert() {
        Context c = MainActivity.getAppContext();
        if (c != null) {
            Toast.makeText(c, "GPS is disabled in your device. Please Enable it...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.getAppContext().startActivity(intent);
        } else {
            Log.getInstance().ScriveLog("GPS Disabilitato. Per favore abilitarlo...");
        }
    }
}
