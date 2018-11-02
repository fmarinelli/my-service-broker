package org.springframework.cloud.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sample.model.ServiceInstance;
import org.springframework.cloud.sample.repository.ServiceInstanceRepository;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyServiceBroker implements ServiceInstanceService {

    private final ServiceInstanceRepository repository;

    @Autowired
    public MyServiceBroker(ServiceInstanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        if (repository.existsById(request.getServiceInstanceId())) {
            return CreateServiceInstanceResponse.builder().instanceExisted(true).build();
        }
        ServiceInstance instance = new ServiceInstance(request.getServiceInstanceId(), request.getServiceDefinitionId(), request.getPlanId());
        repository.save(instance);
        return CreateServiceInstanceResponse.builder().build();
    }

    @Override
    public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) {
        return repository.findById(request.getServiceInstanceId())
                .flatMap(s -> Optional.of(GetServiceInstanceResponse.builder().serviceDefinitionId(s.getServiceId()).planId(s.getPlanId()).build()))
                .orElseThrow(() -> new ServiceInstanceDoesNotExistException(request.getServiceInstanceId()));
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        if(repository.existsById(request.getServiceInstanceId())) {
            repository.deleteById(request.getServiceInstanceId());
            return DeleteServiceInstanceResponse.builder().build();
        }
        throw new ServiceInstanceDoesNotExistException(request.getServiceInstanceId());
    }

}
