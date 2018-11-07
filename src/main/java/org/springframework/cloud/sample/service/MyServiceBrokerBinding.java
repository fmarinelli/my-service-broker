package org.springframework.cloud.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sample.model.ServiceBinding;
import org.springframework.cloud.sample.repository.ServiceBindingRepository;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MyServiceBrokerBinding implements ServiceInstanceBindingService {

    @Autowired
    private ServiceBindingRepository repository;

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        CreateServiceInstanceAppBindingResponse response = repository.findById(request.getBindingId())
                .flatMap(b -> Optional.of(CreateServiceInstanceAppBindingResponse.builder()
                        .credentials("secret", b.getSecret())
                        .bindingExisted(true)
                        .build()))
                .orElseGet(() -> CreateServiceInstanceAppBindingResponse.builder()
                        .credentials("secret", UUID.randomUUID().toString())
                        .bindingExisted(false)
                        .build());
        if(!response.isBindingExisted()) {
            repository.save(new ServiceBinding(request.getBindingId(), response.getCredentials().get("secret").toString()));
        }
        return response;
    }

    @Override
    public GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
        return repository.findById(request.getBindingId())
                .flatMap(b -> Optional.of(GetServiceInstanceAppBindingResponse.builder()
                        .credentials("secret", b.getSecret())
                        .build()))
                .orElseThrow(() -> new ServiceInstanceBindingDoesNotExistException(request.getBindingId()));
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        if(!repository.existsById(request.getBindingId())) {
            throw new ServiceInstanceBindingDoesNotExistException(request.getServiceInstanceId());
        }
        repository.deleteById(request.getBindingId());
    }
}
