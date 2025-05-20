package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.dto.OrderDTO;
import _11.asktpk.artisanconnectbackend.dto.OrderStatusDTO;
import _11.asktpk.artisanconnectbackend.service.OrderService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity addClient(@RequestBody OrderDTO orderDTO) {
            return new ResponseEntity<>(orderService.addOrder(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/changeStatus")
    public ResponseEntity changeStatus(@RequestBody OrderStatusDTO orderStatusDTO) {
        return new ResponseEntity<>(orderService.changeOrderStatus(orderStatusDTO.getId(),orderStatusDTO.getStatus()), HttpStatus.OK);
    }
}
