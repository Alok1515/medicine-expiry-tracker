'use client'

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react'
import { Client } from '@stomp/stompjs'
import type { NotificationItem } from '@/lib/api'

interface WebSocketContextValue {
  notifications: NotificationItem[]
  unreadCount: number
  addNotification: (notification: NotificationItem) => void
}

const WebSocketContext = createContext<WebSocketContextValue | null>(null)

interface WebSocketProviderProps {
  children: ReactNode
  userId: string | null
}

export function WebSocketProvider({ children, userId }: WebSocketProviderProps) {
  const [notifications, setNotifications] = useState<NotificationItem[]>([])
  const [unreadCount, setUnreadCount] = useState(0)

  const addNotification = useCallback((notification: NotificationItem) => {
    setNotifications((prev) => [notification, ...prev])
    if (!notification.read) {
      setUnreadCount((prev) => prev + 1)
    }
  }, [])

  useEffect(() => {
    if (!userId) return

    const wsUrl =
      typeof process !== 'undefined' && process.env.NEXT_PUBLIC_WS_URL
        ? process.env.NEXT_PUBLIC_WS_URL.replace('http', 'ws')
        : 'ws://localhost:8080/ws'

    const client = new Client({
      brokerURL: wsUrl,
      onConnect: () => {
        client.subscribe(`/topic/notifications/${userId}`, (message) => {
          try {
            const notification = JSON.parse(message.body) as NotificationItem
            addNotification(notification)
          } catch {
            // ignore parse errors
          }
        })
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message'])
        console.error('Additional details: ' + frame.body)
      },
      debug: (str) => {
        console.log(str)
      },
    })

    client.activate()

    return () => {
      client.deactivate()
    }
  }, [userId, addNotification])

  const value: WebSocketContextValue = {
    notifications,
    unreadCount,
    addNotification,
  }

  return (
    <WebSocketContext.Provider value={value}>{children}</WebSocketContext.Provider>
  )
}

export function useWebSocket(): WebSocketContextValue {
  const context = useContext(WebSocketContext)
  if (!context) {
    throw new Error('useWebSocket must be used within WebSocketProvider')
  }
  return context
}
