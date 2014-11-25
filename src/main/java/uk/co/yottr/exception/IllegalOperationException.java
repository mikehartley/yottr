package uk.co.yottr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IllegalOperationException extends javax.servlet.ServletException {
    public IllegalOperationException(String message) {
        super(message);
    }
}
