/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The component that manage the communication with the Rabbit MQ broker.
 *
 * @see ListenerService
 * @see PublishService
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class RabbitMQService {

	/**
	 * The event bus used on the platform.
	 */
	@Inject
	Vertx vertx;

	/**
	 * The client to Rabbit MQ.
	 */
	private RabbitMQClient client;

	/**
	 * The username used to authenticate to the Rabbit MQ broker.
	 */
	@ConfigProperty(name = "rabbitmq-username", defaultValue = "mov")
	String username;

	/**
	 * The password used to authenticate to the Rabbit MQ broker
	 */
	@ConfigProperty(name = "rabbitmq-password", defaultValue = "password")
	String password;

	/**
	 * The Rabbit MQ broker hostname.
	 */
	@ConfigProperty(name = "rabbitmq-host", defaultValue = "mov-mq")
	String host;

	/**
	 * The Rabbit MQ broker port.
	 */
	@ConfigProperty(name = "rabbitmq-port", defaultValue = "5672")
	int port;

//	/**
//	 * The multiple addresses for cluster mode, when given overrides the host and
//	 * port.
//	 */
//	@ConfigProperty(name = "rabbitmq-addresses", defaultValue = "")
//	String addresses;

	/**
	 * Whether or not the connection should use SSL.
	 */
	@ConfigProperty(name = "rabbitmq-ssl", defaultValue = "false")
	boolean ssl;

	/**
	 * Whether to skip trust certificate verification.
	 */
	@ConfigProperty(name = "rabbitmq-trust-all", defaultValue = "false")
	boolean trustAll;

//	/**
//	 * The path to a JKS trust store.
//	 */
//	@ConfigProperty(name = "rabbitmq-trust-store-path")
//	String trustStorePath;
//
//	/**
//	 * The password of the JKS trust store.
//	 */
//	@ConfigProperty(name = "rabbitmq-trust-store-password",defaultValue="false")
//	String trustStorePassword;

	/**
	 * The TCP connection timeout (ms); 0 is interpreted as no timeout.
	 */
	@ConfigProperty(name = "connection-timeout", defaultValue = "60000")
	int connectionTimeout;

	/**
	 * The AMQP 0-9-1 protocol handshake timeout (ms).
	 */
	@ConfigProperty(name = "handshake-timeout", defaultValue = "10000")
	int handshakeTimeout;

	/**
	 * Whether automatic connection recovery is enabled.
	 */
	@ConfigProperty(name = "automatic-recovery-enabled", defaultValue = "false")
	boolean automaticRecoveryEnabled;

	/**
	 * Whether automatic recovery on initial connections is enabled.
	 */
	@ConfigProperty(name = "automatic-recovery-on-initial-connection", defaultValue = "true")
	boolean automaticRecoveryOnInitialConnection;

	/**
	 * The number of reconnection attempts.
	 */
	@ConfigProperty(name = "rabbitmq-reconnect-attempts", defaultValue = "100")
	int reconnectAttempts;

	/**
	 * The interval (in seconds) between two reconnection attempts.
	 */
	@ConfigProperty(name = "rabbitmq-reconnect-interval", defaultValue = "10")
	int reconnectInterval;

	/**
	 * How long (ms) will automatic recovery wait before attempting to reconnect.
	 */
	@ConfigProperty(name = "network-recovery-interval", defaultValue = "5000")
	int networkRecoveryInterval;

//	/**
//	 * The user name to use when connecting to the broker.
//	 */
//	@ConfigProperty(name = "user", defaultValue = "guest")
//	String user;

	/**
	 * Whether to include properties when a broker message is passed on the event
	 * bus.
	 */
	@ConfigProperty(name = "include-properties", defaultValue = "false")
	boolean includeProperties;

	/**
	 * The initially requested maximum channel number.
	 */
	@ConfigProperty(name = "requested-channel-max", defaultValue = "2047")
	int requestedChannelMax;

	/**
	 * The initially requested heartbeat interval (seconds), zero for none.
	 */
	@ConfigProperty(name = "requested-heartbeat", defaultValue = "60")
	int requestedHeartbeat;

	/**
	 * Whether usage of NIO Sockets is enabled.
	 */
	@ConfigProperty(name = "use-nio", defaultValue = "false")
	boolean useNio;

	/**
	 * The virtual host to use when connecting to the broker.
	 */
	@ConfigProperty(name = "rabbitmq-virtual-host", defaultValue = "/")
	String virtualHost;

//	/**
//	 * The name of the RabbitMQ Client Option bean used to customize the RabbitMQ
//	 * client configuration.
//	 */
//	@ConfigProperty(name = "rabbitmq-client-options-name")
//	String clienOptionsName;
//
//	/**
//	 * The name of the RabbitMQ Credentials Provider bean used to provide dynamic
//	 * credentials to the RabbitMQ client.
//	 */
//	@ConfigProperty(name = "rabbitmq-credentials-provider-name")
//	String credentialsProviderName;

	/**
	 * Create the client to the rabbit MQ broker.
	 */
	@Startup
	public void createClient() {

		final var config = new RabbitMQOptions();
		config.setUser(this.username);
		config.setPassword(this.password);
		config.setHost(this.host);
		config.setPort(this.port);
//		if (this.addresses != null && this.addresses.trim().length() > 0) {
//
//			final var addressesArray = Address.parseAddresses(this.addresses);
//			config.setAddresses(Arrays.asList(addressesArray));
//		}
		config.setSsl(this.ssl);
		config.setTrustAll(this.trustAll);
		config.setConnectionTimeout(this.connectionTimeout);
		config.setHandshakeTimeout(this.handshakeTimeout);
		config.setAutomaticRecoveryEnabled(this.automaticRecoveryEnabled);
		config.setAutomaticRecoveryOnInitialConnection(this.automaticRecoveryOnInitialConnection);
		config.setReconnectAttempts(this.reconnectAttempts);
		config.setReconnectInterval(this.reconnectInterval);
		config.setNetworkRecoveryInterval(this.networkRecoveryInterval);
		config.setIncludeProperties(this.includeProperties);
		config.setRequestedChannelMax(this.requestedChannelMax);
		config.setRequestedHeartbeat(this.requestedHeartbeat);
		config.setUseNio(this.useNio);
		config.setVirtualHost(this.virtualHost);
		config.setConnectionName(this.getClass().getName());

		this.client = RabbitMQClient.create(this.vertx, config);
		this.client.start().subscribe().with(any -> {
			Log.infov("Started Rabbit MQ connection with {0}", config);
		}, error -> {

			Log.errorv(error, "Cannot start the connection with the Rabbit MQ.");
			this.destroyClient();
		});

	}

	/**
	 * Destroy the client to the rabbit MQ broker.
	 */
	@Shutdown
	public void destroyClient() {

		if (this.client != null) {

			this.client.stop().subscribe().with(any -> Log.infov("Closed connection with the Rabbit MQ."), error -> {

				Log.errorv(error, "Cannot start the connection with the Rabbit MQ.");
				this.destroyClient();
			});
			this.client = null;
		}
	}

	/**
	 * Return the client to the Rabbit MQ broker.
	 *
	 * @return the client to the Rabbit MQ broker.
	 */
	public Uni<RabbitMQClient> client() {

		if (this.client == null) {

			return Uni.createFrom()
					.failure(() -> new IllegalStateException("Not opened connection to the Rabbit MQ broker."));

		} else {

			return Uni.createFrom().item(this.client);
		}
	}

}
