# Charlotte Tattoo

Web-app per **Charlotte**, tatuatrice specializzata in fine line e microrealismo.
I clienti si registrano, scelgono un **flash** (un disegno pronto da tatuare), scelgono un giorno libero nel
calendario e se preferiscono **mattina o pomeriggio**, poi inviano la richiesta. Charlotte la **accetta
comunicando l'orario preciso, oppure la rifiuta** (con un motivo) dal suo pannello.
La zona del corpo dove tatuare si decide insieme a lei.
C'è anche un modulo per richiedere una **consulenza** per un tatuaggio personalizzato.

Progetto finale per il corso di start2impact.

##NETLIFY
https://progetto-finale-di-giulia-senes.netlify.app/

## Tecnologie

| Parte    | Tecnologie |
|----------|------------|
| Backend  | Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, database H2 su file, Maven |
| Frontend | React 19, React Router, Vite, CSS semplice |


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
caricate finiscono in `backend/uploads/`. Per ripartire da zero basta cancellare queste due cartelle
(da fare anche dopo un aggiornamento che cambia la struttura del database).



## Funzionalità
**Cliente**
- Registrazione (con conferma password), login, logout, recupero password e modifica del profilo
- Galleria dei flash con stato (disponibile / in attesa di conferma / non disponibile) e filtro
- Calendario con i giorni liberi, scelta di **mattina o pomeriggio** e messaggio facoltativo per Charlotte
- Pagina "Le mie richieste": stato di ogni richiesta, orario confermato, motivo dell'eventuale rifiuto, annullamento
- Modulo di consulenza con immagine di riferimento facoltativa
- Pagina "Aftercare e FAQ"

**Charlotte (pannello admin)**
- Richieste: accetta indicando l'orario preciso (dentro la mattina o il pomeriggio scelti), oppure rifiuta scrivendo il motivo
- Flash: carica nuove immagini, segna un flash come disponibile / non disponibile, elimina
- Disponibilità: apre giorni con mattina (9–13), pomeriggio (13–20) o entrambi
- Consulenze: legge le richieste e le segna come gestite
- FAQ: aggiunge, modifica ed elimina le domande frequenti mostrate nella pagina "Aftercare e FAQ"
  (al primo avvio ci sono già alcune domande di partenza)

Charlotte riceve le richieste ma non le fa: nel suo menu ci sono solo Pannello e Profilo, le pagine dei
clienti la rimandano al pannello e il backend rifiuta prenotazioni e consulenze fatte con il suo account.

### Recupero password

Si accede con l'**email** (non esiste uno username separato). Dalla pagina di accesso, "Hai dimenticato la
password?" genera un link monouso valido 60 minuti.
**Il progetto non invia email:** il link viene scritto nella **console del backend**, nella riga
`RECUPERO PASSWORD per ...`. In un sito vero si collegherebbe un servizio di posta (es. Spring Mail) nel
metodo `PasswordResetService.requestReset`.

### Come evitiamo le doppie prenotazioni
Appena un cliente invia la richiesta, **il flash e lo slot si bloccano** (stato `PENDING`) in attesa della
risposta di Charlotte. Nessun altro può sceglierli:

| Evento | Flash | Slot |
|--------|-------|------|
| Cliente invia la richiesta | in attesa | in attesa |
| Charlotte **accetta** (con l'orario) | non disponibile | prenotato |
| Charlotte **rifiuta** o il cliente **annulla** | di nuovo disponibile | di nuovo libero |

Prima di creare la richiesta il backend controlla che flash e slot siano ancora liberi. Ogni cliente può avere al massimo 3 richieste in attesa
(`app.max-pending-bookings`), per evitare che qualcuno blocchi tutta la galleria.


## Sicurezza

- Le password sono salvate con hash **BCrypt**, mai in chiaro
- Login con **JWT** (scade dopo 24 ore); chi si registra è sempre un cliente, mai admin
- Le rotte `/api/admin/**` sono accessibili solo al ruolo `ADMIN`

## Test

```bash
cd backend
mvn test
```

I test (`ApiFlowTest`) controllano registrazione e login, il recupero password, le rotte protette, e tutto il
flusso delle prenotazioni: doppia prenotazione bloccata, rifiuto, accettazione con orario, annullamento e
limite di richieste.
