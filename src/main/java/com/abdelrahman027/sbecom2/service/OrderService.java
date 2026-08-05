package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.OrderDTO;
import com.abdelrahman027.sbecom2.dto.OrderRequestDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderService {
    OrderDTO placeOrder(String emailId, OrderRequestDTO orderRequestDTO, String paymentMethod);
}
