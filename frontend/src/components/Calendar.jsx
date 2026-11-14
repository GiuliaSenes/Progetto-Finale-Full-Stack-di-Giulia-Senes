import { useState } from 'react'
import { parseDate, toDateKey } from '../utils/format'

const WEEK_DAYS = ['Lu', 'Ma', 'Me', 'Gi', 'Ve', 'Sa', 'Do']

/**
 * Calendario mensile.
 * - availableDates: elenco di date "YYYY-MM-DD" in cui Charlotte ha posto
 * - selected: la data scelta (oppure null)
 * - onSelect: funzione chiamata quando si clicca un giorno disponibile
 */
export default function Calendar({ availableDates, selected, onSelect }) {
  const available = new Set(availableDates)

  // Il calendario parte dal mese della prima data disponibile (o dal mese corrente)
  const firstDate = availableDates.length > 0 ? parseDate(availableDates[0]) : new Date()
  const [month, setMonth] = useState(new Date(firstDate.getFullYear(), firstDate.getMonth(), 1))

  const today = new Date()
  const currentMonth = new Date(today.getFullYear(), today.getMonth(), 1)
  const canGoBack = month > currentMonth

  const daysInMonth = new Date(month.getFullYear(), month.getMonth() + 1, 0).getDate()
  // getDay(): 0 = domenica. Noi partiamo da lunedì, quindi spostiamo di uno.
  const offset = (month.getDay() + 6) % 7

  const cells = []
  for (let i = 0; i < offset; i++) {
    cells.push(<span key={`empty-${i}`} className="calendar-empty" />)
  }
  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(month.getFullYear(), month.getMonth(), day)
    const key = toDateKey(date)
    const isAvailable = available.has(key)
    cells.push(
      <button
        key={key}
        type="button"
        disabled={!isAvailable}
        className={`calendar-day ${isAvailable ? 'available' : ''} ${selected === key ? 'selected' : ''}`}
        onClick={() => onSelect(key)}
        aria-label={isAvailable ? `${day}, disponibile` : `${day}, non disponibile`}
        aria-pressed={selected === key}
      >
        {day}
      </button>,
    )
  }

  const monthName = month.toLocaleDateString('it-IT', { month: 'long', year: 'numeric' })

  return (
    <div className="calendar">
      <div className="calendar-header">
        <button
          type="button"
          className="calendar-nav"
          disabled={!canGoBack}
          onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() - 1, 1))}
          aria-label="Mese precedente"
        >
          ‹
        </button>
        <strong>{monthName}</strong>
        <button
          type="button"
          className="calendar-nav"
          onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() + 1, 1))}
          aria-label="Mese successivo"
        >
          ›
        </button>
      </div>

      <div className="calendar-grid">
        {WEEK_DAYS.map((name) => (
          <span key={name} className="calendar-weekday">{name}</span>
        ))}
        {cells}
      </div>
    </div>
  )
}
