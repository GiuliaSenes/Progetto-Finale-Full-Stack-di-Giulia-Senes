// Funzioni di aiuto per mostrare date e stati in italiano

// Le fasce orarie che Charlotte può aprire (devono coincidere con quelle del backend)
export const SLOT_TIMES = [
  { start: '10:00', label: '10:00 – 12:00' },
  { start: '12:00', label: '12:00 – 14:00' },
  { start: '15:00', label: '15:00 – 17:00' },
  { start: '17:00', label: '17:00 – 19:00' },
]

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

export const CONSULTATION_STATUS = {
  NEW: 'Da gestire',
  HANDLED: 'Gestita',
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

export function formatSlot(slot) {
  return `${formatDate(slot.date)}, ${formatTime(slot.startTime)} – ${formatTime(slot.endTime)}`
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
