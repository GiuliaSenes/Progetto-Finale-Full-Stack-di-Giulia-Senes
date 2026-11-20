package com.charlottetattoo.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.charlottetattoo.api.repository.PasswordResetTokenRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

/** Test del flusso principale: registrazione, login, prenotazioni e doppia prenotazione. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiFlowTest {

    // Ogni test usa una data diversa, cosi' gli slot dei test non si pestano i piedi
    private static final AtomicInteger DAY_COUNTER = new AtomicInteger(100);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    // ---------- Autenticazione ----------

    @Test
    void registerLoginAndProfile() throws Exception {
        String email = randomEmail();
        String token = register(email);

        // Stessa email una seconda volta -> 409
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(email)))
                .andExpect(status().isConflict());

        // Password sbagliata -> 401
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"sbagliata123\"}"))
                .andExpect(status().isUnauthorized());

        // Login corretto
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"Password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // Profilo con il token
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void registerWithShortPasswordIsRejected() throws Exception {
        String json = registerJson(randomEmail()).replace("Password123", "123");
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La password deve avere almeno 8 caratteri"));
    }

    @Test
    void galleryRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/flashes")).andExpect(status().isUnauthorized());

        String token = register(randomEmail());
        mockMvc.perform(get("/api/flashes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void normalUserCannotUseAdminApi() throws Exception {
        String token = register(randomEmail());
        mockMvc.perform(get("/api/admin/bookings").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ---------- Prenotazioni ----------

    @Test
    void secondPersonCannotBookSameFlashOrSameSlot() throws Exception {
        String admin = loginAdmin();
        long flashA = createFlash(admin);
        long flashB = createFlash(admin);
        long slot1 = createSlot(admin);
        long slot2 = createSlot(admin);

        String anna = register(randomEmail());
        String luca = register(randomEmail());

        // Anna prenota flashA + slot1: la richiesta e' in attesa
        book(anna, flashA, slot1).andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Luca prova lo stesso flash con un altro orario -> 409
        book(luca, flashA, slot2).andExpect(status().isConflict());
        // Luca prova un altro flash ma lo stesso orario -> 409
        book(luca, flashB, slot1).andExpect(status().isConflict());
        // Luca prende un flash e un orario liberi -> ok
        book(luca, flashB, slot2).andExpect(status().isCreated());
    }

    @Test
    void rejectedRequestFreesFlashAndSlot() throws Exception {
        String admin = loginAdmin();
        long flash = createFlash(admin);
        long slot = createSlot(admin);
        String anna = register(randomEmail());
        String luca = register(randomEmail());

        long bookingId = bookingId(book(anna, flash, slot).andExpect(status().isCreated()).andReturn());

        // Il motivo del rifiuto e' obbligatorio
        mockMvc.perform(put("/api/admin/bookings/" + bookingId + "/reject")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/bookings/" + bookingId + "/reject")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Data non disponibile\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Data non disponibile"));

        // Ora Luca puo' prenotare lo stesso flash e lo stesso orario
        book(luca, flash, slot).andExpect(status().isCreated());
    }

    @Test
    void acceptedRequestStaysBlockedUntilCancelled() throws Exception {
        String admin = loginAdmin();
        long flash = createFlash(admin);
        long slot = createSlot(admin);
        String anna = register(randomEmail());
        String luca = register(randomEmail());

        long bookingId = bookingId(book(anna, flash, slot).andExpect(status().isCreated()).andReturn());

        mockMvc.perform(put("/api/admin/bookings/" + bookingId + "/accept")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"time\":\"10:30\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.confirmedTime").value("10:30:00"));

        // Accettata: nessun altro puo' prenotarla
        book(luca, flash, slot).andExpect(status().isConflict());

        // Luca non puo' annullare la richiesta di Anna
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel").header("Authorization", "Bearer " + luca))
                .andExpect(status().isNotFound());

        // Anna annulla -> tutto torna libero
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel").header("Authorization", "Bearer " + anna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        book(luca, flash, slot).andExpect(status().isCreated());
    }

    @Test
    void acceptNeedsATimeInsideTheChosenPeriod() throws Exception {
        String admin = loginAdmin();
        long flash = createFlash(admin);
        long slot = createSlot(admin); // gli slot dei test sono sempre "mattina"
        String anna = register(randomEmail());
        long bookingId = bookingId(book(anna, flash, slot).andExpect(status().isCreated()).andReturn());

        // Senza orario -> 400
        mockMvc.perform(put("/api/admin/bookings/" + bookingId + "/accept")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        // Orario di pomeriggio per una richiesta di mattina -> 400
        mockMvc.perform(put("/api/admin/bookings/" + bookingId + "/accept")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"time\":\"15:00\"}"))
                .andExpect(status().isBadRequest());

        // La richiesta e' ancora in attesa
        mockMvc.perform(get("/api/bookings/mine").header("Authorization", "Bearer " + anna))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void passwordCanBeResetWithTheLinkOnlyOnce() throws Exception {
        String email = randomEmail();
        register(email);

        // Stessa risposta anche per un'email che non esiste
        mockMvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"non-esiste@example.com\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk());

        // Il codice del link è salvato nel database: lo leggiamo da lì
        String token = tokenRepository.findByUserEmail(email).orElseThrow().getToken();

        // Token sbagliato -> 400
        mockMvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"sbagliato\",\"newPassword\":\"NuovaPass123\"}"))
                .andExpect(status().isBadRequest());

        // Token giusto -> password cambiata
        mockMvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"NuovaPass123\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"NuovaPass123\"}"))
                .andExpect(status().isOk());

        // Il link non si puo' riusare
        mockMvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"AltraPass123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminCannotBookOrAskForConsultations() throws Exception {
        String admin = loginAdmin();
        long flash = createFlash(admin);
        long slot = createSlot(admin);

        book(admin, flash, slot).andExpect(status().isForbidden());

        mockMvc.perform(multipart("/api/consultations").param("message", "ciao")
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isForbidden());
    }

    // ---------- FAQ ----------

    @Test
    void faqsArePublicAndOnlyAdminCanEditThem() throws Exception {
        // Si leggono anche senza login, e ci sono quelle iniziali
        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").isNotEmpty());

        String admin = loginAdmin();
        String user = register(randomEmail());
        String json = "{\"question\":\"Domanda di prova?\",\"answer\":\"Risposta di prova.\"}";

        // Un cliente non puo' aggiungere FAQ
        mockMvc.perform(post("/api/admin/faqs").header("Authorization", "Bearer " + user)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isForbidden());

        // Domanda vuota -> 400
        mockMvc.perform(post("/api/admin/faqs").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"\",\"answer\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Scrivi la domanda"));

        // Charlotte crea, modifica ed elimina
        String body = mockMvc.perform(post("/api/admin/faqs").header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();

        mockMvc.perform(put("/api/admin/faqs/" + id).header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"Domanda modificata?\",\"answer\":\"Nuova risposta.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Domanda modificata?"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/admin/faqs/" + id).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNoContent());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/admin/faqs/" + id).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotHaveTooManyPendingRequests() throws Exception {
        String admin = loginAdmin();
        String anna = register(randomEmail());

        // Il limite nei test e' 3
        for (int i = 0; i < 3; i++) {
            book(anna, createFlash(admin), createSlot(admin)).andExpect(status().isCreated());
        }
        book(anna, createFlash(admin), createSlot(admin)).andExpect(status().isConflict());
    }

    // ---------- Funzioni di aiuto ----------

    private org.springframework.test.web.servlet.ResultActions book(String token, long flashId, long slotId)
            throws Exception {
        String json = "{\"flashId\":" + flashId + ",\"slotId\":" + slotId + ",\"note\":\"test\"}";
        return mockMvc.perform(post("/api/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long bookingId(org.springframework.test.web.servlet.MvcResult result) throws Exception {
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private String randomEmail() {
        return "test-" + UUID.randomUUID() + "@example.com";
    }

    private String registerJson(String email) {
        return "{\"firstName\":\"Anna\",\"lastName\":\"Rossi\",\"email\":\"" + email
                + "\",\"phone\":\"3331234567\",\"password\":\"Password123\"}";
    }

    private String register(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private String loginAdmin() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"charlotte@charlottetattoo.it\",\"password\":\"Charlotte2025!\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private long createFlash(String adminToken) throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "flash.png", "image/png", new byte[] {1, 2, 3});
        String body = mockMvc.perform(multipart("/api/admin/flashes").file(image).param("size", "5 cm")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long createSlot(String adminToken) throws Exception {
        LocalDate date = LocalDate.now().plusDays(DAY_COUNTER.incrementAndGet());
        String json = "{\"date\":\"" + date + "\",\"periods\":[\"MORNING\"]}";
        String body = mockMvc.perform(post("/api/admin/slots").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$[0].id")).longValue();
    }
}
