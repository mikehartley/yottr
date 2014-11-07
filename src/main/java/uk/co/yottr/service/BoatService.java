package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Service
public class BoatService {

    private BoatRepository boatRepository;
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Autowired
    public BoatService(BoatRepository boatRepository, RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository) {
        this.boatRepository = boatRepository;
        this.ryaSailCruisingLevelRepository = ryaSailCruisingLevelRepository;
    }

    @Transactional
    public Boat save(Boat boat) {
        final RyaSailCruisingLevel level = ryaSailCruisingLevelRepository.findByRank(boat.getMinimumRequiredLevelByRank());
        boat.setMinimumRequiredLevel(level); // this needs to be non-transient, hence the look-up
        return boatRepository.save(boat);
    }

    @Transactional(readOnly = true)
    public Page<Boat> findAll(Pageable pageable) {
        return boatRepository.findAll(pageable);
    }

    @Transactional
    public void delete(Boat boat) {
        boatRepository.delete(boat);
    }

    @Transactional(readOnly = true)
    public Boat findByReference(String reference) {
        return boatRepository.findByReference(reference);
    }
}
