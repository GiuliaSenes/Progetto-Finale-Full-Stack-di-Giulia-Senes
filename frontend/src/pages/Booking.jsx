import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'
import Calendar from '../components/Calendar'
import { PERIODS, PERIOD_LABELS, formatDate, formatSlot } from '../utils/format'

// Pagina di prenotazione: flash scelto -> calendario -> mattina/pomeriggio -> messaggio -> invio richiesta
export default function Booking() {
  const { flashId } = useParams()
  const navigate = useNavigate()

  const [flash, setFlash] = useState(null)
  const [slots, setSlots] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [selectedDate, setSelectedDate] = useState(null)
  const [selectedSlot, setSelectedSlot] = useState(null)
  const [note, setNote] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    Promise.all([api.get(`/api/flashes/${flashId}`), api.get('/api/slots')])
      .then(([flashData, slotsData]) => {
        setFlash(flashData)
        setSlots(slotsData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [flashId])

  // Date in cui c'è almeno un orario libero (senza ripetizioni)
  const availableDates = [...new Set(slots.map((slot) => slot.date))]
  const slotsOfSelectedDate = slots.filter((slot) => slot.date === selectedDate)

  function handleSelectDate(date) {
    setSelectedDate(date)
    setSelectedSlot(null)
  }

  async function handleSubmit() {
    setError('')
    setSubmitting(true)
    try {
      await api.post('/api/bookings', {
        flashId: flash.id,
        slotId: selectedSlot.id,
        note,
      })
      navigate('/my-bookings', {
        state: { success: 'Richiesta inviata! Charlotte ti risponderà confermando l\'orario preciso.' },
      })
    } catch (err) {
      setError(err.message)
      // Qualcuno potrebbe aver preso la giornata nel frattempo: ricarichiamo la disponibilità
      const [flashData, slotsData] = await Promise.all([
        api.get(`/api/flashes/${flashId}`),
        api.get('/api/slots'),
      ]).catch(() => [null, null])
      if (flashData) setFlash(flashData)
      if (slotsData) setSlots(slotsData)
      setSelectedSlot(null)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <p className="page-message">Caricamento...</p>
  }

  if (!flash) {
    return (
      <main className="container narrow">
        <Alert>{error}</Alert>
        <Link to="/gallery" className="btn btn-outline">Torna alla galleria</Link>
      </main>
    )
  }

  if (flash.status !== 'AVAILABLE') {
    return (
      <main className="container narrow">
        <h1>Flash non disponibile</h1>
        <p>
          {flash.status === 'PENDING'
            ? 'Questo flash è già stato richiesto da un altro cliente ed è in attesa di conferma.'
            : 'Questo flash non è più disponibile.'}
        </p>
        <Link to="/gallery" className="btn">Scegli un altro flash</Link>
      </main>
    )
  }

  return (
    <main className="container">
      <Link to="/gallery" className="text-link">← Torna alla galleria</Link>
      <h1>Prenota il tuo flash</h1>

      <div className="booking-layout">
        <aside className="card booking-flash">
          <img src={flash.imageUrl} alt={`Flash di ${flash.size}`} />
          <p className="flash-size">Dimensione: {flash.size}</p>
          <p className="muted">Durata massima: 2 ore</p>
        </aside>

        <section className="booking-steps">
          <h2>1. Scegli il giorno</h2>
          {availableDates.length === 0 ? (
            <p className="muted">
              Al momento Charlotte non ha date libere. Riprova tra qualche giorno.
            </p>
          ) : (
            <Calendar availableDates={availableDates} selected={selectedDate} onSelect={handleSelectDate} />
          )}

          {selectedDate && (
            <>
              <h2>2. Scegli mattina o pomeriggio</h2>
              <p className="muted">{formatDate(selectedDate)}</p>
              <div className="slot-list">
                {slotsOfSelectedDate.map((slot) => (
                  <button
                    key={slot.id}
                    type="button"
                    className={`slot-button ${selectedSlot?.id === slot.id ? 'selected' : ''}`}
                    onClick={() => setSelectedSlot(slot)}
                    aria-pressed={selectedSlot?.id === slot.id}
                  >
                    {PERIOD_LABELS[slot.period]}
                    <small>{PERIODS.find((p) => p.value === slot.period).hint}</small>
                  </button>
                ))}
              </div>
              <p className="muted small">
                L'orario preciso non lo scegli tu: te lo comunicherà Charlotte quando accetta la richiesta.
              </p>
            </>
          )}

          {selectedSlot && (
            <>
              <h2>3. Aggiungi un messaggio (facoltativo)</h2>
              <label>
                Dubbi o richieste per Charlotte
                <textarea
                  rows={3}
                  maxLength={500}
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  placeholder="Es. ho qualche dubbio sul punto in cui farlo"
                />
                <small>La zona del corpo dove tatuare la deciderai insieme a Charlotte.</small>
              </label>

              <div className="card summary">
                <h3>Riepilogo</h3>
                <p>Flash da {flash.size}</p>
                <p>{formatSlot(selectedSlot)}</p>
                <p className="muted">
                  Orario da definire: te lo confermerà Charlotte. Anche la zona del corpo si decide insieme a lei.
                </p>
                <p className="muted">
                  La richiesta viene inviata a Charlotte, che può accettarla o rifiutarla. Nel frattempo il
                  flash e la giornata restano bloccati per te.
                </p>
              </div>

              <Alert>{error}</Alert>
              <button className="btn" onClick={handleSubmit} disabled={submitting}>
                {submitting ? 'Invio in corso...' : 'Invia la richiesta'}
              </button>
            </>
          )}

          {!selectedSlot && <Alert>{error}</Alert>}
        </section>
      </div>
    </main>
  )
}
