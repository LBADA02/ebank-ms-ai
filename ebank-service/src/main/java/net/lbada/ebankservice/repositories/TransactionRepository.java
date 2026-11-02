package net.lbada.ebankservice.repositories;

import net.lbada.ebankservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction ,String > {
}
