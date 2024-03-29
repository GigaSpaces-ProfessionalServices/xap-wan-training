package com.gigaspaces.training.wan.billbuddy.client;


import java.util.ArrayList;
import org.openspaces.core.GigaSpace;
import com.gigaspaces.training.wan.billbuddy.model.AccountStatus;
import com.gigaspaces.training.wan.billbuddy.model.CategoryType;
import com.gigaspaces.training.wan.billbuddy.model.Merchant;

/** 
 * Merchant Feeder class gets merchant name list which is stored in merchants data member. 
 * The class performing loop on the list and create user class and write it into space
 * 
 * The Class also enables creating one static user & write into the space
 * 
 * @author Gigaspaces
 */


public class MerchantFeeder {
	
	
	@SuppressWarnings("serial")
	private static final ArrayList<String> merchants=new ArrayList<String>(){{
	    add("Like Pace"); add("Konegsad"); add("SomeDisk"); add("Swakowsky"); 
	    add("Green Head band"); add("Shiruckou"); add("Eagle"); add("Lohitech"); 
	    add("The musicals"); add("SoccerMaster"); add("Fort"); add("2-Times"); 
	    add("Mazalaty"); add("jewelry 4 U"); add("Gems"); add("Hautika"); 

	}};
	
	public MerchantFeeder(){
		
	}
	
	// Method loads a list of merchants from the DataUtil class that serves as a user repository.
	// It then writes them into the space using a GigaSpace space proxy.
	// It reads users from the space & displays them into the console.
    
	public static void loadData(GigaSpace gigaSpace) throws Exception {
		System.out.println("Starting Merchant Feeder");
		System.out.println("Method: loadData - loads all merchants into the space");
		
        // merchantAccountId will serve as the Unique Identifier value
        
		Integer merchantAccountId = 1;
        
        CategoryType[] categoryTypes = CategoryType.values();        

        // for each merchant in the merchantList do:
        
        for (String merchantName : MerchantFeeder.merchants) {
        	
        	 // Check the merchant does not exist in the space already by trying to read it
			
        	Merchant foundMerchant = gigaSpace.readById(Merchant.class,merchantAccountId);
        	
        	// If Merchant was not found then create the Merchant and write it to the space
            
        	if (foundMerchant == null) {
            	Merchant merchant = new Merchant();
            	
            	merchant.setName(merchantName);
                merchant.setReceipts(0d);
                merchant.setFeeAmount(0d);
            
                // Select Random Category
 
                merchant.setCategory(categoryTypes[(int) ((categoryTypes.length - 1) * Math.random())]);
                merchant.setStatus(AccountStatus.ACTIVE);
                merchant.setMerchantAccountId(merchantAccountId);
                
                // Write Merchant to the space.
                
                gigaSpace.write(merchant);
                System.out.println("Added Merchant object with name: " + merchant.getName());
            }
            
            merchantAccountId++;
        }
        	
        System.out.println("Stopping Merchant Feeder");
    }

}
