package org.springframework.cloud.sample.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.sample.model.ServiceBinding;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ServiceBindingRepositoryTests {

    @Autowired
    private ServiceBindingRepository repository;

    @Test
    public void save() {
        ServiceBinding binding = new ServiceBinding("binding-id", "secret");

        ServiceBinding savedBinding = repository.save(binding);

        assertThat(savedBinding).isEqualToComparingFieldByField(binding);
    }

    @Test
    public void retrieve() {
        ServiceBinding binding = new ServiceBinding("binding-id", "secret");

        repository.save(binding);

        Optional<ServiceBinding> foundBinding = repository.findById("binding-id");

        assertThat(foundBinding).isPresent();
        assertThat(foundBinding.orElse(null)).isEqualToComparingFieldByField(binding);
    }
}
