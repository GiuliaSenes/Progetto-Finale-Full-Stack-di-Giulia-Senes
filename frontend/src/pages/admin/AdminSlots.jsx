import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'
import StatusBadge from '../../components/StatusBadge'
import { PERIODS, PERIOD_LABELS, SLOT_STATUS, formatDate, toDateKey } from '../../utils/format'

export default function AdminSlots() {
  const [slots, setSlots] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [date, setDate] = useState('')
  const [selectedPeriods, setSelectedPeriods] = useState([]) // es. ['MORNING', 'AFTERNOON']

  function loadSlots() {
    return api
      .get('/api/admin/slots')
      .then(setSlots)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadSlots().finally(() => setLoading(false))
  }, [])

  // Aggiunge o toglie mattina/pomeriggio dalla selezione
  function togglePeriod(period) {
    if (selectedPeriods.includes(period)) {
      setSelectedPeriods(selectedPeriods.filter((p) => p !== period))
    } else {
      setSelectedPeriods([...selectedPeriods, period])
    }
  }

  async function handleCreate(event) {
    event.preventDefault()
    setError('')
    setSuccess('')
    try {
      const created = await api.post('/api/admin/slots', { date, periods: selectedPeriods })
      setSuccess(created.length === 0 ? 'Questa disponibilità esisteva già.' : 'Disponibilità aggiunta.')
      setSelectedPeriods([])
      await loadSlots()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDelete(slot) {
    setError('')
    setSuccess('')
    try {
      await api.delete(`/api/admin/slots/${slot.id}`)
      setSuccess('Disponibilità eliminata.')
      await loadSlots()
    } catch (err) {
      setError(err.message)
    }
  }

  // Raggruppiamo gli slot per giorno: { "2026-11-20": [slot, slot], ... }
  const slotsByDate = {}
  slots.forEach((slot) => {
    if (!slotsByDate[slot.date]) {
      slotsByDate[slot.date] = []
    }
    slotsByDate[slot.date].push(slot)
  })

  return (
    <section>
      <form className="card form" onSubmit={handleCreate}>
        <h2>Aggiungi disponibilità</h2>

        <label>
          Giorno
          <input
            type="date"
            value={date}
            min={toDateKey(new Date())}
            onChange={(e) => setDate(e.target.value)}
            required
          />
        </label>

        <fieldset>
          <legend>Quando sei disponibile?</legend>
          <div className="slot-list">
            {PERIODS.map((period) => (
              <button
                key={period.value}
                type="button"
                className={`slot-button ${selectedPeriods.includes(period.value) ? 'selected' : ''}`}
                onClick={() => togglePeriod(period.value)}
                aria-pressed={selectedPeriods.includes(period.value)}
              >
                {period.label}
                <small>{period.hint}</small>
              </button>
            ))}
          </div>
          <p className="muted small">
            Il cliente sceglie solo mattina o pomeriggio: l'orario preciso lo decidi tu quando accetti la richiesta.
          </p>
        </fieldset>

        <button className="btn" type="submit" disabled={!date || selectedPeriods.length === 0}>
          Aggiungi
        </button>
      </form>

      <Alert type="success">{success}</Alert>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}
      {!loading && slots.length === 0 && <p className="muted">Non hai ancora aggiunto nessuna disponibilità.</p>}
      <div className="list">
        {Object.entries(slotsByDate).map(([day, daySlots]) => (
          <article key={day} className="card">
            <h3 className="capitalize">{formatDate(day)}</h3>
            <ul className="slot-rows">
              {daySlots.map((slot) => (
                <li key={slot.id}>
                  <span>{PERIOD_LABELS[slot.period]}</span>
                  <StatusBadge labels={SLOT_STATUS} value={slot.status} />
                  {slot.status === 'FREE' ? (
                    <button className="btn btn-small btn-outline" onClick={() => handleDelete(slot)}>
                      Elimina
                    </button>
                  ) : (
                    <span className="muted small">Con richiesta collegata</span>
                  )}
                </li>
              ))}
            </ul>
          </article>
        ))}
      </div>
    </section>
  )
}
