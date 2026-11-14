import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false) // menu aperto su mobile

  function handleLogout() {
    // Prima andiamo alla home, poi facciamo il logout: così non finiamo sul login
    navigate('/')
    logout()
    setOpen(false)
  }

  const closeMenu = () => setOpen(false)

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand" onClick={closeMenu}>
          Charlotte <span>tattoo</span>
        </Link>

        <button
          className="menu-toggle"
          aria-label="Apri o chiudi il menu"
          aria-expanded={open}
          onClick={() => setOpen(!open)}
        >
          {open ? '✕' : '☰'}
        </button>

        <nav className={`nav-links ${open ? 'open' : ''}`}>
          <NavLink to="/aftercare" onClick={closeMenu}>Aftercare e FAQ</NavLink>

          {user && (
            <>
              <NavLink to="/gallery" onClick={closeMenu}>Flash</NavLink>
              <NavLink to="/consultation" onClick={closeMenu}>Consulenza</NavLink>
              <NavLink to="/my-bookings" onClick={closeMenu}>Le mie richieste</NavLink>
              {user.role === 'ADMIN' && (
                <NavLink to="/admin" onClick={closeMenu}>Pannello</NavLink>
              )}
              <NavLink to="/profile" onClick={closeMenu}>Profilo</NavLink>
              <button className="btn btn-small btn-outline" onClick={handleLogout}>
                Esci
              </button>
            </>
          )}

          {!user && (
            <>
              <NavLink to="/login" onClick={closeMenu}>Accedi</NavLink>
              <Link to="/register" className="btn btn-small" onClick={closeMenu}>
                Registrati
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
