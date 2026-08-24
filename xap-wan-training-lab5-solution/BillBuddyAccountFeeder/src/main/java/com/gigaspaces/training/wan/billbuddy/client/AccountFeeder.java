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

		// Get a proxy to the US space using a configurer

		// String GS_LOOKUP_LOCATORS = System.getenv("GS_LOOKUP_LOCATORS");
		String lookupURL = "jini://"+ args[0] + "/*/" + args[1];
		System.out.println("lookupURL - " + lookupURL);
		UrlSpaceConfigurer spaceConfigurer = new UrlSpaceConfigurer(lookupURL);
		spaceConfigurer.lookupGroups(args[0]);
	  	IJSpace space = spaceConfigurer.space();

	  	// Create a space proxy

	  	GigaSpace gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();

	  	// Get a proxy to the EMEA space using a configurer, so the same User records
	  	// can be written to both sides and trigger a real WAN gateway conflict.

	  	String lookupURLEmea = "jini://"+ args[2] + "/*/" + args[3];
	  	System.out.println("lookupURL - " + lookupURLEmea);
	  	UrlSpaceConfigurer spaceConfigurerEmea = new UrlSpaceConfigurer(lookupURLEmea);
	  	spaceConfigurerEmea.lookupGroups(args[2]);
	  	IJSpace spaceEmea = spaceConfigurerEmea.space();

	  	GigaSpace gigaSpaceEmea = new GigaSpaceConfigurer(spaceEmea).gigaSpace();

    	try {

    		// Write users into both spaces concurrently, racing the WAN replication,
    		// so the same userAccountId is written independently on both sides and
    		// triggers a real gateway conflict instead of one side skipping via readById.

    		Thread usWriter = new Thread(() -> {
    			try {
    				UserFeeder.loadData(gigaSpace);
    			} catch (Exception ex) {
    				System.out.println(ex.getMessage());
    			}
    		});
    		Thread emeaWriter = new Thread(() -> {
    			try {
    				UserFeeder.loadData(gigaSpaceEmea);
    			} catch (Exception ex) {
    				System.out.println(ex.getMessage());
    			}
    		});
    		usWriter.start();
    		emeaWriter.start();
    		usWriter.join();
    		emeaWriter.join();

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
