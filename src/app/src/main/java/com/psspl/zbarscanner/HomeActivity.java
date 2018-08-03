package com.psspl.zbarscanner;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Below is define Ui component, interface, binding class
     */
    public static final String ZBAR_BARCODE_NUMBER = "ZbarBarcode";
    public static final String ZBAR_BARCODE_TYPE = "ZbarbarcodeType";
    public static final String SCANDIT_BARCODE_TYPE = "ScanditType";
    private AppCompatSpinner spinnerScannerType;
    String[] scannerType = {"Please select Scanner Type", "ZBar", "ZXing", "Scandit"};
    TextView tvFormat = null;
    TextView tvContents = null;
    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZXING_SCANNER_REQUEST = 2;
    public static final int SCANDIT_SCANNER_REQUEST = 1;
    public static final String SCANDIT_BARCODE_NUMBER = "barcode";
    private static final int ZBAR_CAMERA_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initview();
        setListeners();
    }

    /**
     * This function will set Trigger any particular event
     */
    private void setListeners() {
        spinnerScannerType.setOnItemSelectedListener(this);
    }

    /**
     * This function will initialize all the view and components
     */
    private void initview() {
        spinnerScannerType = (AppCompatSpinner) findViewById(R.id.spinnerScannerType);
        tvFormat = (TextView) findViewById(R.id.format);
        tvContents = (TextView) findViewById(R.id.contents);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, scannerType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScannerType.setAdapter(arrayAdapter);
    }

    /*
       this function will scan barcode which is selected by user
    * */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                Toast.makeText(this, R.string.label_scanner_type_selection, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                scanUsingZBar();
                break;
            case 2:
                scanUsingZXing();
                break;
            case 3:
                scanUsingScandit();
                break;

            default:
                break;
        }
    }


    /**
     * This function will call when user will select type of Scandit scanner
     */
    private void scanUsingScandit() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ScanditScannerActivity.class);
            startActivityForResult(intent, SCANDIT_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This function will call when user will select type of zarbar scanner
     */
    private void scanUsingZBar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        }
    }

    /**
     * This function will check Camera is available or not
     * @return boolean
     */
    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This function is initialize zxing scanner
     */
    public void scanUsingZXing() {
        Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
        intentScan.addCategory("android.intent.category.DEFAULT");
        try {
            startActivityForResult(intentScan, ZXING_SCANNER_REQUEST);
        } catch (ActivityNotFoundException var7) {
            var7.printStackTrace();
            showDownloadDialog();

        }
    }

    /*
    *  This method trigger after scan any barcode
    * */
    public void onActivityResult(int request, int result, Intent i) {
        switch (request) {
            case ZBAR_SCANNER_REQUEST:
                setDataForZBar(request, result, i);
                break;
            case ZXING_SCANNER_REQUEST:
                setDataForZxing(request, result, i);
                break;
            case SCANDIT_SCANNER_REQUEST:
                setDataForScandit(request, result, i);
                break;
            default:
                break;
        }

    }

    private AlertDialog showDownloadDialog() {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(HomeActivity.this);
        downloadDialog.setTitle("Install Barcode Scanner?");
        downloadDialog.setMessage("This application requires Barcode Scanner. Would you like to install it?");
        downloadDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String packageName;

                Uri uri = Uri.parse("market://details?id=" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    // Hmm, market is not installed
                    Log.w("test", "Google Play is not installed; cannot install ");

                }
            }
        });
        downloadDialog.setNegativeButton("NO", null);
        downloadDialog.setCancelable(true);
        return downloadDialog.show();
    }

    private void setDataForScandit(int request, int result, Intent i) {
        if (i != null) {
            if (i.hasExtra(SCANDIT_BARCODE_TYPE)) {
                tvFormat.setText(i.getStringExtra(SCANDIT_BARCODE_TYPE));
                tvContents.setText(i.getStringExtra(SCANDIT_BARCODE_NUMBER));
            }
        } else {
            tvFormat.setText("");
            tvContents.setText("");
        }
    }

    private void setDataForZBar(int request, int result, Intent i) {
        if (i != null) {
            if (i.hasExtra(ZBAR_BARCODE_NUMBER)) {
                tvContents.setText(i.getStringExtra(ZBAR_BARCODE_NUMBER));
                tvFormat.setText(i.getStringExtra(ZBAR_BARCODE_TYPE));
            }
        } else {
            tvFormat.setText("");
            tvContents.setText("");

        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putString("format", tvFormat.getText().toString());
        state.putString("contents", tvContents.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        tvFormat.setText(state.getString("format"));
        tvContents.setText(state.getString("contents"));
    }

    private void setDataForZxing(int request, int result, Intent i) {
        IntentResult scan = parseActivityResult(request, result, i);

        if (scan != null) {
            tvFormat.setText(scan.getFormatName());
            tvContents.setText(scan.getContents());
        }

    }

    public IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ZXING_SCANNER_REQUEST) {
            if (resultCode == -1) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                return new IntentResult(contents, formatName);
            } else {
                return new IntentResult((String) null, (String) null);
            }
        } else {
            return null;
        }
    }

    public final class IntentResult {
        private final String contents;
        private final String formatName;

        IntentResult(String contents, String formatName) {
            this.contents = contents;
            this.formatName = formatName;
        }

        public String getContents() {
            return this.contents;
        }

        public String getFormatName() {
            return this.formatName;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, SimpleScannerActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}