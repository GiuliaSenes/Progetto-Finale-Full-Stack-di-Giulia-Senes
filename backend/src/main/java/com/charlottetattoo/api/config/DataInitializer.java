package com.charlottetattoo.api.config;

import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.Flash;
import com.charlottetattoo.api.model.FlashStatus;
import com.charlottetattoo.api.model.Role;
import com.charlottetattoo.api.model.SlotStatus;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.FlashRepository;
import com.charlottetattoo.api.repository.SlotRepository;
import com.charlottetattoo.api.repository.UserRepository;
import com.charlottetattoo.api.service.FileStorageService;
import com.charlottetattoo.api.service.SlotService;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al primo avvio crea:
 *  - l'account admin di Charlotte
 *  - 4 flash di esempio (con immagini SVG)
 *  - qualche giorno di disponibilita' nelle prossime settimane
 * Se i dati ci sono gia' non fa nulla.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FlashRepository flashRepository;
    private final SlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(UserRepository userRepository, FlashRepository flashRepository,
                           SlotRepository slotRepository, PasswordEncoder passwordEncoder,
                           FileStorageService fileStorageService,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.flashRepository = flashRepository;
        this.slotRepository = slotRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdmin();
        createDemoFlashes();
        createDemoSlots();
    }

    private void createAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        User admin = new User();
        admin.setFirstName("Charlotte");
        admin.setLastName("Tattoo");
        admin.setEmail(adminEmail);
        admin.setPhone("+39 000 0000000");
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private void createDemoFlashes() throws Exception {
        if (flashRepository.count() > 0) {
            return;
        }
        String[][] demo = {
                {"moon.svg", "6 cm"},
                {"flower.svg", "9 cm"},
                {"butterfly.svg", "7 cm"},
                {"sun.svg", "5 cm"}
        };
        for (String[] item : demo) {
            try (InputStream in = new ClassPathResource("seed/" + item[0]).getInputStream()) {
                Flash flash = new Flash();
                flash.setImageFile(fileStorageService.saveFromStream(in, "demo-" + item[0]));
                flash.setSize(item[1]);
                flash.setStatus(FlashStatus.AVAILABLE);
                flashRepository.save(flash);
            }
        }
    }

    // Disponibilita' dal martedi' al sabato per le prossime 4 settimane
    private void createDemoSlots() {
        if (slotRepository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 28; i++) {
            LocalDate date = today.plusDays(i);
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SUNDAY || day == DayOfWeek.MONDAY) {
                continue;
            }
            for (LocalTime start : SlotService.ALLOWED_START_TIMES) {
                AvailabilitySlot slot = new AvailabilitySlot();
                slot.setDate(date);
                slot.setStartTime(start);
                slot.setEndTime(start.plusHours(SlotService.SLOT_HOURS));
                slot.setStatus(SlotStatus.FREE);
                slotRepository.save(slot);
            }
        }
    }
}
