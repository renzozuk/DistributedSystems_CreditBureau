package br.ufrn.imd.gateway;

import java.util.HashMap;
import java.util.Map;

public class GatewayFactory {
    private final Map<String, Gateway> gateways = new HashMap<>();

    public GatewayFactory() {
    }

    public void registerGateway(String key, Gateway gateway) {
        gateways.put(key, gateway);
    }

    public Gateway getGateway(String key) {
        return gateways.get(key);
    }
}
