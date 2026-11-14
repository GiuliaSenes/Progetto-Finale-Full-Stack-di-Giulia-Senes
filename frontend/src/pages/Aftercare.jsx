const AFTERCARE = [
  {
    title: 'Le prime 24 ore',
    text: 'Tieni la pellicola protettiva per il tempo indicato da Charlotte. Poi lava delicatamente il tatuaggio con acqua tiepida e un sapone neutro, e asciuga tamponando con carta pulita.',
  },
  {
    title: 'Nei primi giorni',
    text: 'Applica un velo sottilissimo di crema cicatrizzante 2 o 3 volte al giorno. Non serve esagerare: la pelle deve respirare.',
  },
  {
    title: 'Non grattare',
    text: 'Le crosticine e la desquamazione sono normali. Non staccarle e non grattare: potresti perdere colore, e con le linee sottili si nota molto.',
  },
  {
    title: 'Da evitare per 2–3 settimane',
    text: 'Sole diretto, lettini abbronzanti, piscina, mare e sauna. Evita anche vestiti stretti che sfregano sul tatuaggio.',
  },
  {
    title: 'A guarigione avvenuta',
    text: 'Usa sempre una crema solare ad alta protezione: è il modo migliore per mantenere nel tempo la finezza del tratto.',
  },
]

const FAQ = [
  {
    question: 'Come funziona la prenotazione?',
    answer:
      "Scegli un flash dalla galleria, scegli una data e un orario liberi e invia la richiesta. Charlotte la riceve e può accettarla oppure rifiutarla (in quel caso ti scrive il motivo). Puoi seguire lo stato nella pagina \"Le mie richieste\".",
  },
  {
    question: 'Cosa significa "in attesa di conferma"?',
    answer:
      "Quando qualcuno invia una richiesta, il flash e l'orario scelti vengono bloccati finché Charlotte non risponde. In questo modo non possono esserci due prenotazioni sullo stesso flash. Se la richiesta viene rifiutata o annullata, tornano subito disponibili.",
  },
  {
    question: 'Quanto dura un tatuaggio flash?',
    answer: 'Ogni appuntamento dura al massimo 2 ore, per questo il calendario è diviso in fasce da 2 ore.',
  },
  {
    question: 'Posso modificare un flash?',
    answer:
      'I flash si tatuano come sono stati disegnati. Se vuoi qualcosa di personalizzato, usa il modulo "Consulenza" e racconta la tua idea a Charlotte.',
  },
  {
    question: 'Posso annullare una richiesta?',
    answer:
      'Sì, dalla pagina "Le mie richieste" puoi annullare le richieste in attesa o già accettate. Se non puoi più venire, annulla il prima possibile così il posto torna libero per qualcun altro.',
  },
  {
    question: 'Serve essere maggiorenni?',
    answer: 'Sì. Per tatuarsi è necessario essere maggiorenni.',
  },
  {
    question: 'Quanto costa?',
    answer:
      'Il prezzo dipende dalle dimensioni e dal disegno. Charlotte ti darà tutte le informazioni quando risponde alla tua richiesta.',
  },
  {
    question: 'Come funziona la consulenza?',
    answer:
      "Compili il modulo con la tua idea e, se vuoi, allegando un'immagine di riferimento. Charlotte lo legge e ti contatta via email o telefono per accordarsi sui dettagli.",
  },
]

export default function Aftercare() {
  return (
    <main className="container narrow-wide">
      <h1>Aftercare e FAQ</h1>
      <p className="subtitle">Tutto quello che c'è da sapere prima e dopo il tuo tatuaggio.</p>

      <section className="section-small">
        <h2>Come curare il tuo tatuaggio</h2>
        <ol className="aftercare-list">
          {AFTERCARE.map((item) => (
            <li key={item.title}>
              <h3>{item.title}</h3>
              <p>{item.text}</p>
            </li>
          ))}
        </ol>
      </section>

      <section className="section-small">
        <h2>Domande frequenti</h2>
        <div className="faq">
          {FAQ.map((item) => (
            <details key={item.question}>
              <summary>{item.question}</summary>
              <p>{item.answer}</p>
            </details>
          ))}
        </div>
      </section>
    </main>
  )
}
