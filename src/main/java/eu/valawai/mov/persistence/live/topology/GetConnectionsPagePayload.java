/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;

import eu.valawai.mov.events.topology.ConnectionsPagePayload;
import eu.valawai.mov.events.topology.QueryConnectionsPayload;
import eu.valawai.mov.persistence.AbstractGetMinPage;
import eu.valawai.mov.persistence.Queries;
import io.smallrye.mutiny.Uni;

/**
 * Operator to obtain the {@link ConnectionsPagePayload}.
 *
 * @see ConnectionsPagePayload
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class GetConnectionsPagePayload extends AbstractGetMinPage<ConnectionsPagePayload, GetConnectionsPagePayload> {

	/**
	 * The identifier of the query.
	 */
	protected String queryId;

	/**
	 * The identifier of the source component.
	 */
	protected String sourceComponentId;

	/**
	 * The channel name of the source component.
	 */
	protected String sourceChannelName;

	/**
	 * The identifier of the target component.
	 */
	protected String targetComponentId;

	/**
	 * The channel name of the target component.
	 */
	protected String targetChannelName;

	/**
	 * Create the operator.
	 */
	private GetConnectionsPagePayload() {

		super("connections");
	}

	/**
	 * Create the operator to get the page.
	 *
	 * @return the operator to obtain the connections page.
	 */
	public static GetConnectionsPagePayload fresh() {

		return new GetConnectionsPagePayload();
	}

	/**
	 * The name of the source channel of the connection.
	 *
	 * @param name of the source channel.
	 *
	 * @return this operator.
	 */
	public GetConnectionsPagePayload withSourceChannelName(final String name) {

		this.sourceChannelName = name;
		return this.operator();
	}

	/**
	 * The identifier of the source component of the connection.
	 *
	 * @param id of the source component.
	 *
	 * @return this operator.
	 */
	public GetConnectionsPagePayload withSourceComponentId(final String id) {

		this.sourceComponentId = id;
		return this.operator();
	}

	/**
	 * The name of the target channel of the connection.
	 *
	 * @param name of the target channel.
	 *
	 * @return this operator.
	 */
	public GetConnectionsPagePayload withTargetChannelName(final String name) {

		this.targetChannelName = name;
		return this.operator();
	}

	/**
	 * The identifier of the target component of the connection.
	 *
	 * @param id of the target component.
	 *
	 * @return this operator.
	 */
	public GetConnectionsPagePayload withTargetComponentId(final String id) {

		this.targetComponentId = id;
		return this.operator();
	}

	/**
	 * Specify the query to do.
	 *
	 * @param query to do.
	 *
	 * @return this operator.
	 */
	public GetConnectionsPagePayload withQuery(QueryConnectionsPayload query) {

		if (query != null) {

			this.queryId = query.id;
			this.withSourceChannelName(query.sourceChannelName);
			this.withSourceComponentId(query.sourceComponentId);
			this.withTargetChannelName(query.targetChannelName);
			this.withTargetComponentId(query.targetComponentId);
			this.withOrder(query.order);
			this.withOffset(query.offset);
			this.withLimit(query.limit);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<ConnectionsPagePayload> getPageWith(List<Bson> pipeline) {

		if (this.sourceComponentId != null || this.targetComponentId != null || this.queryId != null) {

			pipeline = new ArrayList<>(pipeline);
			if (this.sourceComponentId != null) {

				pipeline.add(0, Aggregates
						.set(new Field<>("sourceComponentId", new Document("$toString", "$source.componentId"))));
			}
			if (this.targetComponentId != null) {

				pipeline.add(0, Aggregates
						.set(new Field<>("targetComponentId", new Document("$toString", "$target.componentId"))));
			}

			if (this.queryId != null) {

				pipeline.add(Aggregates.addFields(new Field<>("queryId", this.queryId)));
			}
		}

		return TopologyConnectionEntity.mongoCollection().aggregate(pipeline, ConnectionsPagePayload.class).collect()
				.first();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		final var filters = new ArrayList<Bson>();
		if (this.sourceChannelName != null) {

			final var filter = Queries.filterByValueOrRegexp("source.channelName", this.sourceChannelName);
			filters.add(filter);
		}
		if (this.sourceComponentId != null) {

			final var filter = Queries.filterByValueOrRegexp("sourceComponentId", this.sourceComponentId);
			filters.add(filter);
		}
		if (this.targetChannelName != null) {

			final var filter = Queries.filterByValueOrRegexp("target.channelName", this.targetChannelName);
			filters.add(filter);
		}
		if (this.targetComponentId != null) {

			final var filter = Queries.filterByValueOrRegexp("targetComponentId", this.targetComponentId);
			filters.add(filter);
		}

		if (filters.isEmpty()) {

			return null;

		} else if (filters.size() == 1) {

			return filters.get(0);

		} else {

			return Filters.and(filters);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Bson> createPipelineBeforeFacet() {

		final var pipeline = super.createPipelineBeforeFacet();
		pipeline.add(Aggregates.unwind("$notifications", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		pipeline.add(Aggregates.project(Projections.fields(
				Projections.include("_id", "createTimestamp", "updateTimestamp", "source", "target", "enabled"),
				Projections.computed("converterJSCode", "$targetMessageConverterJSCode"),
				Projections.computed("notification",
						new Document("target",
								new Document("componentId", "$notifications.node.componentId").append("channelName",
										"$notifications.node.channelName"))
								.append("enabled", "$notifications.enabled")
								.append("converterJSCode", "$notifications.notificationMessageConverterJSCode")))));

		pipeline.add(Aggregates.group("$_id",
				new BsonField("createTimestamp", new Document("$first", "$createTimestamp")),
				new BsonField("updateTimestamp", new Document("$first", "$updateTimestamp")),
				new BsonField("source", new Document("$first", "$source")),
				new BsonField("target", new Document("$first", "$target")),
				new BsonField("enabled", new Document("$first", "$enabled")),
				new BsonField("converterJSCode", new Document("$first", "$converterJSCode")),
				new BsonField("notifications",
						new Document("$push", new Document("$cond", new Document("if", new Document("$eq",
								Arrays.asList(new Document("$ifNull",
										Arrays.asList("$notification.target.componentId", null)), null)))
								.append("then", "$$REMOVE").append("else", "$notification"))))));

		pipeline.add(Aggregates.set(new Field<>("notifications",
				new Document("$cond",
						new Document("if", new Document("$eq", Arrays.asList("$notifications", Collections.EMPTY_LIST)))
								.append("then", null).append("else", "$notifications")))));

		return pipeline;
	}

}
