package com.neoterux.server.api;

import java.util.Map;

public final class ClientInfo {

    private final Map<String, byte[]> imgMap;

    private ClientInfo(Map<String, byte[]> map) {
        this.imgMap = map;
    }
}
