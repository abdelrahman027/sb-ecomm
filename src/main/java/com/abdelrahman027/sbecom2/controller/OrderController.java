package com.abdelrahman027.sbecom2.controller;


import com.abdelrahman027.sbecom2.dto.OrderDTO;
import com.abdelrahman027.sbecom2.dto.OrderRequestDTO;
import com.abdelrahman027.sbecom2.service.OrderService;
import com.abdelrahman027.sbecom2.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtils authUtils;


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtils.loggedInEmail();
        OrderDTO createdOrder = orderService.placeOrder(
                emailId,
                orderRequestDTO,
                paymentMethod
        );
        return new ResponseEntity<OrderDTO>(createdOrder, HttpStatus.CREATED);
    }
}
