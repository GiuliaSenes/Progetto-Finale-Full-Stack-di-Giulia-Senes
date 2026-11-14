import { Link, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Gallery from './pages/Gallery'
import Booking from './pages/Booking'
import MyBookings from './pages/MyBookings'
import Consultation from './pages/Consultation'
import Profile from './pages/Profile'
import Aftercare from './pages/Aftercare'
import AdminPanel from './pages/admin/AdminPanel'

function NotFound() {
  return (
    <main className="container page-message">
      <h1>Pagina non trovata</h1>
      <p>La pagina che cerchi non esiste.</p>
      <Link to="/" className="btn">Torna alla home</Link>
    </main>
  )
}

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        {/* Pagine pubbliche */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/aftercare" element={<Aftercare />} />

        {/* Pagine per utenti loggati */}
        <Route path="/gallery" element={<ProtectedRoute><Gallery /></ProtectedRoute>} />
        <Route path="/booking/:flashId" element={<ProtectedRoute><Booking /></ProtectedRoute>} />
        <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
        <Route path="/consultation" element={<ProtectedRoute><Consultation /></ProtectedRoute>} />
        <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />

        {/* Solo per Charlotte (ruolo ADMIN) */}
        <Route path="/admin" element={<ProtectedRoute adminOnly><AdminPanel /></ProtectedRoute>} />

        <Route path="*" element={<NotFound />} />
      </Routes>
      <footer className="footer">
        <p>© {new Date().getFullYear()} Charlotte Tattoo · Flash fine line e microrealismo</p>
      </footer>
    </>
  )
}
