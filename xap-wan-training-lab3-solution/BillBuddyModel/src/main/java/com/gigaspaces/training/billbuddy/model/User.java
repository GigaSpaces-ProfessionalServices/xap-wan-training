package com.gigaspaces.training.billbuddy.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.index.SpaceIndexType;



/** 
* User class is a POJO which has user account information 
* 
* @author Gigaspaces
*/

@SpaceClass
public class User {

	private Integer userAccountId;
    private String name;
    private Address address;
    private Double balance;
    private Double creditLimit;
    private AccountStatus status;
    private Continent location;
    


	public User(Integer userAccountId) {
        this.userAccountId = userAccountId;
    }

    public User() {
    }

    @SpaceId(autoGenerate = false)
    @SpaceRouting
    public Integer getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Integer userAccountId) {
        this.userAccountId = userAccountId;
    }

	public void setName(String name) {
		this.name = name;
	}
	
	@SpaceIndex(type=SpaceIndexType.BASIC)
	public String getName() {
		return name;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public void setCreditLimit(Double creditLimit) {
		this.creditLimit = creditLimit;
	}

	@SpaceIndex(type=SpaceIndexType.EXTENDED)
	public Double getCreditLimit() {
		return creditLimit;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}
	
    public Continent getLocation() {
		return location;
	}

	public void setLocation(Continent location) {
		this.location = location;
	}
}
