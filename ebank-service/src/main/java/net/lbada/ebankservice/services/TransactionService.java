package net.lbada.ebankservice.services;

import net.lbada.ebankservice.entities.Transfer;

public interface TransactionService {
    void recordTransfer(Transfer savedTransfer);
}
