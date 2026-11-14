# Charlotte Tattoo

Web-app per **Charlotte**, tatuatrice specializzata in fine line e microrealismo.
I clienti si registrano, scelgono un **flash** (un disegno pronto da tatuare), scelgono una data libera nel
calendario e inviano la richiesta. Charlotte la **accetta o la rifiuta** (con un motivo) dal suo pannello.
C'è anche un modulo per richiedere una **consulenza** per un tatuaggio personalizzato.

Progetto finale per il corso di start2impact.

## Tecnologie

| Parte    | Tecnologie |
|----------|------------|
| Backend  | Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, database H2 su file, Maven |
| Frontend | React 19, React Router, Vite, CSS semplice |

## Struttura del progetto

```
backend/    API REST (Spring Boot)
  src/main/java/com/charlottetattoo/api/
    model/        entità del database (User, Flash, AvailabilitySlot, Booking, Consultation)
    repository/   accesso al database
    service/      logica dell'applicazione
    controller/   endpoint REST
    security/     login JWT e protezione delle rotte
    dto/          dati in entrata/uscita dalle API
frontend/   interfaccia React
  src/pages/        pagine (utente e pannello admin)
  src/components/   componenti riutilizzabili (Navbar, Calendar...)
  src/context/      gestione del login
```

## Come avviarlo

Servono **Java 17**, **Maven** e **Node.js**.

**1. Backend** (porta 8080)

```bash
cd backend
mvn spring-boot:run
```

**2. Frontend** (porta 5173), in un secondo terminale

```bash
cd frontend
npm install
npm run dev
```

Poi apri http://localhost:5173

Al primo avvio il backend crea da solo l'account di Charlotte, 4 flash di esempio e le disponibilità delle
prossime 4 settimane. Il database è un file in `backend/data/` (non serve installare nulla); le immagini
caricate finiscono in `backend/uploads/`. Per ripartire da zero basta cancellare queste due cartelle.

### Account di prova

| Ruolo | Email | Password |
|-------|-------|----------|
| Charlotte (admin) | `charlotte@charlottetattoo.it` | `Charlotte2025!` |
| Cliente | si crea dalla pagina "Registrati" | a scelta (min. 8 caratteri) |

Le credenziali dell'admin si cambiano in `backend/src/main/resources/application.properties`.
In un sito vero il segreto JWT e la password admin vanno messi in variabili d'ambiente.

## Funzionalità

**Cliente**
- Registrazione, login, logout e modifica del profilo (nome, cognome, telefono, password)
- Galleria dei flash con stato (disponibile / in attesa di conferma / non disponibile) e filtro
- Calendario con le date libere, scelta della fascia oraria (2 ore) e nota facoltativa
- Pagina "Le mie richieste": stato di ogni richiesta, motivo dell'eventuale rifiuto, annullamento
- Modulo di consulenza con immagine di riferimento facoltativa
- Pagina "Aftercare e FAQ"

**Charlotte (pannello admin)**
- Richieste: accetta oppure rifiuta scrivendo il motivo
- Flash: carica nuove immagini, segna un flash come disponibile / non disponibile, elimina
- Disponibilità: apre giorni e fasce orarie (10–12, 12–14, 15–17, 17–19)
- Consulenze: legge le richieste e le segna come gestite

### Come evitiamo le doppie prenotazioni

Appena un cliente invia la richiesta, **il flash e lo slot si bloccano** (stato `PENDING`) in attesa della
risposta di Charlotte. Nessun altro può sceglierli:

| Evento | Flash | Slot |
|--------|-------|------|
| Cliente invia la richiesta | in attesa | in attesa |
| Charlotte **accetta** | non disponibile | prenotato |
| Charlotte **rifiuta** o il cliente **annulla** | di nuovo disponibile | di nuovo libero |

Nel database, flash e slot vengono letti con un blocco (`PESSIMISTIC_WRITE`), così anche due clic
contemporanei non possono prenotare la stessa cosa. Ogni cliente può avere al massimo 3 richieste in attesa
(`app.max-pending-bookings`), per evitare che qualcuno blocchi tutta la galleria.

## API REST

Gli errori hanno sempre la forma `{"message": "testo in italiano"}`. Le rotte protette richiedono
l'header `Authorization: Bearer <token>`.

| Metodo | Percorso | Chi | Descrizione |
|--------|----------|-----|-------------|
| POST | `/api/auth/register` | tutti | Registrazione (restituisce il token) |
| POST | `/api/auth/login` | tutti | Login (restituisce il token) |
| GET / PUT | `/api/users/me` | loggato | Vedi / modifica il tuo profilo |
| PUT | `/api/users/me/password` | loggato | Cambia password |
| GET | `/api/flashes`, `/api/flashes/{id}` | loggato | Galleria dei flash |
| GET | `/api/slots` | loggato | Fasce orarie libere e future |
| POST | `/api/bookings` | loggato | Invia una richiesta di prenotazione |
| GET | `/api/bookings/mine` | loggato | Le mie richieste |
| PUT | `/api/bookings/{id}/cancel` | loggato | Annulla una mia richiesta |
| POST | `/api/consultations` | loggato | Richiedi una consulenza (multipart) |
| GET | `/api/consultations/mine` | loggato | Le mie consulenze |
| POST | `/api/admin/flashes` | admin | Aggiungi un flash (multipart: `image`, `size`) |
| PUT / DELETE | `/api/admin/flashes/{id}` | admin | Modifica stato / elimina un flash |
| GET / POST | `/api/admin/slots` | admin | Elenco / aggiunta disponibilità |
| DELETE | `/api/admin/slots/{id}` | admin | Elimina una fascia libera |
| GET | `/api/admin/bookings?status=PENDING` | admin | Elenco richieste (filtro facoltativo) |
| PUT | `/api/admin/bookings/{id}/accept` | admin | Accetta una richiesta |
| PUT | `/api/admin/bookings/{id}/reject` | admin | Rifiuta con un motivo |
| GET | `/api/admin/consultations` | admin | Elenco consulenze |
| PUT | `/api/admin/consultations/{id}/handled` | admin | Segna come gestita |

## Sicurezza

- Le password sono salvate con hash **BCrypt**, mai in chiaro
- Login con **JWT** (scade dopo 24 ore); chi si registra è sempre un cliente, mai admin
- Le rotte `/api/admin/**` sono accessibili solo al ruolo `ADMIN`
- Upload: solo JPG, PNG, WEBP fino a 5 MB, con nome file generato dal server

## Test

```bash
cd backend
mvn test
```

I test (`ApiFlowTest`) controllano registrazione e login, le rotte protette, e tutto il flusso delle
prenotazioni: doppia prenotazione bloccata, rifiuto, accettazione, annullamento e limite di richieste.

## Possibili sviluppi futuri

- Email automatiche alla tatuatrice e al cliente
- Scadenza automatica delle richieste rimaste senza risposta
- Preferiti sui flash e filtri per zona del corpo o dimensione
