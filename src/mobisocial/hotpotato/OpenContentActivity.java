package mobisocial.hotpotato;

import mobisocial.nfc.Nfc;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OpenContentActivity extends Activity {
	Nfc mNfc;
	TextView mStatus;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNfc = new Nfc(this);
		mNfc.addNdefHandler(mNdefHandler);
		
		setContentView(R.layout.open_content);
		mStatus = ((TextView)findViewById(R.id.status));
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
	private Nfc.NdefHandler mNdefHandler= new Nfc.NdefHandler() {
		
		@Override
		public int handleNdef(final NdefMessage[] ndefMessages) {
			// TODO: determine type and open appropriately.
			if (ndefMessages[0].getRecords()[0].getTnf() != NdefRecord.TNF_ABSOLUTE_URI) {
				toast("Not a URL.");
				return NDEF_PROPAGATE;
			}
			
			String recordPayload = new String(ndefMessages[0].getRecords()[0].getPayload());
			toast("Opening " + recordPayload);
			Uri uri = Uri.parse(recordPayload);
			Intent launch = new Intent(Intent.ACTION_VIEW);
			launch.setData(uri);
			startActivity(launch);
			return NDEF_PROPAGATE;
		}
	};
	
	public final void toast(final String text) {
		final Activity context = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}