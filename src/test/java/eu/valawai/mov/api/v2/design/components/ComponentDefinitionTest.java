/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v1.components.ChannelSchemaTest;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;

/**
 * Test the {@link ComponentDefinition}.
 *
 * @see ComponentDefinition
 *
 * @author VALAWAI
 */
public class ComponentDefinitionTest extends ModelTestCase<ComponentDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentDefinition createEmptyModel() {

		return new ComponentDefinition();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ComponentDefinition model) {

		model.id = ValueGenerator.nextObjectId();
		model.name = ValueGenerator.nextPattern("Component name {0}");
		model.description = ValueGenerator.nextPattern("Component description {0}");
		model.type = ValueGenerator.next(ComponentType.values());
		model.docsLink = ValueGenerator.nextPattern("https://valawai.github.io/docs/components/{0}/");
		model.gitHubLink = ValueGenerator.nextPattern("https://github.com/VALAWAI/{0}");
		model.version = new VersionInfoTest().nextModel();
		model.apiVersion = new VersionInfoTest().nextModel();
		final var max = ValueGenerator.rnd().nextInt(0, 5);
		if (max > 0) {

			model.channels = new ArrayList<>();
			final var builder = new ChannelSchemaTest();
			for (var i = 0; i < max; i++) {

				final var channel = builder.nextModel();
				channel.name = model.name + nextPattern("/action_{0}");
				model.channels.add(channel);
			}

		}

	}

	/**
	 * Obtain the model from an entity.
	 *
	 * @param entity to get the data.
	 *
	 * @return the modle with the data of the model.
	 */
	public static ComponentDefinition from(ComponentDefinitionEntity entity) {

		ComponentDefinition model = null;
		if (entity != null) {

			model = new ComponentDefinition();
			model.id = entity.id;
			model.name = entity.name;
			model.description = entity.description;
			model.type = entity.type;
			model.docsLink = entity.docsLink;
			model.gitHubLink = entity.repository != null ? entity.repository.html_url : null;
			model.version = entity.version;
			model.apiVersion = entity.apiVersion;
			if (entity.channels != null) {

				model.channels = new ArrayList<>(entity.channels);
			}
		}
		return model;
	}

}
