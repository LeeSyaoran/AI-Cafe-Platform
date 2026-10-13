package com.example.backend.mapper;

import com.example.backend.dto.request.CreateCafeRequest;
import com.example.backend.dto.request.CreateSeatRequest;
import com.example.backend.dto.request.UpdateCafeRequest;
import com.example.backend.dto.request.UpdateSeatRequest;
import com.example.backend.entity.Cafe;
import com.example.backend.entity.Seat;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class CafeMapper {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public Cafe toEntity(CreateCafeRequest request) {
        if (request == null) return null;

        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = toSlug(request.getName());
        }

        return Cafe.builder()
                .companyId(request.getCompanyId())
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .address(request.getAddress())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingHours(request.getOpeningHours() != null ? request.getOpeningHours() : "{\"mon\": \"07:00-22:00\"}")
                .imageUrl(request.getImageUrl())
                .imagesJson(request.getImagesJson() != null ? request.getImagesJson() : "[]")
                .isActive(true)
                .deliveryEnabled(request.getDeliveryEnabled() != null ? request.getDeliveryEnabled() : false)
                .pickupEnabled(request.getPickupEnabled() != null ? request.getPickupEnabled() : true)
                .status(request.getStatus() != null ? request.getStatus() : "open")
                .build();
    }

    public void updateEntity(Cafe cafe, UpdateCafeRequest request) {
        if (cafe == null || request == null) return;

        if (request.getName() != null) cafe.setName(request.getName());
        if (request.getSlug() != null) cafe.setSlug(request.getSlug());
        if (request.getDescription() != null) cafe.setDescription(request.getDescription());
        if (request.getAddress() != null) cafe.setAddress(request.getAddress());
        if (request.getProvince() != null) cafe.setProvince(request.getProvince());
        if (request.getDistrict() != null) cafe.setDistrict(request.getDistrict());
        if (request.getWard() != null) cafe.setWard(request.getWard());
        if (request.getLatitude() != null) cafe.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) cafe.setLongitude(request.getLongitude());
        if (request.getPhone() != null) cafe.setPhone(request.getPhone());
        if (request.getEmail() != null) cafe.setEmail(request.getEmail());
        if (request.getOpeningHours() != null) cafe.setOpeningHours(request.getOpeningHours());
        if (request.getImageUrl() != null) cafe.setImageUrl(request.getImageUrl());
        if (request.getImagesJson() != null) cafe.setImagesJson(request.getImagesJson());
        if (request.getIsActive() != null) cafe.setIsActive(request.getIsActive());
        if (request.getDeliveryEnabled() != null) cafe.setDeliveryEnabled(request.getDeliveryEnabled());
        if (request.getPickupEnabled() != null) cafe.setPickupEnabled(request.getPickupEnabled());
        if (request.getStatus() != null) cafe.setStatus(request.getStatus());
    }

    public Seat toSeatEntity(CreateSeatRequest request) {
        if (request == null) return null;

        return Seat.builder()
                .cafeId(request.getCafeId())
                .seatNumber(request.getSeatNumber())
                .zone(request.getZone() != null ? request.getZone() : "Standard")
                .seatType(request.getSeatType() != null ? request.getSeatType() : "table")
                .capacity(request.getCapacity() != null ? request.getCapacity() : 4)
                .minCapacity(request.getMinCapacity() != null ? request.getMinCapacity() : 1)
                .maxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 8)
                .posX(request.getPosX())
                .posY(request.getPosY())
                .posWidth(request.getPosWidth())
                .posHeight(request.getPosHeight())
                .hourlyRate(request.getHourlyRate())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .status(request.getStatus() != null ? request.getStatus() : "available")
                .build();
    }

    public void updateSeatEntity(Seat seat, UpdateSeatRequest request) {
        if (seat == null || request == null) return;

        if (request.getSeatNumber() != null) seat.setSeatNumber(request.getSeatNumber());
        if (request.getZone() != null) seat.setZone(request.getZone());
        if (request.getSeatType() != null) seat.setSeatType(request.getSeatType());
        if (request.getCapacity() != null) seat.setCapacity(request.getCapacity());
        if (request.getMinCapacity() != null) seat.setMinCapacity(request.getMinCapacity());
        if (request.getMaxCapacity() != null) seat.setMaxCapacity(request.getMaxCapacity());
        if (request.getPosX() != null) seat.setPosX(request.getPosX());
        if (request.getPosY() != null) seat.setPosY(request.getPosY());
        if (request.getPosWidth() != null) seat.setPosWidth(request.getPosWidth());
        if (request.getPosHeight() != null) seat.setPosHeight(request.getPosHeight());
        if (request.getHourlyRate() != null) seat.setHourlyRate(request.getHourlyRate());
        if (request.getIsAvailable() != null) seat.setIsAvailable(request.getIsAvailable());
        if (request.getStatus() != null) seat.setStatus(request.getStatus());
    }

    public String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
