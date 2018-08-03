package com.psspl.zbarscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends BaseScannerActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        /**
         * Initialize Scanner View
         */
        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.ZBAR_BARCODE_NUMBER, rawResult.getContents());
        intent.putExtra(HomeActivity.ZBAR_BARCODE_TYPE, rawResult.getBarcodeFormat().getName());
        setResult(HomeActivity.SCANDIT_SCANNER_REQUEST, intent);
        finish();//finishing activity


    }
}
