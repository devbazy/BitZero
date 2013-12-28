package it.thezero.bitzero.address;

import java.text.DecimalFormat;

public class Address {
	private String address,name,valuta;
	private Integer n_tx,balance;
	
	public static String[][] Val = {{"bitcoin","litecoin","dogecoin"},{"BTC","LTC","DOG"},{"1","L","D"}};
	
	public Address(String v,String n,String a,Integer tx,Integer b) {
		setName(n);
		setAddress(a);
		setTx(tx);
		setBalance(b);
		setValuta(v);
	}

	public String toString(){
		return valuta+";"+address+";"+name+";"+n_tx+";"+balance;
	}
	
	public String getAddress(){
		return address;
	}
	
	private void setAddress(String a){
		address=a;
	}
	
	public String getName(){
		return name;
	}
	
	private void setName(String n){
		name=n;
	}
	
	public Integer getTx(){
		return n_tx;
	}
	
	private void setTx(Integer tx){
		n_tx=tx;
	}
	
	public Integer getBalance(){
		return balance;
	}
	
	private void setBalance(Integer b){
		balance=b;
	}
	
	public String getValuta(Boolean t){
		String v="";
		if(t){
			for(int i=0;i<Val[1].length;i++){
				if(valuta==Val[1][i]){
					v=Val[0][i];
				}
			}
		}else{
			v=valuta;
		}
		return v;
	}
	
	public String getValuta(){
		return valuta;
	}
	
	private void setValuta(String v){
		for(int i=0;i<Val[1].length;i++){
			if(v==Val[1][i]){
				valuta=v;
			}
		}
		// TODO exception (?)
	}
	
	public static String toBTC(Integer b){
		DecimalFormat formatter = new DecimalFormat("#.##########");
		String s = formatter.format(b*0.00000001);
		return s;
	}
	
	public static String parseValuta(Address a){
		return parseValuta(a.getAddress());
	}
	
	public static String parseValuta(String a){
		String ret = "";
		for(int i=0;i<Val[2].length;i++){
			if(a.startsWith(Val[2][i])){
				ret=Val[1][i];
			}
		}
		return ret;
	}
}

