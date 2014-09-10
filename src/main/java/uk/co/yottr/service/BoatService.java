package uk.co.yottr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.model.Boat;
import uk.co.yottr.repository.BoatRepository;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Service
public class BoatService {

    private BoatRepository boatRepository;

    @Autowired
    public BoatService(BoatRepository boatRepository) {
        this.boatRepository = boatRepository;
    }

    @Transactional
    public Boat save(Boat boat) {
        return boatRepository.save(boat);
    }

    public List<Boat> findAll() {

        List<Boat> allBoats = new ArrayList<>();

        for (Boat boat: boatRepository.findAll()) {
            allBoats.add(boat);
        }

        return allBoats;
    }

    public void delete(Boat boat) {
        boatRepository.delete(boat);
    }
}
