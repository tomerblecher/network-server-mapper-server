package ost.snm.model.wrappers;

import ost.snm.model.Server;

public class ServerWrapper extends PingWrapper {
    private Server server;
    private String segmentSubnet;

    @Override
    public void generateJson(String path) {

    }

    @Override
    public String getHash() {
        return this.server.getHash();
    }
}
