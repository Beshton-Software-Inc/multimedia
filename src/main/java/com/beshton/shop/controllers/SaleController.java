package com.beshton.shop.controllers;

import com.beshton.shop.entities.*;
import com.beshton.shop.exceptions.SaleNotFoundException;
import com.beshton.shop.repos.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.*;

@RestController
public class SaleController {
    private final SaleRepository repository;

    SaleController(SaleRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/sales")
    CollectionModel<EntityModel<Sale>> all() {

        List<EntityModel<Sale>> sales = repository.findAll().stream()
                .map(sale -> EntityModel.of(sale,
                        linkTo(methodOn(SaleController.class).one(sale.getId())).withSelfRel(),
                        linkTo(methodOn(SaleController.class).all()).withRel("sales")))
                .collect(Collectors.toList());

        return CollectionModel.of(sales, linkTo(methodOn(SaleController.class).all()).withSelfRel());
    }

    @PostMapping("/sales")
    Sale newSale(@RequestBody Sale newSale) {
        return repository.save(newSale);
    }

    @GetMapping("/sales/{id}")
    EntityModel<Sale> one(@PathVariable Long id) {

        Sale sale = repository.findById(id) //
                .orElseThrow(() -> new SaleNotFoundException(id));

        return EntityModel.of(sale, //
                linkTo(methodOn(SaleController.class).one(id)).withSelfRel(),
                linkTo(methodOn(SaleController.class).all()).withRel("sales"));
    }

    @PutMapping("/sales/{id}")
    Sale replaceSale(@RequestBody Sale newSale, @PathVariable Long id) {

        return repository.findById(id)
                .map(sale -> {
                    sale.setItemName(newSale.getItemName());
                    sale.setSellerFirstName(newSale.getSellerFirstName());
                    sale.setSellerLastName(newSale.getSellerLastName());
                    sale.setCategory(newSale.getCategory());
                    sale.setPrice(newSale.getPrice());
                    sale.setPostalCode(newSale.getPostalCode());
                    sale.setManufacturer(newSale.getManufacturer());
                    sale.setModelName(newSale.getModelName());
                    sale.setUsed(newSale.getUsed());
                    sale.setDescription(newSale.getDescription());
                    sale.setSaleStatus(newSale.getSaleStatus());
                    sale.setTimeStamp(newSale.getTimeStamp());
                    sale.setLatitude(newSale.getLatitude());
                    sale.setLongitude(newSale.getLongitude());
                    return repository.save(sale);
                })
                .orElseGet(() -> {
                    newSale.setId(id);
                    return repository.save(newSale);
                });
    }

    @DeleteMapping("/sales/{id}")
    void deleteSale(@PathVariable Long id) {
        repository.deleteById(id);
    }
}


