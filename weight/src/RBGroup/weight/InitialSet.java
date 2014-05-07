package RBGroup.weight;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class InitialSet extends Activity implements OnClickListener, OnItemSelectedListener{

	private Spinner spinner;
	private RadioGroup rdoGroup;
	private Button btnNext;
	private boolean isMan = false;
	private int height = 150;
	private Common common;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.initialset);
	    
	    common = new Common(this);
	    
	    spinner = (Spinner)findViewById(R.id.spinner);
	    rdoGroup = (RadioGroup)findViewById(R.id.rdoGroup);
	    btnNext = (Button)findViewById(R.id.btnNext);
	    
	    btnNext.setOnClickListener(this);
	    spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		RadioButton checkButton = (RadioButton)findViewById(rdoGroup.getCheckedRadioButtonId()); 
		
		switch(checkButton.getId()){
			case R.id.rdoMan:
				isMan = true;
				break;
			case R.id.rdoWoman:
				isMan = false;
				break;
		}

		common.saveWeight(height);
		common.saveSexInfoIsMan(isMan);
		
		startActivity(new Intent(InitialSet.this, Example.class));
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		height = Integer.parseInt(parent.getItemAtPosition(position).toString());
		//Toast.makeText(InitialSet.this, " : "+parent.getItemAtPosition(position).toString(), 3000).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.e("InitialSet", "IS NOTHING SELECTED");
	}

}
