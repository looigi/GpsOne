package com.looigi.gpsone.Notifiche;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.Utility;
import com.looigi.gpsone.VariabiliGlobali;

public class PassaggioNotifica extends Activity {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		String action=null;

		// Log.getInstance().ScriveLog("Notifica: onCreate PassaggioNotifica");

		try {
			if (getIntent().getExtras() != null) {
				action = (String) getIntent().getExtras().get("DO");
				// Log.getInstance().ScriveLog("Notifica: Action: " + action);
			}
		} catch (Exception e) {
			// Log.getInstance().ScriveLog(Utility.getInstance().PrendeErroreDaException(e));
		}

		if (action!=null) {
			Log.getInstance().ScriveLog("Passaggio notifica: " + action);

			boolean Chiude=true;

			switch (action) {
				case "apre":
					VariabiliGlobali.getInstance().setMascheraAperta(true);
					// Utility.getInstance().CambiaMaschera(R.id.home);
					break;
				case "playStop":
					boolean attivo = VariabiliGlobali.getInstance().isServizioGPS();
					attivo = !attivo;
					VariabiliGlobali.getInstance().setServizioGPS(attivo);
					Utility.getInstance().ScriveDatiAVideo();

					// Notifica.getInstance().setContext(VariabiliGlobali.getInstance().getContext());

					moveTaskToBack(true);
					break;
				case "cambioSezione":
					VariabiliGlobali.getInstance().setSezione(VariabiliGlobali.getInstance().getSezione() + 1);
					VariabiliGlobali.getInstance().setSezioniGiorno(VariabiliGlobali.getInstance().getSezioniGiorno() + 1);

					VariabiliGlobali.getInstance().setSezioniGiornoVisualizzato(VariabiliGlobali.getInstance().getSezioniGiornoVisualizzato() + 1);
					VariabiliGlobali.getInstance().setSezioneDaVisualizzare(-1);

					Utility.getInstance().ScriveDatiAVideo();
					Utility.getInstance().ScriveSezioni();

					moveTaskToBack(true);
					break;
			}

			if (Chiude) {
				finish();
			}
		}
    }
}
