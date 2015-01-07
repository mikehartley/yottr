package uk.co.yottr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import uk.co.yottr.builder.BoatBuilder;
import uk.co.yottr.model.Boat;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.BoatRepository;
import uk.co.yottr.repository.RyaSailCruisingLevelRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
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

    @Mock
    private TemporalProvider temporalProvider;

    private User owner;

    @Before
    public void init() {
        reset(boatRepository);
        reset(ryaSailCruisingLevelRepository);
        reset(temporalProvider);

        boatService = new BoatService(boatRepository, ryaSailCruisingLevelRepository, temporalProvider);

        owner = aUser().build();
    }

    @Test
    public void testSave() throws Exception {
        final RyaSailCruisingLevel coastalSkipperLevel = RyaSailCruisingLevel.COASTAL_SKIPPER;
        final LocalDate firstDay2015 = LocalDate.ofYearDay(2015, 1);
        Boat boat = BoatBuilder.aBoat().withOwner(owner)
                .withMinimumRequiredLevel(coastalSkipperLevel)
                .build();
        ArgumentCaptor<Boat> boatCaptor = ArgumentCaptor.forClass(Boat.class);
        when(ryaSailCruisingLevelRepository.findByRank(coastalSkipperLevel.getRank())).thenReturn(coastalSkipperLevel);
        when(temporalProvider.today()).thenReturn(firstDay2015);

        boatService.save(boat);

        verify(ryaSailCruisingLevelRepository).findByRank(coastalSkipperLevel.getRank());
        verify(boatRepository).save(boatCaptor.capture());

        final Boat boatThatWasSaved = boatCaptor.getValue();
        assertEquals(coastalSkipperLevel, boatThatWasSaved.getMinimumRequiredLevel());
        assertEquals(firstDay2015, boatThatWasSaved.getLastUpdated());

        verifyNoMoreInteractions(boatRepository);
        verifyNoMoreInteractions(ryaSailCruisingLevelRepository);
    }

    @Test
    public void testFindAll() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        final PageImpl<Boat> page = new PageImpl<>(new ArrayList<>());
        when(boatRepository.findAll(pageable)).thenReturn(page);

        final Page<Boat> pageFromService = boatService.findAll(pageable);
        assertSame("page from service", page, pageFromService);
    }

    @Test
    public void testDelete() throws Exception {
        Boat boat = BoatBuilder.aBoat().withOwner(owner).build();

        boatService.delete(boat);

        verify(boatRepository).delete(boat);
    }

    @Test
    public void testFindByReference() throws Exception {
        String reference = "xyz";
        Boat boat = BoatBuilder.aBoat().withOwner(owner).build();
        when(boatRepository.findByReference(reference)).thenReturn(boat);

        final Boat boatFromService = boatService.findByReference(reference);

        assertSame("boat from service", boat, boatFromService);
    }

    @Test
    public void testFindAllExcludingSuspended() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        final ArrayList<Boat> boatList = new ArrayList<>();
        final PageImpl<Boat> page = new PageImpl<>(boatList);
        final boolean suspended = false;
        when(boatRepository.findBySuspended(suspended, pageable)).thenReturn(page);

        final Page<Boat> resultFromService = boatService.findAllExcludingSuspended(pageable);

        assertSame("result from service", page, resultFromService);
    }
}