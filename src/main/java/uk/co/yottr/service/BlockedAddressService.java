package uk.co.yottr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.yottr.exception.IllegalOperationException;
import uk.co.yottr.model.BlockedAddress;
import uk.co.yottr.repository.BlockedAddressRepository;

import java.util.List;

/**
 * The addresses blocked from the site (#36). Every request is checked (BlockedAddressFilter), so the list is held in
 * memory and reloaded when an administrator changes it.
 *
 * Blocking here is for the odd persistent nuisance. Floods are better turned away in front of the site, by the
 * reverse proxy or fail2ban. Behind a proxy, the address is the client's only if server.forward-headers-strategy is
 * set; otherwise every request comes from the proxy.
 */
@Service
public class BlockedAddressService {

    private static final Logger LOG = LoggerFactory.getLogger(BlockedAddressService.class);

    private BlockedAddressRepository blockedAddressRepository;
    private TemporalProvider temporalProvider;

    private volatile List<IpAddressMatcher> blocked = List.of();

    @Autowired
    public BlockedAddressService(BlockedAddressRepository blockedAddressRepository, TemporalProvider temporalProvider) {
        this.blockedAddressRepository = blockedAddressRepository;
        this.temporalProvider = temporalProvider;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void load() {
        blocked = blockedAddressRepository.findAll().stream().map(address -> new IpAddressMatcher(address.getAddress())).toList();
    }

    public boolean isBlocked(String address) {
        final List<IpAddressMatcher> matchers = blocked;
        if (matchers.isEmpty() || address == null) {
            return false;
        }
        try {
            return matchers.stream().anyMatch(matcher -> matcher.matches(address));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<BlockedAddress> all() {
        return blockedAddressRepository.findAllByOrderByAddress();
    }

    /**
     * Blocks an address or range, unless it would block the administrator doing it.
     *
     * @throws IllegalOperationException if it isn't an address or range, is already blocked, or covers ownAddress
     */
    @Transactional
    public void block(String address, String note, String ownAddress) {
        final String trimmed = address == null ? "" : address.strip();
        final IpAddressMatcher matcher;
        try {
            matcher = new IpAddressMatcher(trimmed);
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException("That isn't an IP address, or a range such as 203.0.113.0/24.");
        }
        if (trimmed.isEmpty() || blockedAddressRepository.existsByAddress(trimmed)) {
            throw new IllegalOperationException(trimmed.isEmpty() ? "Give an address to block." : trimmed + " is already blocked.");
        }
        if (matches(matcher, ownAddress)) {
            throw new IllegalOperationException(trimmed + " covers your own address, " + ownAddress + ", so you'd be blocked too.");
        }
        final String trimmedNote = note == null || note.isBlank() ? null : note.strip();
        blockedAddressRepository.save(new BlockedAddress(trimmed, trimmedNote, temporalProvider.today()));
        LOG.info("blocked {} ({})", trimmed, trimmedNote);
        load();
    }

    @Transactional
    public void unblock(long id) {
        blockedAddressRepository.findById(id).ifPresent(address -> {
            blockedAddressRepository.delete(address);
            LOG.info("unblocked {}", address.getAddress());
        });
        blockedAddressRepository.flush();
        load();
    }

    private static boolean matches(IpAddressMatcher matcher, String address) {
        try {
            return address != null && matcher.matches(address);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
