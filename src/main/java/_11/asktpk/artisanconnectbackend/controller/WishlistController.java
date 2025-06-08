package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import _11.asktpk.artisanconnectbackend.dto.RequestResponseDTO;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.service.WishlistService;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final ClientService clientService;
    private final NoticeService noticeService;
    private final Tools tools;

    public WishlistController(WishlistService wishlistService, ClientService clientService, NoticeService noticeService, Tools tools) {
        this.wishlistService = wishlistService;
        this.clientService = clientService;
        this.noticeService = noticeService;
        this.tools = tools;
    }

    @PostMapping("/toggle/{noticeId}")
    public ResponseEntity<RequestResponseDTO> toggleWishlist(@PathVariable Long noticeId, HttpServletRequest request) {
        Long clientId = tools.getClientIdFromRequest(request);
        NoticeResponseDTO noticeResponseDTO = noticeService.getNoticeById(noticeId);
        if (noticeResponseDTO == null) {
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
    public List<NoticeResponseDTO> getWishlistForClient(HttpServletRequest request) {
        Long clientId = tools.getClientIdFromRequest(request);
        return wishlistService.getNoticesInWishlist(clientId);
    }
}