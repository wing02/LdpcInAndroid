package com.github.wing02.ldpcinandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String LOG_TAG = "MyOpenCLTag";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Create a TextView and set its content. the text is retrieved by
		 * calling a native function.
		 */

		final Button decode = (Button) findViewById(R.id.decode);
		final EditText z = (EditText) findViewById(R.id.z);
		final EditText srcLength = (EditText) findViewById(R.id.srcLength);
		final EditText batchSize = (EditText) findViewById(R.id.batchSize);
		final EditText snr = (EditText) findViewById(R.id.snr);
		final EditText deType = (EditText) findViewById(R.id.deType);
		final EditText times = (EditText) findViewById(R.id.times);
		final EditText rate = (EditText) findViewById(R.id.rate);
		final TextView result = (TextView) findViewById(R.id.result);
		final TextView log = (TextView) findViewById(R.id.log);
		log.setText(getLog());
		if (decode != null) {
			decode.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int intZ = Integer.parseInt(z.getText().toString());
					int intSrcLength = Integer.parseInt(srcLength.getText().toString());
					int intBatchSize = Integer.parseInt(batchSize.getText().toString());
					float floatErrVar = Float.parseFloat(snr.getText().toString());
					int intDeType = Integer.parseInt(deType.getText().toString());
					int intTimes = Integer.parseInt(times.getText().toString());
					int intRate = Integer.parseInt(rate.getText().toString());
					
					String res=stringFromJNI(getOpenCLProgram("decodeCL.c"), intZ, intSrcLength, intBatchSize,floatErrVar,intDeType,intTimes,intRate);
					//Log.i(LOG_TAG, res);

					result.setText(res);
					//result.setText(stringFromJNI(getOpenCLProgram("decodeCL.c"), intZ, intSrcLength, intBatchSize,floatErrVar,intDeType)+"");
				}
			});
		} else {
			Log.i("MyOpenCLTag", "decode==null");
		}

	}

	/*
	 * A native method that is implemented by the 'hello-jni' native library,
	 * which is packaged with this application.
	 */
	public native String stringFromJNI(String openCLProgramText, int z, int srcLength, int batchSize,float snr,int deType,int times,int rate);

	public native String getLog();
	/*
	 * This is another native method declaration that is *not* implemented by
	 * 'hello-jni'. This is simply to show that you can declare as many native
	 * methods in your Java code as you want, their implementation is searched
	 * in the currently loaded native libraries only the first time you call
	 * them.
	 *
	 * Trying to call this function will result in a
	 * java.lang.UnsatisfiedLinkError exception !
	 */
	public native String unimplementedStringFromJNI();

	/*
	 * this is used to load the 'hello-jni' library on application startup. The
	 * library has already been unpacked into
	 * /data/data/com.example.hellojni/lib/libhello-jni.so at installation time
	 * by the package manager.
	 */

	private String getOpenCLProgram(String fileName) {
		/*
		 * OpenCL program text is stored in a separate file in assets directory.
		 * Here you need to load it as a single string.
		 *
		 * In fact, the program may be directly built into native source code
		 * where OpenCL API is used, it is useful for short kernels (few lines)
		 * because it doesn't involve loading code and you don't need to pass it
		 * from Java to native side.
		 */

		try {
			StringBuilder buffer = new StringBuilder();
			InputStream stream = getAssets().open(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String s;

			while ((s = reader.readLine()) != null) {
				buffer.append(s);
				buffer.append("\n");
			}

			reader.close();
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	static {
		System.loadLibrary("LdpcInAndroid");
	}

}
