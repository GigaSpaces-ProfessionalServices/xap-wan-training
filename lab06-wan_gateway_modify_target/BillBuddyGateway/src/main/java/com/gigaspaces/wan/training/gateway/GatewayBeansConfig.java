package com.gigaspaces.wan.training.gateway;

import java.util.Collections;
import java.util.List;

import org.openspaces.core.gateway.GatewayDelegation;
import org.openspaces.core.gateway.GatewayDelegatorFactoryBean;
import org.openspaces.core.gateway.GatewayLookup;
import org.openspaces.core.gateway.GatewayLookupsFactoryBean;
import org.openspaces.core.gateway.GatewaySinkFactoryBean;
import org.openspaces.core.gateway.GatewaySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Annotation-based equivalent of the os-gateway:* XSD elements the old pu.xml declared
 * (delegator, sink, lookups). There's no framework base class to extend here - unlike
 * EmbeddedSpaceBeansConfig (used by e.g. my-stateful-with-db), xap-openspaces/xap-admin ship no
 * "GatewayBeansConfig"-style annotation-config base for delegator/sink/lookups, so these are
 * built directly as plain @Bean factory methods. See docker/build-notes.md for the full
 * rationale, including why this needed a new compile-time dependency (xap-admin) that the old
 * XML version never had to declare.
 *
 * @Value defaults below (US) are only used if a value isn't passed at deploy time - same
 * fallback role the old PropertyPlaceholderConfigurer's default <props> played.
 */
@Configuration
public class GatewayBeansConfig {

    @Value("${localGatewayName:US}")
    private String localGatewayName;

    @Value("${remoteGatewayName:EMEA}")
    private String remoteGatewayName;

    @Value("${localSpaceUrl:jini://*/*/wanSpaceUS}")
    private String localSpaceUrl;

    @Value("${requiresBootstrap:false}")
    private boolean requiresBootstrap;

    @Value("${localLookupHost:us-gateway-gsc}")
    private String localLookupHost;

    @Value("${localLookupPort:4174}")
    private String localLookupPort;

    @Value("${localCommunicationPort:8201}")
    private String localCommunicationPort;

    @Value("${remoteLookupHost:emea-gateway-gsc}")
    private String remoteLookupHost;

    @Value("${remoteLookupPort:4174}")
    private String remoteLookupPort;

    @Value("${remoteCommunicationPort:8201}")
    private String remoteCommunicationPort;

    @Bean
    public GatewayLookupsFactoryBean gatewayLookups() {
        GatewayLookup local = new GatewayLookup();
        local.setGatewayName(localGatewayName);
        local.setHost(localLookupHost);
        local.setDiscoveryPort(localLookupPort);
        local.setCommunicationPort(localCommunicationPort);

        GatewayLookup remote = new GatewayLookup();
        remote.setGatewayName(remoteGatewayName);
        remote.setHost(remoteLookupHost);
        remote.setDiscoveryPort(remoteLookupPort);
        remote.setCommunicationPort(remoteCommunicationPort);

        GatewayLookupsFactoryBean lookups = new GatewayLookupsFactoryBean();
        lookups.setGatewayLookups(List.of(local, remote));
        return lookups;
    }

    // Calling gatewayLookups() here (rather than taking it as a method parameter) relies on
    // @Configuration's CGLIB proxying to return the same singleton instance - the annotation
    // equivalent of both os-gateway:delegator and os-gateway:sink referencing one shared
    // gateway-lookups="gatewayLookups" bean by id in the old XML.
    @Bean
    public GatewayDelegatorFactoryBean delegator() {
        GatewayDelegatorFactoryBean delegator = new GatewayDelegatorFactoryBean();
        delegator.setLocalGatewayName(localGatewayName);
        delegator.setGatewayLookups(gatewayLookups());
        delegator.setStartEmbeddedLus(true);
        delegator.setGatewayDelegations(Collections.singletonList(new GatewayDelegation(remoteGatewayName, null)));
        return delegator;
    }

    @Bean
    public GatewaySinkFactoryBean sink() {
        GatewaySinkFactoryBean sink = new GatewaySinkFactoryBean();
        sink.setLocalGatewayName(localGatewayName);
        sink.setGatewayLookups(gatewayLookups());
        sink.setStartEmbeddedLus(true);
        sink.setLocalSpaceUrl(localSpaceUrl);
        sink.setRequiresBootstrap(requiresBootstrap);
        sink.setGatewaySources(Collections.singletonList(new GatewaySource(remoteGatewayName)));
        return sink;
    }
}
