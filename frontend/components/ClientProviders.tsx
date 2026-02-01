'use client'

import { useEffect, useState, type ReactNode } from 'react'
import { WebSocketProvider } from './WebSocketProvider'
import { getAuth } from '@/lib/auth'

export function ClientProviders({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null)

  useEffect(() => {
    const auth = getAuth()
    if (auth) {
      setUserId(auth.userId)
    }
  }, [])

  return (
    <WebSocketProvider userId={userId}>
      {children}
    </WebSocketProvider>
  )
}
