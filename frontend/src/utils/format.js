// Funzioni di aiuto per mostrare date e stati in italiano

// Le due mezze giornate in cui Charlotte può essere disponibile.
// L'orario preciso lo decide lei quando accetta la richiesta (deve stare dentro la fascia).
export const PERIODS = [
  { value: 'MORNING', label: 'Mattina', hint: 'dalle 9:00 alle 13:00' },
  { value: 'AFTERNOON', label: 'Pomeriggio', hint: 'dalle 13:00 alle 20:00' },
]

export const PERIOD_LABELS = {
  MORNING: 'Mattina',
  AFTERNOON: 'Pomeriggio',
}

export const BOOKING_STATUS = {
  PENDING: 'In attesa di conferma',
  ACCEPTED: 'Accettata',
  REJECTED: 'Rifiutata',
  CANCELLED: 'Annullata',
}

export const FLASH_STATUS = {
  AVAILABLE: 'Disponibile',
  PENDING: 'In attesa di conferma',
  UNAVAILABLE: 'Non disponibile',
}

export const SLOT_STATUS = {
  FREE: 'Libero',
  PENDING: 'In attesa',
  BOOKED: 'Prenotato',
}

// Stati delle consulenze visti da Charlotte...
export const CONSULTATION_STATUS = {
  NEW: 'Da gestire',
  HANDLED: 'Gestita',
}

// ...e visti dal cliente
export const CONSULTATION_STATUS_CLIENT = {
  NEW: 'Inviata',
  HANDLED: 'Presa in carico',
}

// "2026-11-20" -> oggetto Date (senza problemi di fuso orario)
export function parseDate(isoDate) {
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year, month - 1, day)
}

// Date -> "2026-11-20"
export function toDateKey(date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

// "2026-11-20" -> "venerdì 20 novembre 2026"
export function formatDate(isoDate) {
  return parseDate(isoDate).toLocaleDateString('it-IT', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

// "10:00:00" -> "10:00"
export function formatTime(time) {
  return time.slice(0, 5)
}

// "venerdì 20 novembre 2026, mattina"
export function formatSlot(slot) {
  return `${formatDate(slot.date)}, ${PERIOD_LABELS[slot.period].toLowerCase()}`
}

// Per una richiesta: se Charlotte ha confermato l'orario lo mostriamo, altrimenti la mezza giornata scelta
export function formatBookingWhen(booking) {
  if (booking.confirmedTime) {
    return `${formatDate(booking.slot.date)}, ore ${formatTime(booking.confirmedTime)}`
  }
  return formatSlot(booking.slot)
}

// "2026-11-14T17:30:00" -> "14 novembre 2026, 17:30"
export function formatDateTime(isoDateTime) {
  return new Date(isoDateTime).toLocaleString('it-IT', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
