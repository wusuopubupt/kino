package com.mathandcs.kino.effectivejava.agile.factoryMethod;

/**
 * Created by dashwang on 11/22/17.
 */
public class SenderFactory {
    public Sender produce(String type) {
		if("mail".equals(type)) {
			return new MailSender();
		} else if("sms".equals(type)) {
			return new SmsSender();
		} else {
			System.out.println("Unknown type: " + type);
			return null;
		}	
    }
}
