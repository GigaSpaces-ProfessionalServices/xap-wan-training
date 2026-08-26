package com.gigaspaces.wan.training.filter.impl;

import com.gigaspaces.training.billbuddy.model.Continent;
import com.gigaspaces.training.billbuddy.model.User;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.cluster.IReplicationFilter;
import com.j_spaces.core.cluster.IReplicationFilterEntry;
import com.j_spaces.core.cluster.ReplicationPolicy;

public class ReplicationFilter implements IReplicationFilter {

	@Override
	public void close() { 
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IJSpace arg0, String arg1, ReplicationPolicy arg2) {
		// TODO Auto-generated method stub
		System.out.println("Starting HKReplicationFilter"); 
	}

	@Override
	public void process(int direction,
			IReplicationFilterEntry replicationFilterEntry,
			String replicationTargetName) {
		// TODO replicate to EMEA only the users with location Europe
	}
}
