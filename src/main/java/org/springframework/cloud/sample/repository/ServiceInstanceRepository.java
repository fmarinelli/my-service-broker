package org.springframework.cloud.sample.repository;

import org.springframework.cloud.sample.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;

public interface ServiceInstanceRepository extends CrudRepository<ServiceInstance, String> {
}
