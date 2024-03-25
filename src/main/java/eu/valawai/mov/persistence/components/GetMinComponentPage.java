/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.MinComponentPage;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a page with some components.
 *
 * @see MinComponentPage
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public class GetMinComponentPage extends AbstractGetPageComponents<MinComponentPage, GetMinComponentPage> {

	/**
	 * It is {@code true} if the component must have at least one publish channel.
	 */
	protected boolean atLeastOnePublishChannel;

	/**
	 * It is {@code true} if the component must have at least one subscribe channel.
	 */
	protected boolean atLeastOneSubscribeChannel;

	/**
	 * Create a new operation.
	 */
	private GetMinComponentPage() {

		super("components");
	}

	/**
	 * Create the operation to obtain some components.
	 *
	 * @return the new get page operation.
	 */
	public static GetMinComponentPage fresh() {

		return new GetMinComponentPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<MinComponentPage> getPageWith(List<Bson> pipeline) {

		return ComponentEntity.mongoCollection().aggregate(pipeline, MinComponentPage.class).collect().first()
				.onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some components");
					final var page = new MinComponentPage();
					page.offset = this.offset;
					return page;
				});
	}

	/**
	 * Mark if the components to return must have at least one publish channel.
	 *
	 * @param atLeastOnePublishChannel is {@code true} if the component must have at
	 *                                 least one publish channel.
	 *
	 * @return this operator.
	 */
	public GetMinComponentPage withAtLeastOnePublishChannel(boolean atLeastOnePublishChannel) {

		this.atLeastOnePublishChannel = atLeastOnePublishChannel;
		return this;
	}

	/**
	 * Mark if the components to return must have at least one subscribe channel.
	 *
	 * @param atLeastOneSubscribeChannel is {@code true} if the component must have
	 *                                   at least one subscribe channel.
	 *
	 * @return this operator.
	 */
	public GetMinComponentPage withAtLeastOneSubscribeChannel(boolean atLeastOneSubscribeChannel) {

		this.atLeastOneSubscribeChannel = atLeastOneSubscribeChannel;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Bson> createComponentsFilters() {

		final var filters = super.createComponentsFilters();
		if (this.atLeastOnePublishChannel) {

			filters.add(Filters.and(Filters.exists("channels.publish", true), Filters.ne("channels.publish", null)));
		}
		if (this.atLeastOneSubscribeChannel) {

			filters.add(
					Filters.and(Filters.exists("channels.subscribe", true), Filters.ne("channels.subscribe", null)));
		}

		return filters;
	}
}
