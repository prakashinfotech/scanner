package com.psspl.zbarscanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.SymbologySettings;

import java.util.Locale;

public class ScanditScannerActivity extends AppCompatActivity implements OnScanListener {

    Context context;
    private final int CAMERA_PERMISSION_REQUEST = 0;
    private BarcodePicker mBarcodePicker;
    private boolean mDeniedCameraAccess = false;
    private boolean mPaused = true;
    private Toast mToast = null;
    public static String SCANDIT_SDK_APP_KEY = "S7REaN9YEeKQLkrJdLr/zWYoZILMH8GS7XA4oOFoVLI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ScanditScannerActivity.this;
        ScanditLicense.setAppKey(SCANDIT_SDK_APP_KEY);
        initializeAndStartBarcodeScanning();
    }

    private void initializeAndStartBarcodeScanning() {
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[]{
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_EAN8,
                Barcode.SYMBOLOGY_UPCA,
                Barcode.SYMBOLOGY_UPCE
        };
        for (int sym : symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true);
        }
        SymbologySettings symSettings = settings.getSymbologySettings(Barcode.SYMBOLOGY_CODE39);
        short[] activeSymbolCounts = new short[]{
                7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        };
        symSettings.setActiveSymbolCounts(activeSymbolCounts);
        // For details on defaults and how to calculate the symbol counts for each symbology, take
        // a look at http://docs.scandit.com/stable/c_api/symbologies.html.
        // Prefer the back-facing camera, is there is any.
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);
        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        boolean emulatePortraitMode = !BarcodePicker.canRunPortraitPicker();
        if (emulatePortraitMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        BarcodePicker picker = new BarcodePicker(this, settings);
        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker = picker;
        mBarcodePicker.setOnScanListener(this);
        mBarcodePicker.getOverlayView().setTorchEnabled(false);
        setContentView(picker);
    }

    /**
     * This is life cycle method which call when activity will sleep mode
     */
    @Override
    protected void onPause() {
        super.onPause();
        mBarcodePicker.stopScanning();
        mPaused = true;
    }

    private void grantCameraPermissionsThenStartScanning() {

        mBarcodePicker.startScanning();
    }

    /**
     * This is life cycle method which call when device wakeup
     */
    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        // Handle permissions for Marshmallow and onwards...
        mBarcodePicker.startScanning();
    }

    /**
     *  This functiona will scan barcode
     * @param scanSession
     */
    @Override
    public void didScan(ScanSession scanSession) {
        String barcodeNumber = "";
        String SymbologyCode = "";
        for (Barcode code : scanSession.getNewlyRecognizedCodes()) {
            String data = code.getData();
            String cleanData = data;
            if (data.length() > 30) {
                cleanData = data.substring(0, 25) + "[...]";
            }
            if (barcodeNumber.length() > 0) {
                barcodeNumber += "\n\n\n";
            }
            barcodeNumber += cleanData;
            Log.e("Msg", barcodeNumber);
            SymbologyCode = code.getSymbologyName().toUpperCase(Locale.US);
            Log.e("SymbologyCode", SymbologyCode);


            Intent intent = new Intent();
            intent.putExtra(HomeActivity.SCANDIT_BARCODE_NUMBER, barcodeNumber);
            intent.putExtra(HomeActivity.SCANDIT_BARCODE_TYPE, SymbologyCode);
            setResult(HomeActivity.SCANDIT_SCANNER_REQUEST, intent);

            finish();//finishing activity
        }
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }
}
