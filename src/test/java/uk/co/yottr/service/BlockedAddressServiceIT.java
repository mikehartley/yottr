package uk.co.yottr.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.model.BlockedAddress;
import uk.co.yottr.testconfig.TestcontainersConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfig.class)
public class BlockedAddressServiceIT {

    private static final String OWN = "192.0.2.10";

    @Autowired
    private BlockedAddressService blockedAddressService;

    @AfterEach
    public void unblockEverything() {
        blockedAddressService.all().forEach(entry -> blockedAddressService.unblock(entry.getId()));
    }

    @Test
    public void anAddressOrARangeIsBlockedUntilUnblocked() {
        blockedAddressService.block(" 203.0.113.0/24 ", "spam listings", OWN);
        blockedAddressService.block("198.51.100.7", null, OWN);

        assertTrue(blockedAddressService.isBlocked("203.0.113.200"));
        assertTrue(blockedAddressService.isBlocked("198.51.100.7"));
        assertFalse(blockedAddressService.isBlocked("198.51.100.8"));
        assertFalse(blockedAddressService.isBlocked("2001:db8::1"), "an IPv6 address isn't in an IPv4 range");

        final List<BlockedAddress> blocked = blockedAddressService.all();
        assertEquals(List.of("198.51.100.7", "203.0.113.0/24"), blocked.stream().map(BlockedAddress::getAddress).toList());
        assertEquals("spam listings", blocked.get(1).getNote());

        blockedAddressService.unblock(blocked.get(1).getId());
        assertFalse(blockedAddressService.isBlocked("203.0.113.200"));
    }

    @Test
    public void onlyAddressesAndRangesCanBeBlocked() {
        assertThrows(IllegalOperationException.class, () -> blockedAddressService.block("example.com", null, OWN));
        assertThrows(IllegalOperationException.class, () -> blockedAddressService.block("203.0.113.0/99", null, OWN));
        assertThrows(IllegalOperationException.class, () -> blockedAddressService.block("", null, OWN));
        assertTrue(blockedAddressService.all().isEmpty());
    }

    @Test
    public void anAddressIsBlockedOnce() {
        blockedAddressService.block("203.0.113.5", null, OWN);
        assertThrows(IllegalOperationException.class, () -> blockedAddressService.block("203.0.113.5", null, OWN));
    }

    @Test
    public void anAdministratorCantBlockThemself() {
        final IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> blockedAddressService.block("192.0.2.0/24", null, OWN));
        assertTrue(e.getMessage().contains("your own address"), e.getMessage());
        assertFalse(blockedAddressService.isBlocked(OWN));
    }
}
