import { createContext, useContext, useEffect, useState } from 'react'
import { api, clearToken, getToken, setToken } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  // Finché non sappiamo se l'utente è loggato (token nel localStorage) mostriamo "Caricamento"
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!getToken()) {
      setLoading(false)
      return
    }
    api
      .get('/api/users/me')
      .then(setUser)
      .catch(() => clearToken())
      .finally(() => setLoading(false))
  }, [])

  // Se il backend dice che il token è scaduto, facciamo il logout
  useEffect(() => {
    function handleExpired() {
      clearToken()
      setUser(null)
    }
    window.addEventListener('auth:expired', handleExpired)
    return () => window.removeEventListener('auth:expired', handleExpired)
  }, [])

  async function login(email, password) {
    const data = await api.post('/api/auth/login', { email, password })
    setToken(data.token)
    setUser(data.user)
    return data.user
  }

  async function register(form) {
    const data = await api.post('/api/auth/register', form)
    setToken(data.token)
    setUser(data.user)
    return data.user
  }

  function logout() {
    clearToken()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, setUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
