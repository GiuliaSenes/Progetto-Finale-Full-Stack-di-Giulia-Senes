import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'
import StatusBadge from '../../components/StatusBadge'
import { SLOT_STATUS, SLOT_TIMES, formatDate, formatTime, toDateKey } from '../../utils/format'

export default function AdminSlots() {
  const [slots, setSlots] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [date, setDate] = useState('')
  const [selectedTimes, setSelectedTimes] = useState([]) // es. ['10:00', '15:00']

  function loadSlots() {
    return api
      .get('/api/admin/slots')
      .then(setSlots)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadSlots().finally(() => setLoading(false))
  }, [])

  // Aggiunge o toglie una fascia oraria dalla selezione
  function toggleTime(start) {
    if (selectedTimes.includes(start)) {
      setSelectedTimes(selectedTimes.filter((time) => time !== start))
    } else {
      setSelectedTimes([...selectedTimes, start])
    }
  }

  async function handleCreate(event) {
    event.preventDefault()
    setError('')
    setSuccess('')
    try {
      const created = await api.post('/api/admin/slots', { date, startTimes: selectedTimes })
      setSuccess(
        created.length === 0
          ? 'Queste fasce esistevano già.'
          : `Disponibilità aggiunta (${created.length} ${created.length === 1 ? 'fascia' : 'fasce'}).`,
      )
      setSelectedTimes([])
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
      setSuccess('Fascia eliminata.')
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
          <legend>Fasce orarie (2 ore ciascuna)</legend>
          <div className="slot-list">
            {SLOT_TIMES.map((time) => (
              <button
                key={time.start}
                type="button"
                className={`slot-button ${selectedTimes.includes(time.start) ? 'selected' : ''}`}
                onClick={() => toggleTime(time.start)}
                aria-pressed={selectedTimes.includes(time.start)}
              >
                {time.label}
              </button>
            ))}
          </div>
        </fieldset>

        <button className="btn" type="submit" disabled={!date || selectedTimes.length === 0}>
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
                  <span>
                    {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
                  </span>
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
