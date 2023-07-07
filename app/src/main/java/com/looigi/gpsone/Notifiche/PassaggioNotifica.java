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
			}

			if (Chiude) {
				finish();
			}
		} else {
			finish();
		}
    }
}
