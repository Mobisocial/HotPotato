package mobisocial.hotpotato;

import java.io.IOException;
import java.io.InputStream;

import mobisocial.nfc.Nfc;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class NfcShareActivity extends Activity {
	private static final String TAG = "nfcshare";
    Nfc mNfc;
    TextView mSharing;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mNfc = new Nfc(this);
        
        setContentView(R.layout.nfc_share);
        mSharing = (TextView)findViewById(R.id.sharing);
    }
    
    private void doSharing() {
    	if (getIntent() == null) {
    		Log.w("nfcshare", "no data to share.");
    		return;
    	}
    	
    	// TODO: binary transfers over NFC?
    	// if we have a content:// uri, proxy over BT?
    	
    	// try data
    	Uri data = getIntent().getData();
    	if (data != null) {
    		if (data.getScheme().equals("ndefb")) {
    			mSharing.setText("Tap to write ndef to tag.");
    			NdefMessage ndef;
				try {
					Log.d("nfc", "have  " + data);
					ndef = new NdefMessage(Base64.decode(data.toString().substring(8), android.util.Base64.URL_SAFE));
					mNfc.setOnTagWriteListener(new Nfc.OnTagWriteListener() {
						@Override
						public void onTagWrite(int status) {
							NfcShareActivity.this.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(NfcShareActivity.this, "Wrote ndef to tag.", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
					mNfc.enableTagWriteMode(ndef);
				} catch (FormatException e) {
					e.printStackTrace();
				}
				
    		} else if (data.getScheme().equals("file")) {
    			// local mime
    			ContentResolver resolver = getContentResolver();
    			String type = getIntent().getType();
    			mSharing.setText("Tap to share " + type);
    			InputStream in;
    			byte[] buffer = new byte[1024];
    			try {
	    			in = resolver.openInputStream(data);
	    			int size = 0;
	    			while (true) {
	    				int r = in.read(buffer);
	    				if (r <= 0) break;
	    				size += r;
	    			}
	    			in = resolver.openInputStream(data);
	    			byte[] bytes = new byte[size];
	    			in.read(bytes);
	    			mNfc.share(type, bytes);
    			} catch (IOException e) {
    				Log.e(TAG, "Error reading file", e);
    			}
    		} else {
	    		mSharing.setText("Tap to share " + getIntent().getType());
	    		mNfc.share(data);
    		}
    	} else {
	    	
	    	// try text
	    	String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
	    	if (text != null) {
	    		mSharing.setText("Tap to share " + getIntent().getType());
		    	mNfc.share(Uri.parse(text));
	    	}
    	}
    	getIntent().setData(null);
    	getIntent().putExtra(Intent.EXTRA_TEXT, (String)null);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mNfc.onResume(this);
    	
    	doSharing();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mNfc.onPause(this);
    	finish();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	if (mNfc.onNewIntent(this, intent)) return;
    }
    
    protected void toast(String text) {
    	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}