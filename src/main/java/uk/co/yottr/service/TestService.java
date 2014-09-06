package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.TestEntity;
import uk.co.yottr.repository.TestRepository;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 05/09/14.
 */
@Service
@Transactional
public class TestService {
    @Autowired
    private TestRepository testRepository;

    public void save(String text) {
        testRepository.save(new TestEntity(text));
    }

    public TestEntity findByTextLike(String text) {
        return testRepository.findByTextLike(text);
    }
}
