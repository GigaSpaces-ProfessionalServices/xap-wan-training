package com.gigaspaces.training.wan.billbuddy.client;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.j_spaces.core.IJSpace;


/** 
 * UserFeederActivator class create a standalone proxy connection to the space using configurer.
 * The class then activates the UserFeeder to write all user into the space.
 * @author Gigaspaces
 */

public class AccountFeeder {

	public static void main(String[] args) {
    	
		// Get a proxy to the space using a configurer
		
		// String GS_LOOKUP_LOCATORS = System.getenv("GS_LOOKUP_LOCATORS");
		String lookupURL = "jini://"+ args[0] + "/*/" + args[1];
		System.out.println("lookupURL - " + lookupURL);
		UrlSpaceConfigurer spaceConfigurer = new UrlSpaceConfigurer(lookupURL);
		spaceConfigurer.lookupGroups(args[0]);
	  	IJSpace space = spaceConfigurer.space();
	  	
	  	// Create a space proxy
	  	
	  	GigaSpace gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();
    	
    	try {
    		
    		// Write users into the space 
    		
    		UserFeeder.loadData(gigaSpace);
    		
    		// Write merchants into the space 
    		
    		MerchantFeeder.loadData(gigaSpace);
    		
    		// Write payments into the space 
    		PaymentFeeder.loadData(gigaSpace);

    	
    	} catch (Exception ex){
    		System.out.println(ex.getMessage());
    		System.out.println(ex.getStackTrace());
    	}
    	
	}

}
