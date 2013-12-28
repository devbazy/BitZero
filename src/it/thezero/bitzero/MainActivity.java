package it.thezero.bitzero;

import it.thezero.bitzero.adapters.CoinCard;
import it.thezero.bitzero.address.Address;

import com.fima.cardsui.views.CardUI;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	static Map<String, String> addr = new HashMap<String, String>();
    Handler handler; 
    private CardUI mCardView;
    public static AlertDialog idia;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Load address from storage
		loadArray(addr);
		
		// Check if there are address
		if(addr.size()<1){
			// BitZero is not a wallet
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(R.string.dialog_msg_bitzero)
				.setTitle(R.string.dialog_title_bitzero)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.dismiss();
	               }
	           }).create().show();
			
		} else {
			
			// Let's do some background stuff
			mCardView = (CardUI) findViewById(R.id.cardsview);
			mCardView.setSwipeable(false);
			
			for (Map.Entry<String, String> entry : addr.entrySet()) {
				(new Fetch()).execute(entry.getValue(),entry.getKey());
			}
			
			Log.d("Cards",String.valueOf(mCardView.getChildCount()));
			
			mCardView.refresh();
			
			mCardView.setLongClickable(true);
			mCardView.setOnLongClickListener(null);
			
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	LayoutInflater inflater = this.getLayoutInflater();
    	final View v = inflater.inflate(R.layout.dialog_address, null);
    	v.findViewById(R.id.imageButtonQr).setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			idia.dismiss();
    			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
    			integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    		}
    	});
    	builder.setView(v)
    		.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
    		@Override
               public void onClick(DialogInterface dialog, int id) {
            	   	String name_dialog = ((EditText)v.findViewById(R.id.name)).getText().toString();
            	   	String address_dialog = ((EditText)v.findViewById(R.id.address)).getText().toString();
            	   	if (!(name_dialog.isEmpty() || address_dialog.isEmpty())){
            	   
            	   		Toast.makeText(getApplicationContext(), address_dialog, Toast.LENGTH_LONG).show();

            	   		addr.put(address_dialog,name_dialog);
            	   		saveArray(addr);
            	   		
            	   		mCardView.clearCards();
            	   		
            	   		for (Map.Entry<String, String> entry : addr.entrySet()) {
            				(new Fetch()).execute(entry.getValue(),entry.getKey());
            			}
            	   		
            	   	}else{
            	   		showDialog(R.string.empty,getString(R.string.empty_dialog));
            	   	}
        	   		dialog.dismiss();
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
            	   dialog.dismiss();
               }
           });
    	idia = builder.create();
	}
	
	@Override
	protected void onPause() {
        super.onPause();
        saveArray(addr);
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	saveArray(addr);
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (result != null) {
			String contents = result.getContents();
			if (contents != null) { 
				// TODO QR Parse
				String[] qr = QrParse(contents);
				showInsertDialog(qr[0],qr[1]);
			} else {
				showDialog(R.string.result_failed, getString(R.string.result_failed_why));
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
			case R.id.add:
				// TODO Etichetta
				showInsertDialog();
				return true;

			case R.id.action_about:
				/*;*/
				return true;
				
			case R.id.action_settings:
				//startActivity(new Intent(this,SettingsActivity.class));
				return true;
		}
		return false;
	}
    
	public boolean saveArray(Map<String,String> a){
	    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor mEdit1 = sp.edit();
	    mEdit1.putInt("Address_size", a.size()); /* sKey is an array */ 
	    
	    int i=0;

	    for (Map.Entry<String, String> entry : a.entrySet()) {
	    	mEdit1.remove("Address_" + i);
	    	mEdit1.putString("Address_" + i, entry.getKey());
	    	mEdit1.remove("Name_" + i);
	    	mEdit1.putString("Name_" + i, entry.getValue()); 
			i++;
		}
	    
	    return mEdit1.commit();     
	}

	public void loadArray(Map<String,String> a){  
	    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
	    a.clear();
	    int sizea = sp.getInt("Address_size", 0);  

	    for(int i=0;i<sizea;i++) 
	    {
	        a.put(sp.getString("Address_" + i, null),sp.getString("Name_" + i, null));
	    }
	}
	
    private class Fetch extends AsyncTask<String,Void,Void> {
    	private Address coinAddr;
    	
		protected Void doInBackground(String... param) {
			String val=Address.parseValuta(param[1]);
			aparse(param[0],param[1],val);
			return null;
		}
		
		protected void aparse(final String l, String a,String val) {	
			// TODO if valuta
			if(val==Address.Val[0][0]){
				String read = request("http://blockchain.info/address/"+a+"?format=json");
				JSONObject jsono;
				try {
					jsono = new JSONObject(read);
					coinAddr = new Address(Address.Val[0][0],l,jsono.getString("address"),jsono.getInt("n_tx"),jsono.getInt("final_balance"));
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(val==Address.Val[0][1]){
				int received = Integer.valueOf(request("http://explorer.litecoin.net/chain/Litecoin/q/getreceivedbyaddress/"+a));
				int sent = Integer.valueOf(request("http://explorer.litecoin.net/chain/Litecoin/q/getsentbyaddress/"+a));
				// received - sent
				coinAddr = new Address(Address.Val[0][1],l,a,-1,received-sent);
			}else if(val==Address.Val[0][2]){
				int balance = Integer.valueOf(request("http://dogechain.info/chain/Dogecoin/q/addressbalance/"+a));
				// received - sent
				coinAddr = new Address(Address.Val[0][2],l,a,-1,balance);
				Log.d("",Address.Val[0][2]);
			}
			try {
				CoinCard aCard = new CoinCard(coinAddr);
				aCard.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent it = new Intent(MainActivity.this,EditActivity.class);
						it.putExtra("label", coinAddr.getName());
						it.putExtra("addr", coinAddr.getAddress());
						startActivity(it);
					}
				});
				mCardView.addCard(aCard);
			} catch (Exception e) {
		    	e.printStackTrace();
			}
		}
		
		protected void onPostExecute(Void result) {			
			mCardView.refresh();
			return;			
		}
	}
    
    private void showDialog(int title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
    
    private void showInsertDialog() {
    	idia.show();
    }
    
    private void showInsertDialog(String n, String a) {
    	((EditText)idia.findViewById(R.id.name)).setText(n);
    	((EditText)idia.findViewById(R.id.address)).setText(a);
    	idia.show();
    }
    
    public String request(String URL) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(MainActivity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			runOnUiThread(new Runnable() {
			    public void run() {
			    	AlertDialog.Builder dbuilder = new AlertDialog.Builder(MainActivity.this);
					dbuilder.setMessage(R.string.dialog_msg_uhost)
					.setTitle(R.string.dialog_title_uhost)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).create().show();
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
    }
    
    public String[] QrParse(String qrtext) {
    	String valuta = qrtext.split(":")[0];
    	String addr_id = qrtext.split(":")[1].substring(0,34);
    	String param = qrtext.split(":")[1].substring(35);
    	String[] p = param.split("=");
    	String label = "Qr";
    	Map<String,String> amap = new HashMap<String,String>();
    	for(int i=0;i<p.length;i++){
    		if(i%2==0){
    			p[i]=p[i].replace("?","");
    			amap.put(p[i], p[i+1]);
    			Log.d("<3", p[i]+ p[i+1]);
    		}
    	}
    	try{
	    	if (!(amap.get("label").isEmpty())){
	    		label=amap.get("label");
	    	}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	String[] r = {label,addr_id};
    	return r;
    	// TODO End this function
    }
}
