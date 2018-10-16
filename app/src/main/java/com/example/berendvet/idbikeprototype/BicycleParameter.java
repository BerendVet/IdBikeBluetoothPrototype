package com.example.berendvet.idbikeprototype;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BicycleParameter {

	public static MainActivity ma;

	int value;
	String name;
	String unit;
	double scaleFactor;
	View view;

	public BicycleParameter(String name, String unit, double sf) {
		this.name = name;
		this.unit = unit;
		this.scaleFactor = sf;
		this.value = -1;    // Represents no value
	}

	public String nameValueUnitToString() {
		String s = name;
		if (value==-1) return s;		// don't transmit a value it has not been received
		if (scaleFactor==1) s +=  " = " + String.valueOf(value);
        else if (scaleFactor==0.1) s +=  " = " + String.format("%.1f", value*scaleFactor);
        else if (scaleFactor==0.01) s +=  " = " + String.format("%.2f", value*scaleFactor);
		if (unit.length()==0) return s;
		return s + " " + unit;
	}

	public void writeValueToView() {
		if (view!=null) ma.runOnUiThread(new WriteToViewThread(nameValueUnitToString()));
	}

	public void writeStringToView() {
		if (view!=null) ma.runOnUiThread(new WriteToViewThread(name + ": " + unit));
	}

	public class WriteToViewThread implements Runnable {
		String text;
		public WriteToViewThread(String s) { text = s; }
		public void run() {
			if (view instanceof EditText) ((EditText) view).setText(text);
			else if (view instanceof TextView) ((TextView) view).setText(text);
		}
	}

}
