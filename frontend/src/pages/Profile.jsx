import { useState } from 'react'
import { api } from '../api'
import Alert from '../components/Alert'
import { useAuth } from '../context/AuthContext'

export default function Profile() {
  const { user, setUser } = useAuth()

  const [profile, setProfile] = useState({
    firstName: user.firstName,
    lastName: user.lastName,
    phone: user.phone,
  })
  const [profileMessage, setProfileMessage] = useState({ type: '', text: '' })

  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })
  const passwordsDiffer = passwords.confirmPassword !== '' && passwords.newPassword !== passwords.confirmPassword
  const [passwordMessage, setPasswordMessage] = useState({ type: '', text: '' })

  async function handleProfileSubmit(event) {
    event.preventDefault()
    setProfileMessage({ type: '', text: '' })
    try {
      const updated = await api.put('/api/users/me', profile)
      setUser(updated)
      setProfileMessage({ type: 'success', text: 'Dati aggiornati.' })
    } catch (err) {
      setProfileMessage({ type: 'error', text: err.message })
    }
  }

  async function handlePasswordSubmit(event) {
    event.preventDefault()
    setPasswordMessage({ type: '', text: '' })

    if (passwords.newPassword !== passwords.confirmPassword) {
      setPasswordMessage({ type: 'error', text: 'Le due password non coincidono' })
      return
    }

    try {
      await api.put('/api/users/me/password', {
        currentPassword: passwords.currentPassword,
        newPassword: passwords.newPassword,
      })
      setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' })
      setPasswordMessage({ type: 'success', text: 'Password cambiata.' })
    } catch (err) {
      setPasswordMessage({ type: 'error', text: err.message })
    }
  }

  return (
    <main className="container narrow">
      <h1>Il mio profilo</h1>

      <form className="card form" onSubmit={handleProfileSubmit}>
        <h2>I tuoi dati</h2>
        <Alert type={profileMessage.type}>{profileMessage.text}</Alert>

        <label>
          Email
          <input value={user.email} disabled />
          <small>L'email non si può modificare.</small>
        </label>

        <div className="form-row">
          <label>
            Nome
            <input
              value={profile.firstName}
              onChange={(e) => setProfile({ ...profile, firstName: e.target.value })}
              required
            />
          </label>
          <label>
            Cognome
            <input
              value={profile.lastName}
              onChange={(e) => setProfile({ ...profile, lastName: e.target.value })}
              required
            />
          </label>
        </div>

        <label>
          Telefono
          <input
            type="tel"
            value={profile.phone}
            onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
            required
          />
        </label>

        <button className="btn" type="submit">Salva modifiche</button>
      </form>

      <form className="card form" onSubmit={handlePasswordSubmit}>
        <h2>Cambia password</h2>
        <Alert type={passwordMessage.type}>{passwordMessage.text}</Alert>

        <label>
          Password attuale
          <input
            type="password"
            value={passwords.currentPassword}
            onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
            autoComplete="current-password"
            required
          />
        </label>

        <label>
          Nuova password
          <input
            type="password"
            value={passwords.newPassword}
            onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
            autoComplete="new-password"
            minLength={8}
            required
          />
          <small>Almeno 8 caratteri.</small>
        </label>

        <label>
          Conferma nuova password
          <input
            type="password"
            value={passwords.confirmPassword}
            onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
            autoComplete="new-password"
            aria-invalid={passwordsDiffer}
            required
          />
          {passwordsDiffer && <small className="field-error">Le due password non coincidono</small>}
        </label>

        <button className="btn" type="submit">Cambia password</button>
      </form>
    </main>
  )
}
