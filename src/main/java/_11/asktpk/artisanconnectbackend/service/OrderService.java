package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.OrderDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.repository.OrderRepository;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import _11.asktpk.artisanconnectbackend.entities.Order;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final NoticeRepository noticeRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository, NoticeRepository noticeRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.noticeRepository = noticeRepository;
    }

    public Order fromDTO(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderType(orderDTO.getOrderType());
        order.setStatus(Enums.OrderStatus.PENDING);
        if(orderDTO.getOrderType() == Enums.OrderType.ACTIVATION){
            order.setAmount(10.00);
        }else{
            order.setAmount(8.00);
        }

        order.setCreatedAt(LocalDateTime.now()
        );
        order.setUpdatedAt(LocalDateTime.now()
        );

        Client client = clientRepository.findById(orderDTO.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID: " + orderDTO.getClientId()));
        order.setClient(client);

        Notice notice = noticeRepository.findById(orderDTO.getNoticeId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + orderDTO.getNoticeId()));
        order.setNotice(notice);

        return order;
    }


    public Long addOrder(OrderDTO orderDTO) {
        return orderRepository.save(fromDTO(orderDTO)).getId();
    }

    public Long changeOrderStatus(Long id, Enums.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zamówienia o ID: " + id));

        order.setStatus(status);

        order = orderRepository.save(order);

        return order.getId();

    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zamówienia o ID: " + id));
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }
}
