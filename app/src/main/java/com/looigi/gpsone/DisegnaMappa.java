package com.looigi.gpsone;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DisegnaMappa {
    private LatLngBounds.Builder builder;
    private int Punti;

    public void DisegnaMappa(boolean NuovaMappa, Cursor cursPercorso, Location locGPS) {
        // PolylineOptions puntiMappa = new PolylineOptions().width(7).color(Color.BLUE).geodesic(true);
        List<LatLng> currentSegment = new ArrayList<>();
        builder = new LatLngBounds.Builder();
        boolean primo=true;

        int height = 80;
        int width = 80;
        Punti = 0;

        BitmapDrawable bitmapdraw = (BitmapDrawable) MainActivity.getAppContext().getResources().getDrawable(R.drawable.partenza);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap markerPartenza = Bitmap.createScaledBitmap(b, width, height, false);

        bitmapdraw = (BitmapDrawable) MainActivity.getAppContext().getResources().getDrawable(R.drawable.marker_fine);
        b = bitmapdraw.getBitmap();
        Bitmap markerFine = Bitmap.createScaledBitmap(b, width, height, false);

        LatLng ultL=null;
        String ultOra="";
        String ultData="";
        int oldColor=-1;
        int actualColor=-1;
        LatLng l = null;
        // String x = "";
        String oldSezione = "***";

        if (cursPercorso != null) {
            do {
                String Sezione = cursPercorso.getString(1);

                if (!oldSezione.equals("***") && !Sezione.equals(oldSezione)) {
                    VariabiliGlobali.getInstance().getmMap().addMarker(new MarkerOptions()
                            .position(ultL)
                            .title(ultOra)
                            .snippet(ultData)
                            .zIndex(2F)
                            .icon(BitmapDescriptorFactory.fromBitmap(markerFine)));
                }

                double lat = Double.parseDouble(cursPercorso.getString(3));
                double lon = Double.parseDouble(cursPercorso.getString(4));

                // String xx = lat + "x" + lon + ";";
                // if (!x.contains(xx)) {
                //     x += xx;

                    if (oldColor == -1) {
                        float f = 30F;
                        try {
                            f = Float.parseFloat(cursPercorso.getString(7));
                        } catch (Exception ignored) {
                            int i = 0;
                        }
                        oldColor = ConvertSpeedToColor(f);
                    } else {
                        if (oldColor != actualColor) {
                            oldColor = actualColor;

                            VariabiliGlobali.getInstance().getmMap().addPolyline(new PolylineOptions()
                                    .addAll(currentSegment)
                                    .color(actualColor)
                                    .width(7));

                            currentSegment.clear();

                            currentSegment.add(ultL);
                            builder.include(ultL);
                            Punti++;
                        }
                    }

                    float f = 30F;
                    try {
                        f = Float.parseFloat(cursPercorso.getString(7));
                    } catch (Exception ignored) {
                        int i = 0;
                    }
                    actualColor = ConvertSpeedToColor(f);
                    l = new LatLng(lat, lon);
                    ultL = l;

                    // puntiMappa.add(l);
                if (oldSezione.equals("***") || Sezione.equals(oldSezione)) {
                    currentSegment.add(l);
                    builder.include(l);
                    Punti++;
                }

                    String ora = cursPercorso.getString(6);
                    ora = ora.substring(0, 2) + ":" + ora.substring(2, 4) + ":" + ora.substring(4, 6) + "." + ora.substring(6, 9);
                    ultOra = ora;
                    ultData = cursPercorso.getString(0);

                    if (primo) {
                        primo = false;

                        VariabiliGlobali.getInstance().getmMap().addMarker(new MarkerOptions()
                                .position(l)
                                .title(ora)
                                .snippet(cursPercorso.getString(0))
                                .zIndex(2F)
                                .rotation(45)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerPartenza)));
                    }
                // }

                if (!oldSezione.equals("***") && !Sezione.equals(oldSezione)) {
                    VariabiliGlobali.getInstance().getmMap().addPolyline(new PolylineOptions()
                            .addAll(currentSegment)
                            .color(actualColor)
                            .width(7));

                    currentSegment.clear();

                    oldSezione = Sezione;
                    primo = true;
                } else {
                    if (oldSezione.equals("***")) {
                        oldSezione = Sezione;
                    }
                }
            } while (cursPercorso.moveToNext());

            if (ultL != null) {
                VariabiliGlobali.getInstance().getmMap().addMarker(new MarkerOptions()
                        .position(ultL)
                        .title(ultOra)
                        .snippet(ultData)
                        .zIndex(2F)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerFine)));
            }

            VariabiliGlobali.getInstance().getmMap().addPolyline(new PolylineOptions()
                    .addAll(currentSegment)
                    .color(actualColor)
                    .width(7));

            currentSegment.clear();
        }

        if (NuovaMappa && locGPS!=null) {
            // if (locGPS.getLatitude() != 41.8648184 && locGPS.getLongitude() != 12.3593419 && locGPS.getAltitude()!=50) {
                if (ultL != null) {
                    locGPS.setLongitude(ultL.longitude);
                    locGPS.setLatitude(ultL.latitude);

                    LatLng lActual = new LatLng(locGPS.getLatitude(), locGPS.getLongitude());
                    // puntiMappa.add(lActual);
                    builder.include(lActual);
                    Punti++;

                    VariabiliGlobali.getInstance().getmMap().addMarker(new MarkerOptions().position(lActual).title("Posizione Attuale"));
                }
            // }
        }

        VariabiliGlobali.getInstance().setPuntiDisegnati(Punti);
        Utility.getInstance().ScriveDatiAVideo();

        if (!VariabiliGlobali.getInstance().isGiaEntratoInMappa()) {
            VariabiliGlobali.getInstance().setGiaEntratoInMappa(true);

            CentraMappa();
        }
    }

    private int ConvertSpeedToColor(float fspeed) {
        float speed = fspeed * 3.6F;

        if (fspeed!=30) {
            int i=0;
        }
        int color = Color.TRANSPARENT;
        if (speed < 5) {
            color = Color.argb(255,0,0,200);
        } else if (speed < 25) {
            color = Color.argb(255,0,200,0);
        } else if (speed < 50) {
            color = Color.argb(255,200,200,0);
        } else {
            color = Color.argb(255,200,0,0);
        }

        return color;
    }

    public void CentraMappa() {
        if (builder!=null && VariabiliGlobali.getInstance().getmMap() !=null && Punti > 0) {
            LatLngBounds bounds = builder.build();
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 15);

            VariabiliGlobali.getInstance().getmMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    VariabiliGlobali.getInstance().getmMap().animateCamera(cu);
                }
            });
        }
    }
}
