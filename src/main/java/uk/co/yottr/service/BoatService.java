package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;

import java.util.ArrayList;
import java.util.List;

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
        boat.setMinimumRequiredLevel(level);
        return boatRepository.save(boat);
    }

    @Transactional(readOnly = true)
    public List<Boat> findAll() {

        List<Boat> allBoats = new ArrayList<>();

        for (Boat boat: boatRepository.findAll()) {
            allBoats.add(boat);
        }

        return allBoats;
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
