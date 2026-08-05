package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
