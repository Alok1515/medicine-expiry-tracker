const TOKEN_KEY = 'token'
const USER_ID_KEY = 'userId'
const NAME_KEY = 'name'
const EMAIL_KEY = 'email'

export function saveAuth(token: string, userId: string, name: string, email: string): void {
  if (typeof window === 'undefined') return
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_ID_KEY, userId)
  localStorage.setItem(NAME_KEY, name)
  localStorage.setItem(EMAIL_KEY, email)
}

export function getAuth(): { token: string; userId: string; name: string; email: string } | null {
  if (typeof window === 'undefined') return null
  const token = localStorage.getItem(TOKEN_KEY)
  const userId = localStorage.getItem(USER_ID_KEY)
  const name = localStorage.getItem(NAME_KEY)
  const email = localStorage.getItem(EMAIL_KEY)
  if (!token || !userId || !name || !email) return null
  return { token, userId, name, email }
}

export function logout(): void {
  if (typeof window === 'undefined') return
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_ID_KEY)
  localStorage.removeItem(NAME_KEY)
  localStorage.removeItem(EMAIL_KEY)
  window.location.href = '/auth/login'
}

export function isAuthenticated(): boolean {
  if (typeof window === 'undefined') return false
  return !!localStorage.getItem(TOKEN_KEY)
}
