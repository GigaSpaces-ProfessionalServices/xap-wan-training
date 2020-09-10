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

		// TODO: Implement the missing capability:
		// When conflicting changes are coming from EMEA to US disregard them
		// When conflicting changes are coming from US to EMEA override existing EMEA values
		// Use sourceGatewayName to distinguish the source name (defined in PU.XML as os-gateway:target)

		// Use conflict.getOperations() to get the relevant operation when there is a conflict
		// Use DataConflictOperation abort and override methods


	}
}