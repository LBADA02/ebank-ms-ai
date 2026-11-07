package net.lbada.ebankservice.repositories;

import net.lbada.ebankservice.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface BankAccountRepository extends JpaRepository<BankAccount , String> {

    List<BankAccount> findByCustomerId(Long id);

    List<BankAccount> findByIdIn(Set<String> ids);
}
