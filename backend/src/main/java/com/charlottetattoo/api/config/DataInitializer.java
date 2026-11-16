package com.charlottetattoo.api.config;

import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.DayPeriod;
import com.charlottetattoo.api.model.Faq;
import com.charlottetattoo.api.model.Flash;
import com.charlottetattoo.api.model.FlashStatus;
import com.charlottetattoo.api.model.Role;
import com.charlottetattoo.api.model.SlotStatus;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.FaqRepository;
import com.charlottetattoo.api.repository.FlashRepository;
import com.charlottetattoo.api.repository.SlotRepository;
import com.charlottetattoo.api.repository.UserRepository;
import com.charlottetattoo.api.service.FileStorageService;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
    private final FaqRepository faqRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(UserRepository userRepository, FlashRepository flashRepository,
                           SlotRepository slotRepository, FaqRepository faqRepository,
                           PasswordEncoder passwordEncoder, FileStorageService fileStorageService,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.flashRepository = flashRepository;
        this.slotRepository = slotRepository;
        this.faqRepository = faqRepository;
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
        createDefaultFaqs();
    }

    // Domande di partenza: poi Charlotte le modifica dal pannello
    private void createDefaultFaqs() {
        if (faqRepository.count() > 0) {
            return;
        }
        String[][] faqs = {
                {"Come funziona la prenotazione?",
                        "Scegli un flash dalla galleria, scegli un giorno e se preferisci la mattina o il pomeriggio, "
                                + "poi invia la richiesta. Charlotte la riceve e può accettarla, comunicandoti l'orario "
                                + "preciso, oppure rifiutarla (in quel caso ti scrive il motivo). Puoi seguire lo stato "
                                + "nella pagina \"Le mie richieste\"."},
                {"Cosa significa \"in attesa di conferma\"?",
                        "Quando qualcuno invia una richiesta, il flash e la giornata scelti vengono bloccati finché "
                                + "Charlotte non risponde. In questo modo non possono esserci due prenotazioni sullo "
                                + "stesso flash. Se la richiesta viene rifiutata o annullata, tornano subito disponibili."},
                {"Quando scopro l'orario preciso?",
                        "Tu scegli solo il giorno e se preferisci mattina o pomeriggio. L'orario esatto te lo comunica "
                                + "Charlotte quando accetta la richiesta: lo trovi nella pagina \"Le mie richieste\"."},
                {"Dove sul corpo faccio il tatuaggio?",
                        "La zona del corpo si decide insieme a Charlotte, che ti consiglia il punto migliore per il "
                                + "disegno che hai scelto."},
                {"Quanto dura un tatuaggio flash?",
                        "Ogni appuntamento dura al massimo 2 ore, perché i flash sono disegni piccoli e veloci da "
                                + "realizzare."},
                {"Posso modificare un flash?",
                        "I flash si tatuano come sono stati disegnati. Se vuoi qualcosa di personalizzato, usa il "
                                + "modulo \"Consulenza\" e racconta la tua idea a Charlotte."},
                {"Posso annullare una richiesta?",
                        "Sì, dalla pagina \"Le mie richieste\" puoi annullare le richieste in attesa o già accettate. "
                                + "Se non puoi più venire, annulla il prima possibile così il posto torna libero per "
                                + "qualcun altro."},
                {"Serve essere maggiorenni?", "Sì. Per tatuarsi è necessario essere maggiorenni."},
                {"Quanto costa?",
                        "Il prezzo dipende dalle dimensioni e dal disegno. Charlotte ti darà tutte le informazioni "
                                + "quando risponde alla tua richiesta."},
                {"Come funziona la consulenza?",
                        "Compili il modulo con la tua idea e, se vuoi, allegando un'immagine di riferimento. Charlotte "
                                + "lo legge e ti contatta via email o telefono per accordarsi sui dettagli."},
                {"Ho dimenticato la password: cosa faccio?",
                        "Dalla pagina di accesso clicca su \"Hai dimenticato la password?\" e segui il link per "
                                + "sceglierne una nuova. Per accedere si usa sempre la tua email."}
        };
        for (String[] item : faqs) {
            Faq faq = new Faq();
            faq.setQuestion(item[0]);
            faq.setAnswer(item[1]);
            faqRepository.save(faq);
        }
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
            for (DayPeriod period : DayPeriod.values()) {
                AvailabilitySlot slot = new AvailabilitySlot();
                slot.setDate(date);
                slot.setPeriod(period);
                slot.setStatus(SlotStatus.FREE);
                slotRepository.save(slot);
            }
        }
    }
}
