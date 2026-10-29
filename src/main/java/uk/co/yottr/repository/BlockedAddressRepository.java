package uk.co.yottr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.yottr.model.BlockedAddress;

import java.util.List;

public interface BlockedAddressRepository extends JpaRepository<BlockedAddress, Long> {

    List<BlockedAddress> findAllByOrderByAddress();

    boolean existsByAddress(String address);
}
