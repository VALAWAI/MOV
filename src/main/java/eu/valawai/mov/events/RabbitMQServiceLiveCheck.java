/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The component that check is the {@link RabbitMQService} is live.
 *
 * @see RabbitMQService
 *
 * @author VALAWAI
 */
@ApplicationScoped
@Liveness
public class RabbitMQServiceLiveCheck implements HealthCheck {

	/**
	 * The event bus used on the platform.
	 */
	@Inject
	RabbitMQService service;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HealthCheckResponse call() {

		if (this.service.isLive()) {

			return HealthCheckResponse.up("RabbitMQ service");

		} else {

			return HealthCheckResponse.down("RabbitMQ service");
		}
	}

}
