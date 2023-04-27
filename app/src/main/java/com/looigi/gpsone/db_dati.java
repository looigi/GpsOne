package com.looigi.gpsone;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class db_dati {
    private final String PathDB;
    private final SQLiteDatabase myDB;
    private final String DATABASE_TABELLA_GPS = "Posizioni";
    private final String DATABASE_TABELLA_MULTIMEDIA = "Multimedia";
    private final String DATABASE_TABELLA_DISTANZE = "Distanze";

    public db_dati() {
        PathDB = VariabiliGlobali.getInstance().getPercorsoDIR() + "/Db/";

        Utility.getInstance().CreaCartelle(PathDB);
        myDB = ApreDB();
    }

    private SQLiteDatabase ApreDB() {
        SQLiteDatabase db = null;
        try {
            String nomeDB = "posizioni.db";
            db = MainActivity.getAppContext().openOrCreateDatabase(
                    PathDB + nomeDB, MODE_PRIVATE, null);
        } catch (Exception e) {
            Log.getInstance().ScriveLog("ERRORE Nell'apertura del db: " + Utility.getInstance().PrendeErroreDaException(e));
        }

        return  db;
    }

    public void CreazioneTabelle() {
        if (myDB != null) {
            try {
            // SQLiteDatabase myDB = ApreDB();
                String DATABASE_CREAZIONE_1 = "CREATE TABLE IF NOT EXISTS  " + DATABASE_TABELLA_GPS + " (" +
                        "data text not null, " +
                        "sezione integer not null, " +
                        "pro integer not null, " +
                        "lat text not null, " +
                        "lon text not null, " +
                        "alt text not null, " +
                        "dat text not null, " +
                        "vel text not null, " +
                        "acc text not null" +
                        ");";
                myDB.execSQL(DATABASE_CREAZIONE_1);
            } catch (Exception ignored) {
                int a = 0;
            }

            try {
                // SQLiteDatabase myDB = ApreDB();
                String DATABASE_CREAZIONE_1 = "CREATE TABLE IF NOT EXISTS  Impostazioni (" +
                        "accuracy text not null, " +
                        "accuracyvalue text not null, " +
                        "distanzagps text not null, " +
                        "gpsbetter text not null, " +
                        "tempogps text not null" +
                        ");";
                myDB.execSQL(DATABASE_CREAZIONE_1);
            } catch (Exception ignored) {
                int a = 0;
            }

           /* try {
                String DATABASE_CREAZIONE_2 = "CREATE TABLE IF NOT EXISTS  " + DATABASE_TABELLA_MULTIMEDIA + " (" +
                        "data text not null, " +
                        "integer text not null, " +
                        "lat text not null, " +
                        "lon text not null, " +
                        "alt text not null, " +
                        "dat text not null, " +
                        "nfile text not null, " +
                        "type text not null " +
                        ");";
                myDB.execSQL(DATABASE_CREAZIONE_2);
            } catch (Exception ignored) {
                int a = 0;
            }

            try {
                String DATABASE_CREAZIONE_3 = "CREATE TABLE IF NOT EXISTS  " + DATABASE_TABELLA_DISTANZE + " (" +
                        "data text not null, " +
                        "distanza text not null " +
                        ");";
                myDB.execSQL(DATABASE_CREAZIONE_3);
            } catch (Exception ignored) {
                int a = 0;
            } */

            try {
                myDB.execSQL("CREATE INDEX IF NOT EXISTS Posizioni_Index ON " + DATABASE_TABELLA_GPS + "(data, pro);");
            } catch (Exception ignored) {
                int a = 0;
            }

            /* try {
                myDB.execSQL("CREATE INDEX IF NOT EXISTS Multimedia_Index ON " + DATABASE_TABELLA_MULTIMEDIA + "(data, pro);");
            } catch (Exception ignored) {
                int a = 0;
            }

            try {
                myDB.execSQL("CREATE INDEX IF NOT EXISTS Distanze_Index ON " + DATABASE_TABELLA_DISTANZE + "(data);");
            } catch (Exception ignored) {
                int a = 0;
            } */
        }
    }

    public void CompattaDB() {
        // SQLiteDatabase myDB = ApreDB();
        if (myDB != null) {
            myDB.execSQL("VACUUM");
        }
    }

    public void PulisceDati() {
        if (myDB != null) {
            myDB.execSQL("Delete From " + DATABASE_TABELLA_GPS);
            myDB.execSQL("Delete From Impostazioni");
            // myDB.execSQL("Delete From " + DATABASE_TABELLA_DISTANZE );
            // myDB.execSQL("Delete From " + DATABASE_TABELLA_MULTIMEDIA);
        }
    }

    public long scriveImpostazioni()
    {
        try {
            if (myDB != null) {
                myDB.execSQL("INSERT INTO"
                        + " Impostazioni "
                        + " (accuracy, accuracyvalue, distanzagps, gpsbetter, tempogps)"
                        + " VALUES ("
                        + "'" + (VariabiliGlobali.getInstance().isAccuracy() ? "S" : "N") + "', "
                        + "'" + VariabiliGlobali.getInstance().getAccuracyValue() + "', "
                        + "'" + VariabiliGlobali.getInstance().getDISTANZA_GPS() + "', "
                        + "'" + (VariabiliGlobali.getInstance().isGPSBetter() ? "S" : "N") + "', "
                        + "'" + VariabiliGlobali.getInstance().getTEMPO_GPS() + "' "
                        + ")");

                return 1;
            } else {
                Log.getInstance().ScriveLog("Scrivi impostazioni. ERROR: Db chiuso");

                return -1;
            }
        } catch (Exception e) {
            Log.getInstance().ScriveLog("Scrivi impostazioni. ERROR: " + Utility.getInstance().PrendeErroreDaException(e));

            return -1;
        }
    }

    public boolean caricaImpostazioni() {
        try {
            if (myDB != null) {
                Cursor c1 = myDB.rawQuery("SELECT * FROM Impostazioni", null);
                c1.moveToFirst();
                if (c1.getCount() > 0) {
                    VariabiliGlobali.getInstance().setAccuracy(c1.getString(0).equals("S"));
                    VariabiliGlobali.getInstance().setAccuracyValue(Integer.parseInt(c1.getString(1)));
                    VariabiliGlobali.getInstance().setDISTANZA_GPS(Integer.parseInt(c1.getString(2)));
                    VariabiliGlobali.getInstance().setGPSBetter(c1.getString(3).equals("S"));
                    VariabiliGlobali.getInstance().setTEMPO_GPS(Integer.parseInt(c1.getString(4)));
                }
                c1.close();
            } else {
                Log.getInstance().ScriveLog("Carica impostazioni. DB non aperto");

                return false;
            }
        } catch(Exception e) {
            Log.getInstance().ScriveLog("Carica impostazioni. ERROR: " + Utility.getInstance().PrendeErroreDaException(e));

            return false;
        }

        return true;
    }

    public long aggiungiPosizione(
            String data,
            int Sezione,
            String pro,
            String lat,
            String lon,
            String alt,
            String vel,
            String acc)
    {
        try {
            NumberFormat f2 = new DecimalFormat("00");
            NumberFormat f3 = new DecimalFormat("000");
            Calendar c = Calendar.getInstance();
            String hour = f2.format(c.get(Calendar.HOUR_OF_DAY));
            String minutes = f2.format(c.get(Calendar.MINUTE));
            String seconds = f2.format(c.get(Calendar.SECOND));
            String mseconds = f3.format(c.get(Calendar.MILLISECOND));

            String dat = hour + minutes + seconds + mseconds;

            // Arrotondo alla cifra superiore la latitudine e la longitudine
            /* if (lat.length() > 6) {
                lat = lat.substring(0, lat.length() - 1) + "0";
            }
            if (lon.length() > 6) {
                lon = lon.substring(0, lon.length() - 1) + "0";
            } */
            // Arrotondo alla cifra superiore la latitudine e la longitudine

            if (myDB != null) {
                boolean ok = true;

                if (!VariabiliGlobali.getInstance().isDisegnatoPrimoPunto()) {
                    Cursor c1 = myDB.rawQuery("SELECT * FROM " + DATABASE_TABELLA_GPS + " WHERE data = ? and lat = ? and lon = ?",
                            new String[]{data, lat, lon});
                    c1.moveToFirst();
                    if (c1.getCount() > 0) {
                        ok = false;
                    }
                    c1.close();

                    VariabiliGlobali.getInstance().setDisegnatoPrimoPunto(true);
                }

                if (ok) {
                    long Progressivo = 0;

                    Cursor c1 = myDB.rawQuery("SELECT Max(pro) FROM " + DATABASE_TABELLA_GPS + " WHERE data = ?",
                            new String[]{data});
                    c1.moveToFirst();
                    if (c1.getCount() > 0) {
                        Progressivo = c1.getLong(0);
                    }
                    c1.close();

                    Progressivo++;
                    myDB.execSQL("INSERT INTO"
                            + " " + DATABASE_TABELLA_GPS + " "
                            + " (data, sezione, pro, lat, lon, alt, dat, vel, acc)"
                            + " VALUES ('" + data + "', " + Sezione + ", " + Progressivo + ", " + lat + ", " + lon + ", "
                            + alt + ", '" + dat + "', " + vel + ", " + acc + ");");
                }

                return 1;
            } else {
                Log.getInstance().ScriveLog("Aggiungi posizione. ERROR: Db chiuso");

                return -1;
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.getInstance().ScriveLog("Aggiungi posizione. ERROR: "+errors.toString());

            return -1;
        }
    }

    /* public long aggiungiMultimedia(
            String data,
            String pro,
            String lat,
            String lon,
            String alt,
            String nomeFile,
            String type)
    {
        try {
            NumberFormat f2 = new DecimalFormat("00");
            NumberFormat f3 = new DecimalFormat("000");
            Calendar c = Calendar.getInstance();
            String hour = f2.format(c.get(Calendar.HOUR_OF_DAY));
            String minutes = f2.format(c.get(Calendar.MINUTE));
            String seconds = f2.format(c.get(Calendar.SECOND));
            String mseconds = f3.format(c.get(Calendar.MILLISECOND));

            String dat = hour + minutes + seconds + mseconds;

            if (myDB != null) {
                long Progressivo = 0;

                Cursor c1 = myDB.rawQuery("SELECT Max(pro) FROM " + DATABASE_TABELLA_MULTIMEDIA + " WHERE data = ?",
                        new String[]{data});
                c1.moveToFirst();
                if (c1.getCount() > 0) {
                    Progressivo = c1.getLong(0);
                }
                c1.close();

                Progressivo++;
                myDB.execSQL("INSERT INTO"
                        + " " + DATABASE_TABELLA_MULTIMEDIA + " "
                        + " (data, pro, lat, lon, alt, dat, nfile, type)"
                        + " VALUES ('" + data + "', " + Progressivo + ", " + lat + ", "
                        + lon + ", " + alt + ", '" + dat + "', '" + nomeFile + "', '"+ type + "');");

                return 1;
            } else {
                Log.getInstance().ScriveLog("Aggiungi multimedia. ERROR: Db chiuso");

                return -1;
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.getInstance().ScriveLog("Aggiungi multimedia. ERROR: "+errors.toString());

            return -1;
        }
    }

    public long inserisceNuovaDistanza(
            String data,
            String distanza)
    {
        try {
            if (myDB != null) {
                myDB.execSQL("INSERT INTO"
                        + " " + DATABASE_TABELLA_DISTANZE + " "
                        + " (data, distanza)"
                        + " VALUES ('" + data + "', " + distanza);

                return 1;
            } else {
                Log.getInstance().ScriveLog("Aggiungi distanze. ERROR: Db chiuso");

                return -1;
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.getInstance().ScriveLog("Aggiungi distanze. ERROR: "+errors.toString());

            return -1;
        }
    }

    public long aggiornaDistanza(
            String data,
            String distanza)
    {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("data", data);
            initialValues.put("distanza", distanza);

            String whereClause = "data = ?";
            String[] whereArgs = new String[] {
                    data
            };

            long l = myDB.update(DATABASE_TABELLA_DISTANZE, initialValues, whereClause, whereArgs);

            return l;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.getInstance().ScriveLog("Aggiorna distanze. ERROR: "+errors.toString());

            return -1;
        }
    } */

    /* public boolean cancellaDatiGPSPerDataAttuale()
    {
        Date dataVisua = VariabiliGlobali.getInstance().getDataDiVisualizzazioneMappa();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataVisuaS = formatter.format(dataVisua);

        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                dataVisuaS
        };

        return myDB.delete(DATABASE_TABELLA_GPS, whereClause, whereArgs) > 0;
    }

    public boolean cancellaDatiGPSPerData(Date dataVisua)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataVisuaS = formatter.format(dataVisua);

        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                dataVisuaS
        };

        return myDB.delete(DATABASE_TABELLA_GPS, whereClause, whereArgs) > 0;
    }

    public List<Date> RitornaTutteLeDateInArchivio() {
        List<Date> lista = new ArrayList<>();

        String[] tableColumns = new String[] {
                "data",
                "pro",
                "lat",
                "lon",
                "alt",
                "dat",
                "vel"
        };

        Date ddd = new Date(System.currentTimeMillis());
        String oggi = android.text.format.DateFormat.format("dd/MM/yyyy", ddd).toString();
        Cursor c =null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String selection = "data < ? ";
        String[] selectionArgs = new String[] {oggi};

        try {
            c = myDB.query(DATABASE_TABELLA_GPS, tableColumns, selection, selectionArgs,
                    "data", null, "data");
            if (c.moveToFirst()) {
                do {
                    String d = c.getString(0);
                    Date dd = dateFormat.parse(d);

                    lista.add(dd);
                } while (c.moveToNext());
            }
            if (c != null)
                c.close();

        } catch (Exception ignored) {

        }

        return lista;
    }

    public boolean cancellaDatiMultiMedia()
    {
        return myDB.delete(DATABASE_TABELLA_MULTIMEDIA, "", null) > 0;
    }

    public boolean cancellaDatiMultiMediaPerDataAttuale()
    {
        Date dataVisua = VariabiliGlobali.getInstance().getDataDiVisualizzazioneMappa();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataVisuaS = formatter.format(dataVisua);

        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                dataVisuaS
        };


        return myDB.delete(DATABASE_TABELLA_MULTIMEDIA, whereClause, whereArgs) > 0;
    }

    public boolean cancellaDatiMultiMediaPerData(Date dataVisua)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataVisuaS = formatter.format(dataVisua);

        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                dataVisuaS
        };


        return myDB.delete(DATABASE_TABELLA_MULTIMEDIA, whereClause, whereArgs) > 0;
    }

    public Cursor ottieniValoriGPS()
    {
        return myDB.query(DATABASE_TABELLA_GPS, new String[] {
                "data",
                "pro",
                "lat",
                "lon",
                "alt",
                "dat",
                "vel"
        }, null,null, null, null, null);
    } */

    public Cursor ottieniValoriGPSPerData(String oggi, String Sezione)
    {
        String[] tableColumns = new String[] {
                "data",
                "sezione",
                "pro",
                "lat",
                "lon",
                "alt",
                "dat",
                "vel",
                "acc"
        };
        String whereClause = "data = ?";
        if (!Sezione.equals("")) {
            whereClause = " and Sezione = ?";
        }
        String[] whereArgs;
        if (Sezione.equals("")) {
            whereArgs = new String[]{
                    oggi
            };
        } else {
            whereArgs = new String[]{
                    oggi,
                    Sezione
            };
        }

        Cursor c =null;
        try {
            c = myDB.query(DATABASE_TABELLA_GPS, tableColumns, whereClause, whereArgs,
                    null, null, "data asc, sezione asc, pro asc");
        } catch (Exception ignored) {

        }
        return c;
    }

    public String contaValoriGPSPerData(String oggi)
    {
        String PuntiDisegnati = "";
        String Sezioni = "";

        Cursor c =null;
        try {
            c = myDB.rawQuery("SELECT Count(*) FROM " + DATABASE_TABELLA_GPS + " WHERE data = ?", new String[]{ oggi });
            if (c.getCount() > 0) {
                c.moveToFirst();
                int punti = c.getInt(0);
                PuntiDisegnati = Integer.toString(punti);
            }
        } catch (Exception ignored) {
            // VariabiliGlobali.getInstance().setPuntiDisegnati(0);
            PuntiDisegnati = "0";
        }

        try {
            c = myDB.rawQuery("SELECT Max(Sezione) FROM " + DATABASE_TABELLA_GPS + " WHERE data = ?", new String[]{ oggi });
            if (c.getCount() > 0) {
                c.moveToFirst();
                int sezioni = c.getInt(0);
                // VariabiliGlobali.getInstance().setSezioniGiorno(sezioni);
                // VariabiliGlobali.getInstance().setSezione(sezioni);
                Sezioni = Integer.toString(sezioni);
            } else {
                // VariabiliGlobali.getInstance().setSezioniGiorno(0);
                // VariabiliGlobali.getInstance().setSezione(0);
                Sezioni = "0";
            }
        } catch (Exception ignored) {
            // VariabiliGlobali.getInstance().setSezione(1);
            Sezioni = "0";
        }

        return PuntiDisegnati + ";" + Sezioni;
    }

    /* public Cursor ottieniDistanzeData(String oggi)
    {
        String[] tableColumns = new String[] {
                "data",
                "distanza"
        };
        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                oggi
        };

        Cursor c = null;
        try {
            c = myDB.query(DATABASE_TABELLA_DISTANZE, tableColumns, whereClause, whereArgs,
                    null, null, null);
        } catch (Exception ignored) {

        }

        return c;
    }

    public Cursor ottieniValoriMultiMediaPerData(String oggi)
    {
        String[] tableColumns = new String[] {
                "data",
                "pro",
                "lat",
                "lon",
                "alt",
                "dat",
                "nFile",
                "type"
        };
        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                oggi
        };

        Cursor c = null;
        try {
            c = myDB.query(DATABASE_TABELLA_MULTIMEDIA, tableColumns, whereClause, whereArgs,
                    null, null, null);
        } catch (Exception ignored) {

        }
        return c;
    }

    public int ottieniMassimoProgressivoGPSPerData(String oggi)
    {
        String[] tableColumns = new String[] {
                "data",
                "pro"
        };
        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                oggi
        };
        int pro=0;
        Cursor c = myDB.query(DATABASE_TABELLA_GPS, tableColumns, whereClause, whereArgs,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                int p = c.getInt(1);
                if (p>pro) {
                    pro=p;
                }
            } while (c.moveToNext());
        }
        if(c != null)
            c.close();

        return pro;
    }

    public int ottieniMassimoProgressivoMultimediaPerData(String oggi)
    {
        String[] tableColumns = new String[] {
                "data",
                "pro"
        };
        String whereClause = "data = ?";
        String[] whereArgs = new String[] {
                oggi
        };
        int pro=0;
        Cursor c = myDB.query(DATABASE_TABELLA_MULTIMEDIA, tableColumns, whereClause, whereArgs,
                null, null, null);
        if (c.moveToFirst()) {
            do {
                int p = c.getInt(1);
                if (p>pro) {
                    pro=p;
                }
            } while (c.moveToNext());
        }
        if(c != null)
            c.close();

        return pro;
    } */

}
