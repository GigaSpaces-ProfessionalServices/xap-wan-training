package com.gigaspaces.training.billbuddy.client;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

/**
 * UserFeederActivator class create a standalone proxy connection to the space using configurer.
 * The class then activates the UserFeeder to write all user into the space.
 * @author Gigaspaces
 */

public class AccountFeeder {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: AccountFeeder <spaceName>");
			System.err.println("Lookup locators come from the -Dcom.gs.jini_lus.locators system property.");
			System.exit(1);
		}

		// Get a proxy to the space using a configurer.
		// Locators/groups are resolved from -Dcom.gs.jini_lus.locators (and optionally
		// -Dcom.gs.jini_lus.groups), so no host/port is hardcoded here.
		System.out.println("spaceName - " + args[0]);
		SpaceProxyConfigurer spaceConfigurer = new SpaceProxyConfigurer(args[0]);

	  	
	  	// Create a space proxy
	  	GigaSpace gigaSpace = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();
    	
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
