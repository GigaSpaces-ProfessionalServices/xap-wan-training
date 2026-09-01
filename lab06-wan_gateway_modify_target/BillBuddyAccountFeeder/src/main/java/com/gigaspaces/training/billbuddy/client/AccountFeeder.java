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
			System.err.println("Usage: AccountFeeder <spaceName> [users|merchants|payments|all]");
			System.err.println("Lookup locators come from the -Dcom.gs.jini_lus.locators system property.");
			System.err.println("The second argument defaults to 'all'; this lab's docker-compose invokes it");
			System.err.println("one phase at a time (via FEED_SET) so each phase's writes are distinguishable");
			System.err.println("from the last, since every feeder here is idempotent by ID against whichever");
			System.err.println("local space it targets and re-running the same phase against a space that");
			System.err.println("already has that data is a no-op.");
			System.exit(1);
		}

		// Get a proxy to the space using a configurer.
		// Locators/groups are resolved from -Dcom.gs.jini_lus.locators (and optionally
		// -Dcom.gs.jini_lus.groups), so no host/port is hardcoded here.
		System.out.println("spaceName - " + args[0]);
		SpaceProxyConfigurer spaceConfigurer = new SpaceProxyConfigurer(args[0]);


	  	// Create a space proxy
	  	GigaSpace gigaSpace = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();

		String feedSet = args.length > 1 ? args[1].toLowerCase() : "all";
		System.out.println("feedSet - " + feedSet);

    	try {

    		switch (feedSet) {
    			case "users":
    				UserFeeder.loadData(gigaSpace);
    				break;
    			case "merchants":
    				MerchantFeeder.loadData(gigaSpace);
    				break;
    			case "payments":
    				// Needs Users and Merchants already present in this same local space -
    				// PaymentFeeder picks random existing user/merchant ids to reference.
    				PaymentFeeder.loadData(gigaSpace);
    				break;
    			case "all":
    				UserFeeder.loadData(gigaSpace);
    				MerchantFeeder.loadData(gigaSpace);
    				PaymentFeeder.loadData(gigaSpace);
    				break;
    			default:
    				System.err.println("Unknown feed set: " + feedSet + " (expected users|merchants|payments|all)");
    				System.exit(1);
    		}

    	} catch (Exception ex){
    		System.out.println(ex.getMessage());
    		System.out.println(ex.getStackTrace());
    	}

	}

}
