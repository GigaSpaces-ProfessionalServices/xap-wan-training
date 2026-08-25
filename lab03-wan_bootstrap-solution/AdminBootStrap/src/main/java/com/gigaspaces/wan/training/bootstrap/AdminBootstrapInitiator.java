package com.gigaspaces.wan.training.bootstrap;

import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gateway.BootstrapResult;
import org.openspaces.admin.gateway.Gateway;
import org.openspaces.admin.gateway.GatewaySinkSource;

public class AdminBootstrapInitiator {

	public static void main(String[] args) {
		String locators = System.getenv("GS_LOCATORS");
		if (locators == null || locators.isEmpty()) {
			System.err.println("Usage: set GS_LOCATORS (e.g. us-manager) before running.");
			System.exit(1);
		}

		System.out.println("Creating Admin Factory");
		Admin admin = new AdminFactory().addLocator(locators).useDaemonThreads(true).create();
		try {
			System.out.println("Waiting for US gateway");
			Gateway USGateway = admin.getGateways().waitFor("US");
			System.out.println("Waiting for US to connect to EMEA Sink");
			GatewaySinkSource EMEASinkSource = USGateway.waitForSinkSource("EMEA");
			System.out.println("Starting Bootstrap");
			BootstrapResult bootstrapResult = EMEASinkSource.bootstrapFromGatewayAndWait(3600, TimeUnit.SECONDS);
			System.out.println(bootstrapResult.toString());
			System.out.println("Bootstrap process finished");
		} finally {
			admin.close();
		}
	}
}
