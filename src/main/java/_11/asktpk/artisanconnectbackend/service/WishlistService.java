package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.WishlistDTO;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Wishlist;
import _11.asktpk.artisanconnectbackend.repository.WishlistRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final NoticeService noticeService;

    public WishlistService(WishlistRepository wishlistRepository, @Lazy NoticeService noticeService) {
        this.wishlistRepository = wishlistRepository;
        this.noticeService = noticeService;
    }

    public List<WishlistDTO> getWishlistForClientId(Long clientId) {
        List<Wishlist> wishlistEntities = wishlistRepository.findAllByClientId(clientId);
        return wishlistEntities.stream()
                .map(this::toDTO)
                .toList();
    }

    public boolean isWishlisted(Client client, Notice notice) {
        Optional<Wishlist> existingEntry = wishlistRepository.findByClientAndNotice(client, notice);

        return existingEntry.isEmpty();
    }

    public boolean toggleWishlist(Client client, Notice notice) {
        Optional<Wishlist> existingEntry = wishlistRepository.findByClientAndNotice(client, notice);

        if (existingEntry.isPresent()) {
            wishlistRepository.delete(existingEntry.get());
            return false;
        } else {
            Wishlist wishlist = new Wishlist();
            wishlist.setClient(client);
            wishlist.setNotice(notice);
            wishlistRepository.save(wishlist);
            return true;
        }
    }

    private WishlistDTO toDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setClientId(wishlist.getClient().getId());
        dto.setNoticeId(wishlist.getNotice().getIdNotice());

        return dto;
    }

    public List<NoticeDTO> getNoticesInWishlist(Long clientId) {
        List<Wishlist> wishlistEntries = wishlistRepository.findAllByClientId(clientId);

        return wishlistEntries.stream()
                .map(wishlist -> noticeService.getNoticeById(wishlist.getNotice().getIdNotice()))
                .collect(Collectors.toList());

    }
}