import { useState } from 'react'
import AdminBookings from './AdminBookings'
import AdminFlashes from './AdminFlashes'
import AdminSlots from './AdminSlots'
import AdminConsultations from './AdminConsultations'
import AdminFaqs from './AdminFaqs'

const TABS = [
  { id: 'bookings', label: 'Richieste' },
  { id: 'flashes', label: 'Flash' },
  { id: 'slots', label: 'Disponibilità' },
  { id: 'consultations', label: 'Consulenze' },
  { id: 'faqs', label: 'FAQ' },
]

// Pannello di Charlotte: una pagina con 5 schede
export default function AdminPanel() {
  const [tab, setTab] = useState('bookings')

  return (
    <main className="container">
      <h1>Pannello di Charlotte</h1>

      <div className="tabs" role="tablist">
        {TABS.map((item) => (
          <button
            key={item.id}
            role="tab"
            aria-selected={tab === item.id}
            className={`tab ${tab === item.id ? 'active' : ''}`}
            onClick={() => setTab(item.id)}
          >
            {item.label}
          </button>
        ))}
      </div>

      {tab === 'bookings' && <AdminBookings />}
      {tab === 'flashes' && <AdminFlashes />}
      {tab === 'slots' && <AdminSlots />}
      {tab === 'consultations' && <AdminConsultations />}
      {tab === 'faqs' && <AdminFaqs />}
    </main>
  )
}
