package com.beshton.shop.repos;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
}
