package uk.co.yottr.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.model.Country;
import uk.co.yottr.model.User;
import uk.co.yottr.testconfig.ConstantsForTests;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clearUsersBeforeRunningTests() {
        userRepository.deleteAll();
    }

    @Test
    public void paging() {
        for (int i = 0; i < 10; i++) {
            final User savedUser = userRepository.save(createUser("user" + i));
        }

        final PageRequest pageRequest = PageRequest.of(
                ConstantsForTests.DEFAULT_PAGE_REQUEST_PAGE,
                ConstantsForTests.DEFAULT_PAGE_SIZE,
                Sort.Direction.ASC,
                "username");
        final Page<User> pagedUsers = userRepository.findAll(pageRequest);
        assertEquals(4, pagedUsers.getSize());

        int i = 0;
        for (User user : pagedUsers) {
            String usernameToFind = "user" + i++;
            assertEquals(usernameToFind, user.getUsername(), "unexpected user in sequence");
        }
    }

    private User createUser(String username) {
        User user = new User();

        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("Izzy");
        user.setLastName("Wizzy");
        user.setEmail("izzy@wizzy.test");
        user.setMobile("01234 123456");
        user.setCountry(Country.UK);
        user.setPostcode("W8 4QT");

        return user;
    }
}