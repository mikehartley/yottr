package uk.co.yottr.tempDatastore;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 26/08/14.
 */
public class Sequence {

    private static long counter = 0;

    public static long next() {
        return counter++;
    }
}
