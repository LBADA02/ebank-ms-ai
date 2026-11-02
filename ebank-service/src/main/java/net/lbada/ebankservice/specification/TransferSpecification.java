package net.lbada.ebankservice.specification;

import jakarta.persistence.criteria.Predicate;
import net.lbada.ebankservice.dtos.filters.TransferFilter;
import net.lbada.ebankservice.entities.Transfer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class TransferSpecification {
    public static Specification<Transfer> filter(
            List<String> accountIds,
            TransferFilter filter
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Only transfers sent from customer's accounts
            predicates.add(
                    root.get("sourceAccountId").in(accountIds)
            );

            if (filter.destinationAccountId() != null) {
                predicates.add(cb.equal(
                        root.get("destinationAccountId"),
                        filter.destinationAccountId()
                ));
            }

            if (filter.beneficiaryId() != null) {
                predicates.add(cb.equal(
                        root.get("beneficiaryId"),
                        filter.beneficiaryId()
                ));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(
                        root.get("status"),
                        filter.status()
                ));
            }

            if (filter.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.fromDate()
                ));
            }

            if (filter.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filter.toDate()
                ));
            }

            if (filter.minAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("amount"),
                        filter.minAmount()
                ));
            }

            if (filter.maxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("amount"),
                        filter.maxAmount()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
