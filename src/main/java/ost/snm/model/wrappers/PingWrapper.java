package ost.snm.model.wrappers;

import lombok.Getter;

public abstract class PingWrapper implements Runnable {
    @Getter
    private long timestamp;

    public PingWrapper() {
        this.timestamp = System.currentTimeMillis();
    }

    public abstract void generateJson(String path);
    public abstract String getHash();

    @Override
    public void run() {
        System.out.println("IM RUNNING ON PING!"+getHash());
    }
}
