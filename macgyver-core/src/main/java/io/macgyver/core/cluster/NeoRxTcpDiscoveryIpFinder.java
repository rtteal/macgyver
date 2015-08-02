package io.macgyver.core.cluster;

import io.macgyver.neorx.rest.NeoRxClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoRxTcpDiscoveryIpFinder extends TcpDiscoveryIpFinderAdapter {

	Logger logger = LoggerFactory.getLogger(NeoRxTcpDiscoveryIpFinder.class);

	private NeoRxClient neo4j;

	public NeoRxTcpDiscoveryIpFinder(NeoRxClient client) {
		this.neo4j = client;
		setShared(true);
	}

	@Override
	public Collection<InetSocketAddress> getRegisteredAddresses()
			throws IgniteSpiException {
		Collection<InetSocketAddress> addrs = new LinkedList<>();
		if (neo4j.checkConnection()) {
			logger.info("reading from ignite registry");
			neo4j.execCypher("match (r:IgniteRegistry) return r").subscribe(
					x -> {
						InetSocketAddress a = new InetSocketAddress(x.path(
								"host").asText(), x.path("port").asInt());
						addrs.add(a);
					});
		}

		return addrs;

	}

	@Override
	public void registerAddresses(Collection<InetSocketAddress> addrs)
			throws IgniteSpiException {

		if (neo4j.checkConnection()) {
			String cypher = "merge (s:IgniteRegistry {host: {host}, port: {port}}) ON CREATE SET s.updateTs=timestamp() ON MATCH SET s.updateTs=timestamp() return s;";
			for (InetSocketAddress addr : addrs) {
				logger.info("merging to ignite registry: {} {}", addr
						.getAddress().getHostAddress(), addr.getPort());
				neo4j.execCypher(cypher, "host", addr.getAddress()
						.getHostAddress(), "port", addr.getPort());
			}
		}
	}

	@Override
	public void unregisterAddresses(Collection<InetSocketAddress> addrs)
			throws IgniteSpiException {
		if (neo4j.checkConnection()) {
			String cypher = "merge (s:IgniteRegistry {host: {host}, port: {port}}) delete s;";
			for (InetSocketAddress addr : addrs) {
				logger.info("removing from ignite registry: {} {}", addr
						.getAddress().getHostAddress(), addr.getPort());

				neo4j.execCypher(cypher, "host", addr.getAddress()
						.getHostAddress(), "port", addr.getPort());
			}
		}

	}

}
