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

package org.springframework.cloud.sample.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sample.model.ServiceBinding;
import org.springframework.cloud.sample.repository.ServiceBindingRepository;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyServiceBrokerBindingTests {

    private static final String SERVICE_INSTANCE_ID = "instance-id";
    private static final String SERVICE_BINDING_ID = "binding-id";

    private static final String EXPECTED_SECRET = "my-secret";
    private static final String FIELD_SECRET = "secret";

    @MockBean
    private ServiceBindingRepository repository;

    @Autowired
    private MyServiceBrokerBinding service;

    @Test
    public void createBindingWhenBindingDoesNotExist() {
        when(repository.findById(SERVICE_BINDING_ID))
                .thenReturn(Optional.empty());

        CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
                .serviceInstanceId(SERVICE_INSTANCE_ID)
                .bindingId(SERVICE_BINDING_ID)
                .build();

        CreateServiceInstanceBindingResponse response = service.createServiceInstanceBinding(request);

        assertThat(response).isInstanceOf(CreateServiceInstanceAppBindingResponse.class);

        CreateServiceInstanceAppBindingResponse appResponse = (CreateServiceInstanceAppBindingResponse) response;
        assertThat(appResponse.isBindingExisted()).isFalse();

        Map<String, Object> credentials = appResponse.getCredentials();
        assertThat(credentials)
                .hasSize(1)
                .containsOnlyKeys(FIELD_SECRET);

        verify(repository).findById(SERVICE_BINDING_ID);

        ArgumentCaptor<ServiceBinding> repositoryCaptor = ArgumentCaptor.forClass(ServiceBinding.class);
        verify(repository).save(repositoryCaptor.capture());
        ServiceBinding actualBinding = repositoryCaptor.getValue();
        assertThat(actualBinding.getServiceBindingId()).isEqualTo(SERVICE_BINDING_ID);
        assertThat(actualBinding.getSecret()).isEqualTo(credentials.get(FIELD_SECRET).toString());

        verifyNoMoreInteractions(repository);
    }


    @Test
    public void createBindingWhenBindingExists() {
        ServiceBinding binding = new ServiceBinding(SERVICE_BINDING_ID, EXPECTED_SECRET);

        when(repository.findById(SERVICE_BINDING_ID))
                .thenReturn(Optional.of(binding));

        CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
                .serviceInstanceId(SERVICE_INSTANCE_ID)
                .bindingId(SERVICE_BINDING_ID)
                .build();

        CreateServiceInstanceBindingResponse response = service.createServiceInstanceBinding(request);

        assertThat(response).isInstanceOf(CreateServiceInstanceAppBindingResponse.class);

        CreateServiceInstanceAppBindingResponse appResponse = (CreateServiceInstanceAppBindingResponse) response;
        assertThat(appResponse.isBindingExisted()).isTrue();

        Map<String, Object> credentials = appResponse.getCredentials();

        assertThat(credentials)
                .hasSize(1)
                .containsOnlyKeys(FIELD_SECRET);

        assertThat(credentials.get(FIELD_SECRET)).isEqualTo(EXPECTED_SECRET);

        verify(repository).findById(SERVICE_BINDING_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void getBindingWhenBindingExists() {
        ServiceBinding serviceBinding = new ServiceBinding(SERVICE_BINDING_ID, EXPECTED_SECRET);

        when(repository.findById(SERVICE_BINDING_ID))
                .thenReturn(Optional.of(serviceBinding));

        GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
                .bindingId(SERVICE_BINDING_ID)
                .build();

        GetServiceInstanceBindingResponse response = service.getServiceInstanceBinding(request);

        assertThat(response).isInstanceOf(GetServiceInstanceAppBindingResponse.class);

        GetServiceInstanceAppBindingResponse appResponse = (GetServiceInstanceAppBindingResponse) response;

        Map<String, Object> credentials = appResponse.getCredentials();

        assertThat(appResponse.getParameters()).isEmpty();
        assertThat(credentials)
                .hasSize(1)
                .containsOnlyKeys(FIELD_SECRET);
        assertThat(credentials.get(FIELD_SECRET)).isEqualTo(EXPECTED_SECRET);

        verify(repository).findById(SERVICE_BINDING_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test(expected = ServiceInstanceBindingDoesNotExistException.class)
    public void getBindingWhenBindingDoesNotExist() {
        when(repository.findById(SERVICE_BINDING_ID))
                .thenReturn(Optional.empty());

        GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
                .bindingId(SERVICE_BINDING_ID)
                .build();

        service.getServiceInstanceBinding(request);
    }

    @Test
    public void deleteBindingWhenBindingExists() {
        when(repository.existsById(SERVICE_BINDING_ID))
                .thenReturn(true);

        DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
                .serviceInstanceId(SERVICE_INSTANCE_ID)
                .bindingId(SERVICE_BINDING_ID)
                .build();

        service.deleteServiceInstanceBinding(request);

        verify(repository).existsById(SERVICE_BINDING_ID);
        verify(repository).deleteById(SERVICE_BINDING_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test(expected = ServiceInstanceBindingDoesNotExistException.class)
    public void deleteBindingWhenBindingDoesNotExist() {
        when(repository.existsById(SERVICE_BINDING_ID))
                .thenReturn(false);

        DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
                .serviceInstanceId(SERVICE_INSTANCE_ID)
                .bindingId(SERVICE_BINDING_ID)
                .build();

        service.deleteServiceInstanceBinding(request);
    }
}