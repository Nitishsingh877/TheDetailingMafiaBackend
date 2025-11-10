package com.thederailingmafia.carwash.washerservice.repository;

import com.thederailingmafia.carwash.washerservice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
