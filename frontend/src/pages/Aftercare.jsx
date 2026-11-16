import { useEffect, useState } from 'react'
import { api } from '../api'
import Alert from '../components/Alert'

// Consigli per la cura del tatuaggio: testo fisso
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

export default function Aftercare() {
  // Le FAQ arrivano dal database: le gestisce Charlotte dal suo pannello
  const [faqs, setFaqs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/api/faqs')
      .then(setFaqs)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

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
        <Alert>{error}</Alert>
        {loading && <p className="page-message">Caricamento...</p>}
        {!loading && !error && faqs.length === 0 && <p className="muted">Nessuna domanda al momento.</p>}
        <div className="faq">
          {faqs.map((item) => (
            <details key={item.id}>
              <summary>{item.question}</summary>
              <p className="pre-line">{item.answer}</p>
            </details>
          ))}
        </div>
      </section>
    </main>
  )
}
