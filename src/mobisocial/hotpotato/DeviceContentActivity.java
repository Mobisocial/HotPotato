package mobisocial.hotpotato;

import mobisocial.nfc.Nfc;
import static mobisocial.nfc.Nfc.*;
import mobisocial.nfc.Nfc.NdefHandler;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceContentActivity extends Activity {
	Nfc mNfc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mNfc = new Nfc(this);
        mNfc.disableConnectionHandover();
        mNfc.addNdefHandler(mRemoteHandler);
        setContentView(R.layout.nfc_remote);
        ((TextView)findViewById(R.id.status)).setText("Touch me to an Accio target!");
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mNfc.onResume(this);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mNfc.onPause(this);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	if (mNfc.onNewIntent(this, intent)) return;
    }
    
    private void handoverSet() {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	((TextView)findViewById(R.id.status)).setText("");
		    	findViewById(R.id.main_layout).setBackgroundResource(android.R.color.background_light);				
			}
		});
    }
    
    private NdefMessage mHandoverNdef = null;
    private boolean handlingMessage = false;

    NdefHandler mRemoteHandler = new NdefHandler() {
		@Override
		public int handleNdef(NdefMessage[] ndefMessages) {
			if (handlingMessage) return NDEF_PROPAGATE;
			handlingMessage = true;

			NdefMessage ndef = ndefMessages[0];
			if (mHandoverNdef == null && isHandoverRequest(ndef)) {
				handoverSet();
				mHandoverNdef = ndef;
				mNfc.enableConnectionHandover();
				
				handlingMessage = false;
				return NDEF_CONSUME;
			}

			mNfc.getConnectionHandoverManager().doHandover(mHandoverNdef, ndef);
			toast("Sending message.");

			new Thread() {
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {}
					handlingMessage = false;
				};
			}.start();
			return NDEF_CONSUME;
			
			
		}
	};
	
	public void toast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(DeviceContentActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
}