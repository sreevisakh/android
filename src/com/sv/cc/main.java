package com.sv.cc;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class main extends Activity {
	 //private AdView adView;
	private String s= "";
	static final private int MENU_ITEM = Menu.FIRST;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         Intent i = new Intent(main.this,ProgressBar.class);
        startActivity(i);
        
        final Spinner s1 = (Spinner)findViewById(R.id.spinnerFrom);
        final Spinner s2 = (Spinner)findViewById(R.id.spinnerTo);
        final EditText e  = (EditText)findViewById(R.id.editTextAmount);
        final Button btn = (Button)findViewById(R.id.buttonConvert);
        final TextView tv  = (TextView)findViewById(R.id.textViewResult);
        fillSpinner();
      //  SimpleCursorAdapter a = new SimpleCursorAdapter(null, 0, null, null, null);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch(v.getId())
				{
					case R.id.buttonConvert:
						
						if(e.getText().length()<=0)
						{
							Toast.makeText(main.this, "Enter Amount", Toast.LENGTH_LONG).show();
							return;
						}
						double result = Convert(s1.getSelectedItem().toString(),s2.getSelectedItem().toString(),e.getText().toString());
						tv.setText("Result : "+ result);
				}
				
				
			}
		});
        	
        
        
        
    }

    private void fillSpinner(){
    	 
    	CurrencyDbAdapter db = new CurrencyDbAdapter(this);
		db.open();
		Cursor c = db.fetchAllRates();
		
		ArrayAdapter <CharSequence> adapter =
			  new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		while(c.moveToNext())
		{
			
			adapter.add(c.getString(1));
		}
			
    	db.close();
    	Spinner s1 = (Spinner)findViewById(R.id.spinnerFrom);
    	s1.setAdapter(adapter);
    	Spinner s2 = (Spinner)findViewById(R.id.spinnerTo);
    	s2.setAdapter(adapter);
    	}
    	 
    private double Convert(String s1,String s2,String e)
    {
    
    	double amount = Double.parseDouble(e);
    	double r1 =0;
    	double r2 =0;
    	CurrencyDbAdapter db = new CurrencyDbAdapter(this);
		db.open();
    	if(s1!="USD")
    	{
    		r1 = db.fetchRate(s1);
    		if(r1==0)
    		{
    			return 0;
    		}
    	
    	}
    	else
    	{
    		r1 =1;
    	}
    	if(s2!="USD")
    	{
    		r2 = db.fetchRate(s2);
    		if(r2==0)
    		{
    			return 0;
    			
    		}
    	
    	}
    	else
    	{
    		r2=1;
    	}
    	amount = amount / r1;
    	amount = amount * r2;
    	
    	db.close();
    	Log.i("CONVERT","S1="+s1+"S2="+s2+"R1="+r1+"R2="+r2+"Amount= "+amount);
    	amount = roundToDecimals(amount,2);
    	return amount;
    	
    }
    public static double roundToDecimals(double d, int c) {
    	int temp=(int)((d*Math.pow(10,c)));
    	return (((double)temp)/Math.pow(10,c));
    	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		int groupId=0;
		int menuItemId = MENU_ITEM;
		int menItemOrder = Menu.NONE;
		String menuItemText = "Update";
		
		MenuItem menuItem = menu.add(groupId, menuItemId, menItemOrder, menuItemText);
		menuItem.setIcon(android.R.drawable.ic_menu_rotate);
		
		return true;
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		

		switch(item.getItemId())
		{
			case MENU_ITEM:
			
			//Toast.makeText(main.this, "Updating..", Toast.LENGTH_SHORT).show();
			//Update_rates();
			//Delete_rates();
			//Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
				showDialog(0);
			return true;
			
			default:
				return false;
			 
		}
		
	}
	
	//Loading and Update of Data
    public boolean Update(String s) throws JSONException
    {
  	   	try {
			JSONObject json = new JSONObject(s);  	//New JSON Object
			json = json.getJSONObject("rates"); 	//Extracted Rate part from JSON file
			CurrencyDbAdapter db = new CurrencyDbAdapter(this);
			db.open();								//Open database connection
			
			Iterator<String> it = json.keys();		//json.keys() have all the Country IDs
			while (it.hasNext()) {					//Iterate through them
				String country = it.next();
				long rowId = db.containRate(country);	// check if country already Exists in table 
				double rate = json.optDouble(country);	
				if (rowId > 0) {
					//Log.i("SQL","UPDATE "+country);	//if so update rate
					db.updateRate(rowId, rate);
				} else {
					//Log.i("SQL","INSERT "+country);	//else create new entry
					db.createRate(country, rate);
				}
				//
			}
			db.close();
			return true;
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
			return false;
		}
		
    }
    public InputStream getStream(String url) {
        try {
            URL url1 = new URL(url);
            URLConnection urlConnection = url1.openConnection();
            urlConnection.setConnectTimeout(1000);
            return urlConnection.getInputStream();
        } catch (Exception ex) {
            //Log.e("JSON",ex.toString());
             Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
            return null;
            
        }
    }
	public String Stream_String(java.io.InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (Exception e) {
	        return "";
	    }
	}
	public boolean Update_rates()
	{
		
		  try {
			InputStream i = getStream("https://raw.github.com/currencybot/open-exchange-rates/master/latest.json");
			//InputStream i = this.getResources().openRawResource(R.raw.test);
			if (i != null) {
				s = Stream_String(i);
				if (s != "") {
					return Update(s);
					
				}
				else
				{
					Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				  Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
			return false;
		}
		return false;	
	}
	public boolean Delete_rates()
	{
		CurrencyDbAdapter db = new CurrencyDbAdapter(this);
		db.open();
		db.deleteAllRate();
		db.close();
		return true;
	}
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Updating..");
                dialog.setMessage("Please wait while updating database...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                Update_rates();
                return dialog;
                
            }
            case 1: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait while loading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
        }
        return null;
    }
}
    
    
    
    
