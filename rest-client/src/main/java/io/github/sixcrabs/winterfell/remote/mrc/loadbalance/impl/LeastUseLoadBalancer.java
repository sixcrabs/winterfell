//package cn.piesat.v.remote.mrc.loadbalance.impl;
//
//import cn.piesat.v.remote.mrc.loadbalance.AbstractLoadBalancer;
//import cn.piesat.v.remote.mrc.loadbalance.Resource;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * <p>
// * .最少连接算法选择当前连接数最少的服务器
// * </p>
// *
// * @author Alex
// * @version v1.0 2025/3/7
// */
//public class LeastUseLoadBalancer extends AbstractLoadBalancer {
//
//    private final Map<String, Integer> connectionCounts = new ConcurrentHashMap<>();
//
//    @Override
//    public void setResources(Resource... resources) {
//        super.setResources(resources);
//        for (Resource resource : resources) {
//            connectionCounts.put(resource.getUrl(), 0);
//        }
//    }
//
//    /**
//     * @return
//     */
//    @Override
//    public String name() {
//        return "LeastUse";
//    }
//
//    /**
//     * 获取下一个资源的url
//     *
//     * @return
//     */
//    @Override
//    public String getNextResource() {
//        String selectedServer = null;
//        int minConnections = Integer.MAX_VALUE;
//        for (Resource server : resources) {
//            int connections = connectionCounts.get(server.getUrl());
//            if (connections < minConnections) {
//                minConnections = connections;
//                selectedServer = server.getUrl();
//            }
//        }
//        connectionCounts.put(selectedServer, minConnections + 1);
//        return selectedServer;
//    }
//
//    public void connectionClosed(String server) {
//        connectionCounts.put(server, connectionCounts.get(server) - 1);
//    }
//
//    public static void main(String[] args) {
//        LeastUseLoadBalancer loadBalancer = new LeastUseLoadBalancer();
//        loadBalancer.setResources(new Resource().setUrl("server1"), new Resource().setUrl("server2"), new Resource().setUrl("server3"));
//        for (int i = 0; i < 100; i++) {
//            System.out.println("Request " + (i + 1) + " routed to: " + loadBalancer.getNextResource());
//            if (i % 10 == 0) {
//                loadBalancer.connectionClosed("server1");
//            }
//        }
//    }
//}
