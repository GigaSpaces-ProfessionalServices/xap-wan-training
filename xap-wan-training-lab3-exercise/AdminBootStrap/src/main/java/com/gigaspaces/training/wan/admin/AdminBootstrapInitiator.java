package com.gigaspaces.training.wan.admin;

import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gateway.BootstrapResult;
import org.openspaces.admin.gateway.Gateway;
import org.openspaces.admin.gateway.GatewaySinkSource;

public class AdminBootstrapInitiator {

	public static void main(String[] args) {
		System.out.println("Creating Admin Factory");
		// TODO: get Admin Factory

		System.out.println("Waiting for US gateway");

		// TODO: get US Gateway

		System.out.println("Waiting for US to connect to EMEA Sink");

		// TODO: get US Gateway to connect to EMEA sink

		System.out.println("Starting Boostrap");

		// TODO: Start bootstrap process.

		System.out.println("Bootstrap process finished");
	}
}
