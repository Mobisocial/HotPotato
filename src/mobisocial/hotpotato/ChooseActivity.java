package mobisocial.hotpotato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// For active devices:
public class ChooseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.choose_activity);
		findViewById(R.id.content_device).setOnClickListener(mContentDeviceListener);
		findViewById(R.id.device_content).setOnClickListener(mDeviceContentListener);
		findViewById(R.id.open_content).setOnClickListener(mOpenContentListener);
	}
	
	View.OnClickListener mContentDeviceListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startActivity(new Intent(ChooseActivity.this, ContentDeviceActivity.class));
		}
	};
	
	View.OnClickListener mDeviceContentListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startActivity(new Intent(ChooseActivity.this, DeviceContentActivity.class));
		}
	};
	
	View.OnClickListener mOpenContentListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			startActivity(new Intent(ChooseActivity.this, OpenContentActivity.class));
		}
	};
}
