package it.thezero.bitzero.adapters;

import it.thezero.bitzero.MainActivity;
import it.thezero.bitzero.R;
import it.thezero.bitzero.address.Address;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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

		if(addr.getValuta()==Address.Val[1][0]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_bitcoin));
		}else if(addr.getValuta()==Address.Val[1][1]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_litecoin));
		}else if(addr.getValuta()==Address.Val[1][2]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_dogecoin));
		}else if(addr.getValuta()==Address.Val[1][3]) {
			((ImageView) view.findViewById(R.id.valuta)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_zetacoin));
		}
		
		((TextView) view.findViewById(R.id.label)).setText(title);
		((TextView) view.findViewById(R.id.address)).setText("Address: "+addr.getAddress());
		if(addr.getValuta()==Address.Val[1][0]) {
			((TextView) view.findViewById(R.id.bitcoin)).setText("Balance: "+Address.toBTC(addr.getBalance())+" "+addr.getValuta());
		}else if(addr.getValuta()==Address.Val[1][1]) {
			((TextView) view.findViewById(R.id.bitcoin)).setText("Balance: "+addr.getBalance()+" "+addr.getValuta());
		}else if(addr.getValuta()==Address.Val[1][2]) {
			((TextView) view.findViewById(R.id.bitcoin)).setText("Balance: "+addr.getBalance()+" "+addr.getValuta());
		}else if(addr.getValuta()==Address.Val[1][3]) {
			((TextView) view.findViewById(R.id.bitcoin)).setText("Balance: "+addr.getBalance()+" "+addr.getValuta());
		}
		
		((TextView) view.findViewById(R.id.tx)).setText("Transaction: "+addr.getTx().toString());
		
		((ImageButton) view.findViewById(R.id.qrbtn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MainActivity.encodeBarcode("TEXT_TYPE", addr.getValuta(true)+":"+addr.getAddress()+"?label="+addr.getName());
			}
		});
		 
		return view;
	}

}
