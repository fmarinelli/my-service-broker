/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sample.configuration;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceCatalogConfiguration {
	@Bean
	public Catalog catalog() {
		Plan plan = Plan.builder()
				.id("b973fb78-82f3-49ef-9b8b-c1876974a6cd")
				.name("Standard")
				.description("A Standard custom service plan")
				.free(true)
				.build();
		
		Plan planPremium = Plan.builder()
				.id("b973fb78-82f3-49ef-9b8b-c1876974a6Fb")
				.name("Premium")
				.description("A Premium custom service plan")
				.free(true)
				.build();		

		ServiceDefinition serviceDefinition = ServiceDefinition.builder()
				.id("bdb1be2e-360b-495c-8115-d7697f9c6a9e")
				.name("custom")
				.description("A simple custom service")
				.bindable(true)
				.tags("custom-service")
				.plans(plan,planPremium)
				.metadata("displayName", "custom")
				.metadata("longDescription", "A simple custom store service")
				.metadata("providerDisplayName", "Acme Custom")
				.build();

		return Catalog.builder()
				.serviceDefinitions(serviceDefinition)
				.build();
	}
}
