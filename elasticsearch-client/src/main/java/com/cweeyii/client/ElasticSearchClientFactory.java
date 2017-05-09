package com.cweeyii.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
public class ElasticSearchClientFactory {
    private static final Map<ESConfig, TransportClient> cacheEsClientMap = new ConcurrentHashMap<>();

    public static Client build(ESConfig esConfig) {
        if (checkConfig(esConfig)) {
            return null;
        }
        TransportClient transportClient=cacheEsClientMap.get(esConfig);
        if(transportClient==null){
            synchronized (ElasticSearchClientFactory.class){
                if(transportClient==null){
                    transportClient=TransportClient.builder().settings(Settings.settingsBuilder().put("cluster.name", esConfig.getClusterName())).build();
                    List<InetSocketTransportAddress> servers = getServerList(esConfig.getHostName(), esConfig.getPort());
                    for (InetSocketTransportAddress inetSocketTransportAddress : servers) {
                        transportClient.addTransportAddress(inetSocketTransportAddress);
                    }
                    cacheEsClientMap.put(esConfig,transportClient);
                }
            }
        }
        return transportClient;
    }

    private static boolean checkConfig(ESConfig esConfig) {
        Preconditions.checkNotNull(esConfig);
        return !StringUtils.isEmpty(esConfig.getClusterName()) && !StringUtils.isEmpty(esConfig.getHostName()) && esConfig.getPort() > 0;
    }

    private static List<InetSocketTransportAddress> getServerList(String ips, int port) {
        List<InetSocketTransportAddress> servers = new ArrayList<>();
        List<String> serverList = Splitter.on(",").splitToList(ips);
        for (String server : serverList) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(server, port);
            servers.add(new InetSocketTransportAddress(inetSocketAddress));
        }
        return servers;
    }

}
