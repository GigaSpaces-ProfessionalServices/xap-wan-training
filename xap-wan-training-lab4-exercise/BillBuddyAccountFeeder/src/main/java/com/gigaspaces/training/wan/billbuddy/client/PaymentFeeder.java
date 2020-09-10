package com.gigaspaces.training.wan.billbuddy.client;
import java.util.Date;
import java.util.Random;

import org.openspaces.core.GigaSpace;

import com.gigaspaces.training.wan.billbuddy.model.Merchant;
import com.gigaspaces.training.wan.billbuddy.model.Payment;
import com.gigaspaces.training.wan.billbuddy.model.TransactionStatus;
import com.gigaspaces.training.wan.billbuddy.model.User;

/** 
 * User Feeder class reads userNameList which is stored in users data member. 
 * The class performing loop on the list and create user class and write it into space
 * 
 * The Class also enables creating one static user & write into the space
 * 
 * @author Gigaspaces
 */


public class PaymentFeeder {
	

	public PaymentFeeder(){
	}

	// Method loads a list of users from users data member.
	// It then writes them into the space using a GigaSpace space proxy.
	// It reads users from the space & displays them into the console.
	
	public static void loadData(GigaSpace gigaSpace) throws Exception {
		System.out.println("Starting Payment Feeder");
		System.out.println("Method: loadData - Generate random payments");
        
        int userAmount = gigaSpace.count(new User());
        int merchantAmount = gigaSpace.count(new Merchant());
        Random rand = new Random();

        // Read a list of user & iterate over them one by one.
        int paymentID = gigaSpace.count(new Payment());
        int startid,endid;
        if (paymentID > 0) {
        	startid = paymentID+50;
        	endid = paymentID+550;
        } else {
        	startid = 1;
        	endid = 500;
        }
        
        for (int i=startid;  i<endid ; i++ ) {
          
        	int randomUser = rand.nextInt(userAmount-1) + 1;
        	int randomMechant = rand.nextInt(merchantAmount-1) + 1;	
        	
        	System.out.println("Random User is:" + randomUser + " and Random Merchant is:" + randomMechant);
        	// create user
        	
        	Payment payment = new Payment();
        	payment.setReceivingMerchantId(randomMechant);
        	payment.setPayingAccountId(randomUser);
        	payment.setDescription("Online Purchase");
        	payment.setPaymentAmount(rand.nextDouble() *10000);
        	payment.setPaymentId(String.valueOf(i));
        	payment.setStatus(TransactionStatus.PROCESSED);
        	payment.setCreatedDate(new Date());
        	
        	// Write payment to the space
            gigaSpace.write(payment);
            
            System.out.println("Payment has been made " + payment.toString());
            if (i%25 == 1){
            	Thread.sleep(500);
            }
        }

        System.out.println("Stopping Payment Feeder writing process");
        
    }

}
