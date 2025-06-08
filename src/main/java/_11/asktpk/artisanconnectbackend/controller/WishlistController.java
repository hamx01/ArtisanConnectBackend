package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import _11.asktpk.artisanconnectbackend.dto.RequestResponseDTO;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static _11.asktpk.artisanconnectbackend.utils.Tools.getClientIdFromRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final ClientService clientService;
    private final NoticeService noticeService;

    public WishlistController(WishlistService wishlistService, ClientService clientService, NoticeService noticeService) {
        this.wishlistService = wishlistService;
        this.clientService = clientService;
        this.noticeService = noticeService;
    }

    @PostMapping("/toggle/{noticeId}")
    public ResponseEntity<RequestResponseDTO> toggleWishlist(@PathVariable Long noticeId, HttpServletRequest request) {
        Long clientId = getClientIdFromRequest(request);
        NoticeDTO noticeDTO = noticeService.getNoticeById(noticeId);
        if (noticeDTO == null) {
            return ResponseEntity.badRequest().body(new RequestResponseDTO("Notice not found"));
        }
        boolean added = wishlistService.toggleWishlist(
                clientService.getClientById(clientId),
                noticeService.getNoticeByIdEntity(noticeId)
        );

        if (added) {
            return ResponseEntity.ok(new RequestResponseDTO("Wishlist entry added"));
        } else {
            return ResponseEntity.ok(new RequestResponseDTO("Wishlist entry removed"));
        }
    }

//    @GetMapping("/{clientId}")
//    public  ResponseEntity<List<WishlistDTO>>  getWishlist(@PathVariable Long clientId) {
//        List<WishlistDTO> wishlist = wishlistService.getWishlistForClientId(clientId);
//        return ResponseEntity.ok(wishlist);
//    }

    @GetMapping("/")
    public List<NoticeDTO> getWishlistForClient(HttpServletRequest request) {
        Long clientId = getClientIdFromRequest(request);
        return wishlistService.getNoticesInWishlist(clientId);
    }
}