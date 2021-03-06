package mobisocial.hotpotato;

import mobisocial.nfc.Nfc;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContentDeviceActivity extends Activity {
	Nfc mNfc;
	TextView mStatus;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNfc = new Nfc(this);
		mNfc.addNdefHandler(mOnTagReadListener);
		
		setContentView(R.layout.hot_potato);
		mStatus = ((TextView)findViewById(R.id.status));
		findViewById(R.id.clear).setOnClickListener(mClearListener);
	}

	public void onResume() {
		super.onResume();
		mNfc.onResume(this);
	}

	public void onPause() {
		super.onPause();
		mNfc.onPause(this);
	}

	public void onNewIntent(Intent intent) {
		if (mNfc.onNewIntent(this, intent)) {
			return;
		}
	}
	
	/**
	 * Triggered when an NdefMessage is received via card or P2P scan.
	 */
	private Nfc.NdefHandler mOnTagReadListener = new Nfc.NdefHandler() {
		
		@Override
		public int handleNdef(final NdefMessage[] ndefMessages) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mStatus.setText(R.string.sharing);
					mNfc.share(ndefMessages[0]);					
				}
			});
			
			return NDEF_PROPAGATE;
		}
	};
	
	/**
	 * Attached to the "clear" button.
	 */
	private View.OnClickListener mClearListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new Thread() {
			 public void run() {
				 mNfc.clearSharing();
			 };
			}.start();

			mStatus.setText(R.string.not_sharing);
		}
	}; 
}