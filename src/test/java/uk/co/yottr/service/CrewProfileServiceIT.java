package uk.co.yottr.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.form.CrewProfileForm;
import uk.co.yottr.form.CrewSearch;
import uk.co.yottr.model.CrewProfile;
import uk.co.yottr.model.Frequency.FrequencyEnum;
import uk.co.yottr.model.RyaSailCruisingLevel;
import uk.co.yottr.model.SailingRegion;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.CrewProfileRepository;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.*;
import static uk.co.yottr.builder.UserBuilder.aUser;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class CrewProfileServiceIT {

    @Autowired
    private CrewProfileService crewProfileService;

    @Autowired
    private CrewProfileRepository crewProfileRepository;

    @Autowired
    private UserService userService;

    private User dave;
    private User erin;

    @BeforeEach
    public void createMembers() {
        crewProfileRepository.deleteAll();
        dave = userService.save(aUser().withUsername("dave" + secure().nextAlphanumeric(10)).build(), false);
        erin = userService.save(aUser().withUsername("erin" + secure().nextAlphanumeric(10)).build(), false);
    }

    @AfterEach
    public void clearProfiles() {
        crewProfileRepository.deleteAll();
    }

    @Test
    public void aProfileIsMadeThenChanged() {
        crewProfileService.save(dave.getUsername(), form(SailingRegion.WEST_COUNTRY, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.WEEKENDS));
        crewProfileService.save(dave.getUsername(), form(SailingRegion.SOUTH_COAST, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.HOLIDAYS, FrequencyEnum.WEEKENDS));

        final CrewProfile profile = crewProfileService.profileOf(dave.getUsername()).orElseThrow();
        assertEquals(1, crewProfileRepository.count(), "one profile a member");
        assertEquals(SailingRegion.SOUTH_COAST, profile.getRegion());
        assertEquals(Set.of(FrequencyEnum.HOLIDAYS, FrequencyEnum.WEEKENDS), profile.getAvailability());
        assertEquals("Weekends, Holidays", profile.getWhen());
        assertEquals("Day Skipper", profile.getLevelName());
    }

    @Test
    public void ownersFindCrewByWhereTheirLevelAndWhen() {
        crewProfileService.save(dave.getUsername(), form(SailingRegion.WEST_COUNTRY, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.WEEKENDS));
        crewProfileService.save(erin.getUsername(), form(SailingRegion.SOUTH_COAST, RyaSailCruisingLevel.COMPETENT_CREW, FrequencyEnum.HOLIDAYS));

        assertEquals(Set.of(dave.getUsername(), erin.getUsername()), found(s -> {}));
        assertEquals(Set.of(dave.getUsername()), found(s -> s.setRegion(SailingRegion.WEST_COUNTRY)));
        assertEquals(Set.of(dave.getUsername()), found(s -> s.setMinLevel(RyaSailCruisingLevel.DAY_SKIPPER.getRank())));
        assertEquals(Set.of(erin.getUsername()), found(s -> s.setWhen(FrequencyEnum.HOLIDAYS)));
    }

    @Test
    public void aRemovedProfileOrADisabledMemberIsNotFound() {
        crewProfileService.save(dave.getUsername(), form(SailingRegion.WEST_COUNTRY, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.WEEKENDS));
        crewProfileService.save(erin.getUsername(), form(SailingRegion.WEST_COUNTRY, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.WEEKENDS));

        crewProfileService.remove(dave.getUsername());
        userService.flipEnabled(userService.findMember(erin.getUsername()));

        assertTrue(found(s -> {}).isEmpty());
        assertTrue(crewProfileService.profileOf(dave.getUsername()).isEmpty());
    }

    @Test
    public void deletingTheMemberDeletesTheirProfile() {
        crewProfileService.save(dave.getUsername(), form(SailingRegion.WEST_COUNTRY, RyaSailCruisingLevel.DAY_SKIPPER, FrequencyEnum.WEEKENDS));

        userService.delete(userService.findMember(dave.getUsername()));

        assertEquals(0, crewProfileRepository.count());
    }

    private Set<String> found(Consumer<CrewSearch> criteria) {
        final CrewSearch search = new CrewSearch();
        criteria.accept(search);
        return crewProfileService.search(search).stream().map(profile -> profile.getMember().getUsername()).collect(Collectors.toSet());
    }

    private static CrewProfileForm form(SailingRegion region, RyaSailCruisingLevel level, FrequencyEnum... when) {
        final CrewProfileForm form = new CrewProfileForm();
        form.setRegion(region);
        form.setLevelRank(level.getRank());
        form.setAvailability(List.of(when));
        form.setExperience("Three seasons on the south coast.");
        return form;
    }
}
