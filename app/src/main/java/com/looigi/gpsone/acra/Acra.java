package com.looigi.gpsone.acra;

import android.app.Application;

import com.looigi.gpsone.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        customReportContent = {
            ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.ANDROID_VERSION,
            ReportField.PHONE_MODEL,
            ReportField.CUSTOM_DATA,
            ReportField.STACK_TRACE,
            ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        sendReportsAtShutdown = false,
        resToastText = R.string.toast_crash_report,
        reportSenderFactoryClasses = {CustomAcraSender.class}
)

public class Acra extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);

        /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code... */

        super.onCreate();
    }
}
