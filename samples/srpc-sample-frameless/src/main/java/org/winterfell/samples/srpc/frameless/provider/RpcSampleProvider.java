package org.winterfell.samples.srpc.frameless.provider;


import org.winterfell.samples.srpc.api.PersonService;
import org.winterfell.misc.srpc.AsyncResultHandler;
import org.winterfell.misc.srpc.RpcProviderFactory;

/**
 * RPC server
 *
 * @author alex
 */
public class RpcSampleProvider {

    public static void main(String[] args) {
        RpcProviderFactory providerFactory = RpcProviderFactory.builder().accessToken("whosyourdaddy")
                .corePoolSize(10)
                .maxPoolSize(64)
                .port(5555)
                .build();
        providerFactory.start(new AsyncResultHandler() {
            @Override
            public void complete(Object result) {
                System.out.println("rpc server 启动成功！");
                providerFactory.addService(PersonService.class.getName(), new PersonServiceImpl());
            }
            @Override
            public void failed(Throwable error) {
                System.err.println("rpc server 启动失败");
            }
        });
    }
}
