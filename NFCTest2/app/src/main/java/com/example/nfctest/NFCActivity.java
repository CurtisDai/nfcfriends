package com.example.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.charset.Charset;

public class NFCActivity extends Activity implements CreateNdefMessageCallback {
    private EditText mEditText;
    private NfcAdapter mNfcAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mEditText = (EditText) findViewById(R.id.edit_text_field);

//        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
//        if(mAdapter==null){
//            mEditText.setText("Sorry, this device does not have NFC.");
//            return;
//        }
//
//        if(!mAdapter.isEnabled()){
//            Toast.makeText(this,"Please enable NFC via Settings.",Toast.LENGTH_LONG).show();
//        }
//        mAdapter.setNdefPushMessageCallback(this,this);
        checkNFCFunction();
    }

    private void checkNFCFunction(){
        //getting the default NFC adapter.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter==null){
            Toast.makeText(getApplicationContext(),"Sorry,this device does not have NFC.",Toast.LENGTH_LONG).show();
            return;
        } else {
            if(!mNfcAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"NFC is not Enabled!",Toast.LENGTH_LONG).show();

                Intent setnfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(setnfc);
                return;
            } else if(!mNfcAdapter.isNdefPushEnabled()){
                Toast.makeText(getApplicationContext(),"NFC Beam is not Enabled!",Toast.LENGTH_LONG).show();

                Intent setnfc = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(setnfc);
                return;
            }else {
                mNfcAdapter.setNdefPushMessageCallback(this,this);
                Toast.makeText(getApplicationContext(),"NFC is ready to use!",Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        String message = mEditText.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }
}
