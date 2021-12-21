package com.beshton.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;

import java.time.LocalDateTime;


@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository,
                                   SaleRepository saleRepository) {

        return args -> {
            employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
            employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));

            employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));

            orderRepository.save(new CustomerOrder("MacEmployee Pro", Status.COMPLETED));
            orderRepository.save(new CustomerOrder("iPhone", Status.IN_PROGRESS));

            orderRepository.findAll().forEach(order -> {
                log.info("Preloaded " + order);
            });
            saleRepository.save(new Sale("Mixer", "Casey", "Jian", "Kitchen",
                    20L, "61801", "KitchenAid", "A101010", true,
                    "Almost new", "pending", LocalDateTime.now().toString(), 10898L, 1893L));
            saleRepository.findAll().forEach(sale -> {
                log.info("Preloaded " + sale);
            });
        };
    }
}