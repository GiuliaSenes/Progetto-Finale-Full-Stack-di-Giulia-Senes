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
        <Link to="/" className="brand" onClick={closeMenu} aria-label="Charlotte Tattoo - vai alla home">
          <img src="/img/logo-full.png" alt="Hot Babe Hot Tattoos by Tottapiga" width="342" height="304" />
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
              {/* Charlotte (admin) riceve le richieste: non sceglie flash e non prenota */}
              {user.role === 'ADMIN' ? (
                <NavLink to="/admin" onClick={closeMenu}>Pannello</NavLink>
              ) : (
                <>
                  <NavLink to="/gallery" onClick={closeMenu}>Flash</NavLink>
                  <NavLink to="/consultation" onClick={closeMenu}>Consulenza</NavLink>
                  <NavLink to="/my-bookings" onClick={closeMenu}>Le mie richieste</NavLink>
                </>
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
