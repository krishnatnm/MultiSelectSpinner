package com.example.multiselectspinner;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "CHECKED OPTIONS";
	protected EditText selectOptions;
	protected CharSequence[] options = { "Option 1", "Option 2", "Option 3",
			"Option 4", "Option 5", "Option 6" };
	protected ArrayList<CharSequence> selectedOptions = new ArrayList<CharSequence>();
	
	SharedPreferences sharedPreferences;
	Editor editor;
	
	JSONArray jArray = new JSONArray();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPreferences = getApplicationContext().getSharedPreferences("Reg",
				0);
		editor = sharedPreferences.edit();

		selectOptions = (EditText) findViewById(R.id.selectOptions);
		selectOptions.setOnClickListener(this);

		
		String jString = sharedPreferences.getString("selectedOpt", null);
		if(jString != null && !jString.equalsIgnoreCase("[]")){
			try {
				jArray = new JSONArray(jString);
				Log.d(TAG, "" + jString);
				for(int i=0; i< jArray.length(); i++){
					int npos = jArray.getInt(i);
					selectedOptions.add(options[npos]);
				}
				onChangeSelectedOptions();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		else
			Toast.makeText(this, "Kuchh Nahi", Toast.LENGTH_LONG).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.selectOptions:
			showSelectOptionsDialog();
			break;

		default:
			break;
		}

	}

	@SuppressLint("NewApi") protected void onChangeSelectedOptions() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < selectedOptions.size(); i++){
			stringBuilder.append(options[i]);
			if(i != selectedOptions.size()-1){
				stringBuilder.append(", ");
			}
		}
		if(!stringBuilder.toString().isEmpty()){
		selectOptions.setText(stringBuilder.toString());
		}else{
			selectOptions.setText("Select Options");
		}
	}

	protected void showSelectOptionsDialog() {
		boolean[] checkedOptions = new boolean[options.length];
		int count = options.length;

		for (int i = 0; i < count; i++)
			checkedOptions[i] = selectedOptions.contains(options[i]);

		DialogInterface.OnMultiChoiceClickListener optionsDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
			@SuppressLint("NewApi") @Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				if (isChecked) {
					selectedOptions.add(options[which]);
					jArray.put(which);
				} else{
					selectedOptions.remove(options[which]);
					for(int i=0; i< jArray.length(); i++){
						try {
							if(jArray.getInt(i) == which){
								jArray.remove(i);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				editor.putString("selectedOpt", jArray.toString());
				editor.commit();
				onChangeSelectedOptions();
				
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Options");
		builder.setMultiChoiceItems(options, checkedOptions,
				optionsDialogListener);
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
}
