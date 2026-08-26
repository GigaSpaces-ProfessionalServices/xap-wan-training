package com.gigaspaces.wan.training.bootstrap;

import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gateway.BootstrapResult;
import org.openspaces.admin.gateway.Gateway;
import org.openspaces.admin.gateway.GatewaySinkSource;

public class AdminBootstrapInitiator {

	public static void main(String[] args) {
		String locators = System.getenv("LOCATORS");
		if (locators == null || locators.isEmpty()) {
			System.err.println("Usage: set LOCATORS (e.g. us-manager) before running.");
			System.exit(1);
		}

		System.out.println("Creating Admin Factory");
		// TODO: get Admin Factory
		Admin admin = null;
		try {
			System.out.println("Waiting for US gateway");
			// TODO: get US Gateway
			Gateway USGateway = null;
			System.out.println("Waiting for US to connect to EMEA Sink");
			// TODO: get US Gateway to connect to EMEA sink
			GatewaySinkSource EMEASinkSource = null;
			System.out.println("Starting Bootstrap");
			// TODO: Start bootstrap process.
		} finally {
			admin.close();
		}
	}
}
