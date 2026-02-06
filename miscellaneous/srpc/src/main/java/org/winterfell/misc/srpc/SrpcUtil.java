package org.winterfell.misc.srpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class SrpcUtil {

    public static final Logger logger = LoggerFactory.getLogger(SrpcUtil.class);

    private static final int MAX_PORT = 65535;


    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     * @since 3.2.2
     */
    public static String simpleUUID() {
        return randomUUID().replace("-", "");
    }

    /**
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * find an avaliable port
     *
     * @param defaultPort
     * @return
     */
    public int findAvailablePort(int defaultPort) {
        int portTmp = defaultPort;
        while (portTmp < MAX_PORT) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = defaultPort--;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new RpcException("no available port.");
    }

    /**
     * check port used
     *
     * @param port
     * @return
     */
    public boolean isPortUsed(int port) {
        boolean used;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            used = false;
        } catch (IOException e) {
            logger.info("[rpc]: port[{}] is in use.", port);
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.info("");
                }
            }
        }
        return used;
    }

}
