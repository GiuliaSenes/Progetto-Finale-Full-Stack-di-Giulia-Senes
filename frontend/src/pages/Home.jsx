import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const STEPS = [
  { title: 'Registrati', text: 'Crea il tuo account in meno di un minuto.' },
  { title: 'Scegli il flash', text: 'Sfoglia la galleria e seleziona il disegno che ti piace.' },
  { title: 'Scegli la data', text: 'Guarda il calendario con le disponibilità di Charlotte e scegli il giorno, di mattina o di pomeriggio.' },
  { title: 'Aspetta la conferma', text: 'Charlotte ti risponde con l\'orario preciso, oppure ti spiega perché non può.' },
]

export default function Home() {
  const { user } = useAuth()

  return (
    <main>
      <section className="hero">
        <div className="container hero-inner">
          <p className="eyebrow">Fine line · Microrealismo</p>
          <h1>Piccoli disegni, tratti sottilissimi.</h1>
          <p className="hero-text">
            Sono Charlotte, tatuatrice specializzata in fine line e microrealismo. Scegli uno dei
            miei flash disponibili, prenota il tuo orario e vieni a farti tatuare.
          </p>
          <div className="hero-actions">
            <Link to={user ? '/gallery' : '/register'} className="btn">
              {user ? 'Vedi i flash disponibili' : 'Registrati per prenotare'}
            </Link>
            <Link to={user ? '/consultation' : '/login'} className="btn btn-outline">
              {user ? 'Richiedi una consulenza' : 'Accedi'}
            </Link>
          </div>
        </div>
      </section>

      <section className="container section">
        <h2>Come funziona</h2>
        <ol className="steps">
          {STEPS.map((step, index) => (
            <li key={step.title} className="step">
              <span className="step-number">{index + 1}</span>
              <h3>{step.title}</h3>
              <p>{step.text}</p>
            </li>
          ))}
        </ol>
      </section>

      <section className="container section two-columns">
        <div>
          <h2>Cosa sono i flash?</h2>
          <p>
            I flash sono disegni già pronti, pensati per essere tatuati una sola volta. Sono
            piccoli, veloci da realizzare (al massimo 2 ore) e a un prezzo accessibile. Quando
            un flash viene tatuato, esce dalla galleria e non verrà più rifatto uguale.
          </p>
        </div>
        <div>
          <h2>Hai un'idea tutta tua?</h2>
          <p>
            Se non trovi il disegno giusto puoi richiedere una consulenza: raccontami la tua idea,
            allega un'immagine di riferimento e ci mettiamo d'accordo su come realizzarla.
          </p>
          <Link to={user ? '/consultation' : '/login'} className="text-link">
            Richiedi una consulenza →
          </Link>
        </div>
      </section>

      <section className="container section cta-box">
        <h2>Prima di prenotare</h2>
        <p>Leggi le informazioni su cura del tatuaggio e le risposte alle domande più frequenti.</p>
        <Link to="/aftercare" className="btn btn-outline">Aftercare e FAQ</Link>
      </section>
    </main>
  )
}
