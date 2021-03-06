package org.infinispan.client.hotrod.impl.operations;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.impl.protocol.Codec;
import org.infinispan.client.hotrod.impl.protocol.HeaderParams;
import org.infinispan.client.hotrod.impl.transport.Transport;
import org.infinispan.client.hotrod.impl.transport.TransportFactory;

/**
 * Reads more keys at a time. Specified <a href="http://community.jboss.org/wiki/HotRodBulkGet-Design">here</a>.
 *
 * @author Mircea.Markus@jboss.com
 * @since 4.1
 */
public class BulkGetOperation<K, V> extends RetryOnFailureOperation<Map<K, V>> {

   private final int entryCount;

   public BulkGetOperation(Codec codec, TransportFactory transportFactory, byte[] cacheName, AtomicInteger topologyId,
                           int flags, ClientIntelligence clientIntelligence, int entryCount) {
      super(codec, transportFactory, cacheName, topologyId, flags, clientIntelligence);
      this.entryCount = entryCount;
   }

   @Override
   protected Transport getTransport(int retryCount, Set<SocketAddress> failedServers) {
      return transportFactory.getTransport(failedServers, cacheName);
   }

   @Override
   protected Map<K, V> executeOperation(Transport transport) {
      HeaderParams params = writeHeader(transport, BULK_GET_REQUEST);
      transport.writeVInt(entryCount);
      transport.flush();
      short status = readHeaderAndValidate(transport, params);
      Map<K, V> result = new HashMap<K, V>();
      while ( transport.readByte() == 1) { //there's more!
         K key = codec.readUnmarshallByteArray(transport, status);
         V value = codec.readUnmarshallByteArray(transport, status);
         result.put(key, value);
      }
      return result;
   }
}
