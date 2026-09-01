package com.gigaspaces.wan.training.modifytarget;

import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.space.Space;
import org.openspaces.core.gateway.GatewayTarget;

public class ModifyGatewayTargetInitiator {

	private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 30;

	public static void main(String[] args) {
		String locators = requireEnv("LOCATORS");
		String spaceName = requireEnv("SPACE_NAME");
		String gatewayName = requireEnv("GATEWAY_NAME");
		String action = requireEnv("ACTION").toLowerCase();
		long waitTimeoutSeconds = envLong("WAIT_TIMEOUT_SECONDS", DEFAULT_WAIT_TIMEOUT_SECONDS);

		if (!action.equals("add") && !action.equals("remove")) {
			System.err.println("ACTION must be 'add' or 'remove', got: " + action);
			System.exit(1);
		}

		System.out.println("Creating Admin Factory, locators=" + locators);
		Admin admin = new AdminFactory().addLocator(locators).useDaemonThreads(true).create();
		try {
			System.out.println("Waiting for space: " + spaceName);
			Space space = admin.getSpaces().waitFor(spaceName, waitTimeoutSeconds, TimeUnit.SECONDS);
			if (space == null) {
				System.err.println("Could not find space '" + spaceName + "' within " + waitTimeoutSeconds + " seconds.");
				System.exit(1);
			}

			if (action.equals("add")) {
				System.out.println("Adding gateway target '" + gatewayName + "' to space '" + spaceName + "'...");
				GatewayTarget gatewayTarget = new GatewayTarget(gatewayName);
				// The "true" here is the resetTarget flag (GS-14480, added in 15.8.1): it relaxes
				// the sequential-packet-numbering check normally enforced when a target is
				// (re-)added, so a target that was previously removed can be added back cleanly
				// instead of being rejected for a gap in packet numbers. Same as the reference
				// tool this lab is modeled on (wangateway_examples-main's ModifyTarget.java).
				space.getReplicationManager().addGatewayTarget(gatewayTarget, true);
				System.out.println("Gateway target '" + gatewayName + "' added.");
			} else {
				System.out.println("Removing gateway target '" + gatewayName + "' from space '" + spaceName + "'...");
				space.getReplicationManager().removeGatewayTarget(gatewayName);
				System.out.println("Gateway target '" + gatewayName + "' removed.");
			}
		} finally {
			admin.close();
		}
	}

	private static String requireEnv(String name) {
		String value = System.getenv(name);
		if (value == null || value.isEmpty()) {
			System.err.println("Usage: set " + name + " before running.");
			System.exit(1);
		}
		return value;
	}

	private static long envLong(String name, long defaultValue) {
		String value = System.getenv(name);
		return (value == null || value.isEmpty()) ? defaultValue : Long.parseLong(value);
	}
}
