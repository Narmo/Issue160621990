package world.facesof.issue160621990;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ImageStack stack = findViewById(R.id.stack);
		stack.setImages();

		ImageStackAttributed attributedStack = findViewById(R.id.stack_attributed);
		attributedStack.setImages();

		String device = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

		TextView deviceDetailsLabel = findViewById(R.id.device_details_label);
		deviceDetailsLabel.setText(device);
	}
}
