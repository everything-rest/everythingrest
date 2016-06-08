package rest.everything.core

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress

/**
 *
 */
class ContextHolder {

    TransportClient transportClient
    private static ContextHolder instance

    private ContextHolder(){
        transportClient = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    public static ContextHolder getInstance(){
        if ( instance == null )
            instance = new ContextHolder()
        return instance
    }
}
