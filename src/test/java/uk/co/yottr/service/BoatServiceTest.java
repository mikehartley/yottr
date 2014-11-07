package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import uk.co.yottr.builder.BoatBuilder;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class BoatServiceTest {

    private BoatService boatService;

    @Mock
    private BoatRepository boatRepository;

    @Mock
    private RyaSailCruisingLevelRepository ryaSailCruisingLevelRepository;

    @Before
    public void init() {
        reset(boatRepository);
        reset(ryaSailCruisingLevelRepository);

        boatService = new BoatService(boatRepository, ryaSailCruisingLevelRepository);
    }

    @Test
    public void testSave() throws Exception {
        final RyaSailCruisingLevel cruisingLevel = RyaSailCruisingLevel.COASTAL_SKIPPER;
        Boat boat = BoatBuilder.aBoat().withMinimumRequiredLevel(cruisingLevel).build();
        when(ryaSailCruisingLevelRepository.findByRank(cruisingLevel.getRank())).thenReturn(cruisingLevel);

        boatService.save(boat);

        verify(ryaSailCruisingLevelRepository).findByRank(cruisingLevel.getRank());
        verify(boatRepository).save(boat);

        verifyNoMoreInteractions(boatRepository);
        verifyNoMoreInteractions(ryaSailCruisingLevelRepository);
    }

    @Test
    public void testFindAll() throws Exception {
        Pageable pageable = mock(Pageable.class);
        final PageImpl<Boat> page = new PageImpl<>(new ArrayList<>());
        when(boatRepository.findAll(pageable)).thenReturn(page);

        final Page<Boat> pageFromService = boatService.findAll(pageable);
        assertEquals("page from service", page, pageFromService);
    }

    @Test
    public void testDelete() throws Exception {
        Boat boat = BoatBuilder.aBoat().build();

        boatService.delete(boat);

        verify(boatRepository).delete(boat);
    }

    @Test
    public void testFindByReference() throws Exception {
        String reference = "xyz";
        Boat boat = BoatBuilder.aBoat().build();
        when(boatRepository.findByReference(reference)).thenReturn(boat);

        final Boat boatFromService = boatService.findByReference(reference);

        assertEquals("boat from service", boat, boatFromService);
    }
}