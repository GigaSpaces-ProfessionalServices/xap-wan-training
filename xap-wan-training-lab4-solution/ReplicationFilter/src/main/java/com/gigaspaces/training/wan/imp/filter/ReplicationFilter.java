package com.gigaspaces.training.wan.imp.filter;

import com.gigaspaces.training.wan.billbuddy.model.Continent;
import com.gigaspaces.training.wan.billbuddy.model.User;
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
		if (replicationTargetName.equals("gateway:EMEA")) {
			if (replicationFilterEntry.getClassName().equals(
					User.class.getName())) {
				Continent loc = (Continent) replicationFilterEntry
						.getFieldValue("location");
				if (loc != Continent.Europe) {
					System.out.println("found user in region: " + loc
							+ " DO NOT copy this user to EMEA");
					replicationFilterEntry.discard();
				} else {
					System.out.println("found user in region: " + loc
							+ " copy this user to EMEA");
				}
			}
		} else {
			System.out
					.println("ReplicationFilter filter not defined for gateway:"
							+ replicationTargetName);

		}
	}
}
