package uk.co.yottr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 27/08/14.
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends javax.servlet.ServletException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
