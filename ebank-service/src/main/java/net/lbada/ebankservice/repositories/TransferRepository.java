package net.lbada.ebankservice.repositories;

import net.lbada.ebankservice.entities.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer , String>,
        JpaSpecificationExecutor<Transfer> {

    @Override
    @EntityGraph(attributePaths = {
            "sourceAccount",
            "destinationAccount"
    })
    Optional<Transfer> findById(String id);

    @Override
    @EntityGraph(attributePaths = {
            "sourceAccount",
            "destinationAccount"
    })
    Page<Transfer> findAll(
            Specification<Transfer> specification,
            Pageable pageable
    );

    Optional<Transfer>  findByIdempotencyKey(String idEmpotencyKey);
}
