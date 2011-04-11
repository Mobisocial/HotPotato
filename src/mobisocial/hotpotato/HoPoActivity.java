package mobisocial.hotpotato;

import mobisocial.nfc.Nfc;
import mobisocial.nfc.Nfc.NdefFactory;
import mobisocial.nfc.Nfc.OnTagWriteListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.widget.Toast;

public class HoPoActivity extends Activity {
	private Nfc mNfc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNfc = new Nfc(this);

		final Uri data = getIntent().getData();
		NdefMessage wrappedNdef;
		try {
			wrappedNdef = new NdefMessage(android.util.Base64.decode(
	        		data.getPath().substring(1), android.util.Base64.URL_SAFE));
		} catch (FormatException e) {
			toast("Error parsing message.");
			finish();
			return;
		}

		final NdefMessage handoverRequest = wrappedNdef;
		String[] choices = new String[] { "Get Message",
										  "Mimic Request",
										  "Clone to Tag" };

		DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// TODO
					toast("Not implemented yet.");
					break;
				case 1:
					mNfc.share(handoverRequest);
					toast("Touch to device to share.");
					break;
				case 2:
					toast("Touch to tag to write.");
					mNfc.disableConnectionHandover();
					mNfc.setOnTagWriteListener(mOnWrite);
					mNfc.enableTagWriteMode(NdefFactory.fromUri(data));
					break;
				}
			}
		};

		new AlertDialog.Builder(this)
			.setItems(choices, onClick)
			.setTitle("Handover Request Found")
			.create().show();
	}

	OnTagWriteListener mOnWrite = new OnTagWriteListener() {
		@Override
		public void onTagWrite(int status) {
			if (status == WRITE_OK) {
				toast("Wrote to tag.");
			} else {
				toast("Error writing tag.");
			}
		}
	};

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
	
	public final void toast(final String text) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(HoPoActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}