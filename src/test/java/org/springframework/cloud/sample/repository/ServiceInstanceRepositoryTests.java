package org.springframework.cloud.sample.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.sample.model.ServiceBinding;
import org.springframework.cloud.sample.model.ServiceInstance;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ServiceInstanceRepositoryTests {

    @Autowired
    private ServiceInstanceRepository repository;

    @Test
    public void save() {
        ServiceInstance instance = new ServiceInstance("instance-id", "service-id", "plan-id");

        ServiceInstance savedInstance = repository.save(instance);

        assertThat(savedInstance).isEqualToComparingFieldByField(instance);
    }

    @Test
    public void retrieve() {
        ServiceInstance instance = new ServiceInstance("instance-id", "service-id", "plan-id");

        repository.save(instance);

        Optional<ServiceInstance> foundInstance = repository.findById("instance-id");

        assertThat(foundInstance).isPresent();
        assertThat(foundInstance.orElse(null)).isEqualToComparingFieldByField(instance);
    }
}
