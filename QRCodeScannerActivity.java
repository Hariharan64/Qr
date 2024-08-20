package com.example.qrstaff;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class QRCodeScannerActivity extends Activity {


        private CompoundBarcodeView barcodeView;

        private static final String EXPECTED_CODE = "https://quadcrag.com";

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qrcode_scanner);

            barcodeView = findViewById(R.id.barcode_scanner);
            barcodeView.decodeContinuous(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    if (result != null) {
                        String scannedCode = result.getText();
                        if (scannedCode.equals(EXPECTED_CODE)) {
                            handleValidScan();
                        } else {
                            handleInvalidScan();
                        }
                    }
                }
            });
        }

        private void handleValidScan() {
            boolean isPunchIn = getIntent().getBooleanExtra("isPunchIn", true);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isPunchIn", isPunchIn);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        private void handleInvalidScan() {
            // Show an error message
            Toast.makeText(this, "Invalid QR code. Please try again.", Toast.LENGTH_SHORT).show();
            // Optionally, you can restart the scanning process if you want
            barcodeView.resume();
        }

        @Override
        protected void onResume() {
            super.onResume();
            barcodeView.resume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            barcodeView.pause();
        }
    }
