package com.gigaspaces.training.wan.imp.conflict;

import com.gigaspaces.cluster.replication.gateway.conflict.DataConflict;
import com.gigaspaces.cluster.replication.gateway.conflict.DataConflictOperation;
import com.gigaspaces.cluster.replication.gateway.conflict.EntryAlreadyInSpaceConflict;
import com.gigaspaces.cluster.replication.gateway.conflict.EntryLockedUnderTransactionConflict;
import com.gigaspaces.cluster.replication.gateway.conflict.EntryNotInSpaceConflict;
import com.gigaspaces.cluster.replication.gateway.conflict.EntryVersionConflict;

public class UserConflictHandler extends com.gigaspaces.cluster.replication.gateway.conflict.ConflictResolver  {

	@Override
	public void onDataConflict(String sourceGatewayName, DataConflict conflict) {
				
		// If we get conflict and HK is the source then we disregard those changes
		if (sourceGatewayName.equals("EMEA")) {
			conflict.abortAll();
		}
		
		// We treat US as higher priority all changes of US will over take
		if (sourceGatewayName.equals("US")) {
			for (DataConflictOperation operation : conflict.getOperations()) {
				if (operation.hasConflict()) {
					if ( operation.getConflictCause() instanceof EntryNotInSpaceConflict) {
						operation.override();
					}
					if ( operation.getConflictCause() instanceof EntryAlreadyInSpaceConflict) {
						operation.override();
					}
					if ( operation.getConflictCause() instanceof EntryVersionConflict) {
						operation.override();
					}
					if ( operation.getConflictCause() instanceof EntryLockedUnderTransactionConflict) {
						operation.override();
					}
					
					
				}
			}
		}
	}

}
