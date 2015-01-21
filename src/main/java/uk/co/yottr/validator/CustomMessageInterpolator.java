package uk.co.yottr.validator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.Locale;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class CustomMessageInterpolator extends ResourceBundleMessageInterpolator {
    public CustomMessageInterpolator() {
        super();
    }

    public CustomMessageInterpolator(ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
    }

    public CustomMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cacheMessages) {
        super(userResourceBundleLocator, cacheMessages);
    }

    @Override
    public String interpolate(String message, Context context) {
        return super.interpolate(message, context);
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return super.interpolate(message, context, locale);
    }
}
