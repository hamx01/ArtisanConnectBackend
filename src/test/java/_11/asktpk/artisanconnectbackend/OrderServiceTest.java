package _11.asktpk.artisanconnectbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import _11.asktpk.artisanconnectbackend.dto.OrderDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.repository.OrderRepository;
import _11.asktpk.artisanconnectbackend.service.OrderService;
import _11.asktpk.artisanconnectbackend.utils.Enums;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final ClientRepository clientRepository = Mockito.mock(ClientRepository.class);
    private final NoticeRepository noticeRepository = Mockito.mock(NoticeRepository.class);
    private final OrderService orderService = new OrderService(orderRepository, clientRepository, noticeRepository);

    @Test
    @DisplayName("Test dodawania zamówienia")
    public void testAddOrder() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);
        orderDTO.setNoticeId(1L);
        orderDTO.setOrderType(Enums.OrderType.ACTIVATION);

        Client client = new Client();
        client.setId(1L);

        Notice notice = new Notice();
        notice.setIdNotice(1L);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setClient(client);
        savedOrder.setNotice(notice);
        savedOrder.setOrderType(Enums.OrderType.ACTIVATION);
        savedOrder.setStatus(Enums.OrderStatus.PENDING);
        savedOrder.setAmount(10.00);
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Long orderId = orderService.addOrder(orderDTO);

        assertNotNull(orderId, "ID zamówienia nie powinno być null");
        assertEquals(1L, orderId, "ID zamówienia powinno być równe 1");
        verify(orderRepository, times(1)).save(any(Order.class));

        System.out.println("Test dodawania zamówienia przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test zmiany statusu zamówienia")
    public void testChangeOrderStatus() {
        Long orderId = 1L;
        Enums.OrderStatus newStatus = Enums.OrderStatus.COMPLETED;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Enums.OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        Long updatedOrderId = orderService.changeOrderStatus(orderId, newStatus);

        assertNotNull(updatedOrderId, "ID zaktualizowanego zamówienia nie powinno być null");
        assertEquals(orderId, updatedOrderId, "ID zaktualizowanego zamówienia powinno być równe podanemu");
        assertEquals(newStatus, existingOrder.getStatus(), "Status zamówienia powinien zostać zaktualizowany");
        verify(orderRepository, times(1)).save(existingOrder);

        System.out.println("Test zmiany statusu zamówienia przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania zamówienia po ID")
    public void testGetOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order retrievedOrder = orderService.getOrderById(orderId);

        assertNotNull(retrievedOrder, "Pobrane zamówienie nie powinno być null");
        assertEquals(orderId, retrievedOrder.getId(), "ID pobranego zamówienia powinno być równe podanemu");

        System.out.println("Test pobierania zamówienia po ID przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania zamówień po ID klienta")
    public void testGetOrdersByClientId() {
        Long clientId = 1L;
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findByClientId(clientId)).thenReturn(orders);

        List<Order> retrievedOrders = orderService.getOrdersByClientId(clientId);

        assertNotNull(retrievedOrders, "Lista zamówień nie powinna być null");
        assertEquals(2, retrievedOrders.size(), "Lista zamówień powinna zawierać 2 elementy");

        System.out.println("Test pobierania zamówień po ID klienta przeszedł pomyślnie.");
    }
}