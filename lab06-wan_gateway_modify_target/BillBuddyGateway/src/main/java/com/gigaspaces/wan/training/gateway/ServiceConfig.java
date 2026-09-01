package com.gigaspaces.wan.training.gateway;

import org.openspaces.config.DefaultServiceConfig;
import org.springframework.context.annotation.*;

@Configuration
@Import({DefaultServiceConfig.class, GatewayBeansConfig.class})
public class ServiceConfig {
}
