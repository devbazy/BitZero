package it.thezero.bitzero.adapters;

import it.thezero.bitzero.R;
import it.thezero.bitzero.address.Address;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

public class CoinCard extends Card {
	private Address addr;
	
	public CoinCard(String v,String n,String a,Integer tx,Integer b){
		super(n);
		addr=new Address(v,n,a,tx,b);
	}

	public CoinCard(Address a){
		super(a.getName());
		addr=a;
	}
	
	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_ex, null);
		Log.d("",addr.getAddress()+" "+addr.getValuta());
		if(addr.getValuta()==Address.Val[0][0]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_bitcoin));
		}else if(addr.getValuta()==Address.Val[0][1]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_litecoin));
		}else if(addr.getValuta()==Address.Val[0][2]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_dogecoin));
		}
		
		((TextView) view.findViewById(R.id.label)).setText(title);
		((TextView) view.findViewById(R.id.address)).setText("Address: "+addr.getAddress());
		((TextView) view.findViewById(R.id.bitcoin)).setText("Balance: "+Address.toBTC(addr.getBalance())+" BTC");
		((TextView) view.findViewById(R.id.tx)).setText("Transaction: "+addr.getTx().toString());
		
		return view;
	}

}
