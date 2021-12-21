package com.beshton.shop.exceptions;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException(Long id) {
        super("Could not find sale " + id);
    }
}