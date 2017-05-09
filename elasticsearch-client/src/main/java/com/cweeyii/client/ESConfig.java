package com.cweeyii.client;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class ESConfig {
    private String clusterName;
    private String hostName;
    private int port;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
