package com.mathandcs.kino.agile.factoryMethod;

/**
 * Created by dashwang on 11/22/17.
 */
public class FactoryTest {
	public static void main(String[] args) {
		SenderFactory factory = new SenderFactory();
		Sender sender = factory.produce("sms");
  		sender.send();
	}
}
