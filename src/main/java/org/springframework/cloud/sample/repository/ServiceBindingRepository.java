package org.springframework.cloud.sample.repository;

import org.springframework.cloud.sample.model.ServiceBinding;
import org.springframework.data.repository.CrudRepository;

public interface ServiceBindingRepository extends CrudRepository<ServiceBinding, String> {
}
