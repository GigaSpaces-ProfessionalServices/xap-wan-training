package com.gigaspaces.training.wan.feeder;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.util.Random;

/**
 * Standalone feeder for lab10-active_active_on_nodes. Unlike the other labs in this
 * training set, there is no shared model module here: the "WanEvent" type is defined
 * on the fly with a SpaceTypeDescriptor and written as schema-free SpaceDocuments, so
 * this one class is the entire client - nothing to build or deploy alongside it.
 *
 * Run from any machine that can reach one of the two nodes over the network:
 *   java -Dcom.gs.jini_lus.locators=<node-host>:<discoveryPort> \
 *        -jar wan-feeder.jar <spaceName> <originSite> [count]
 */
public class DocumentFeeder {

    private static final String TYPE_NAME = "WanEvent";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: DocumentFeeder <spaceName> <originSite> [count]");
            System.err.println("Lookup locators come from the -Dcom.gs.jini_lus.locators system property.");
            System.exit(1);
        }

        String spaceName = args[0];
        String originSite = args[1];
        int count = args.length > 2 ? Integer.parseInt(args[2]) : 100;

        System.out.println("spaceName - " + spaceName);
        SpaceProxyConfigurer spaceConfigurer = new SpaceProxyConfigurer(spaceName).lookupTimeout(30000);
        GigaSpace gigaSpace = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();

        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(TYPE_NAME)
                .idProperty("id")
                .routingProperty("id")
                .addFixedProperty("id", String.class)
                .addFixedProperty("originSite", String.class)
                .addFixedProperty("amount", Double.class)
                .addFixedProperty("createdAt", Long.class)
                .create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        Random random = new Random();
        try {
            for (int i = 0; i < count; i++) {
                String id = originSite + "-" + System.currentTimeMillis() + "-" + i;
                double amount = Math.round(random.nextDouble() * 10000) / 100.0;

                SpaceDocument event = new SpaceDocument(TYPE_NAME)
                        .setProperty("id", id)
                        .setProperty("originSite", originSite)
                        .setProperty("amount", amount)
                        .setProperty("createdAt", System.currentTimeMillis());

                gigaSpace.write(event);
            }
            System.out.println("Wrote " + count + " " + TYPE_NAME + " documents from " + originSite + " into " + spaceName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
