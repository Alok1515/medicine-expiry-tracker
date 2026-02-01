'use client'

import { useCallback, useEffect, useState } from 'react'
import { notificationAPI, type NotificationItem } from '@/lib/api'

function formatDate(createdAt: string) {
  try {
    const d = new Date(createdAt)
    return d.toLocaleString()
  } catch {
    return createdAt
  }
}

function typeEmoji(type: NotificationItem['type']) {
  switch (type) {
    case 'EXPIRED':
      return '⚠️'
    case 'EXPIRING_SOON':
      return '🕐'
    case 'DISPOSED':
      return '🗑️'
    default:
      return '📌'
  }
}

export default function AlertsPage() {
  const [notifications, setNotifications] = useState<NotificationItem[]>([])
  const [unreadCount, setUnreadCount] = useState(0)
  const [loading, setLoading] = useState(true)

  const fetchAll = useCallback(() => {
    setLoading(true)
    Promise.all([notificationAPI.getAll(), notificationAPI.getUnreadCount()])
      .then(([listRes, countRes]) => {
        setNotifications(listRes.data)
        setUnreadCount(countRes.data.unreadCount)
      })
      .catch(() => {
        setNotifications([])
        setUnreadCount(0)
      })
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    fetchAll()
  }, [fetchAll])

  const handleMarkAsRead = (id: string) => {
    const item = notifications.find((n) => n.id === id)
    if (!item || item.read) return
    notificationAPI
      .markAsRead(id)
      .then(() => {
        setNotifications((prev) =>
          prev.map((n) => (n.id === id ? { ...n, read: true } : n))
        )
        setUnreadCount((c) => Math.max(0, c - 1))
      })
      .catch(() => {})
  }

  const handleMarkAllAsRead = () => {
    if (unreadCount === 0) return
    notificationAPI
      .markAllAsRead()
      .then(() => {
        setNotifications((prev) => prev.map((n) => ({ ...n, read: true })))
        setUnreadCount(0)
      })
      .catch(() => {})
  }

  if (loading) {
    return (
      <main className="max-w-3xl mx-auto px-4 py-8">
        <p className="text-gray-500">Loading...</p>
      </main>
    )
  }

  return (
    <main className="max-w-3xl mx-auto px-4 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Alerts</h1>
        <div className="flex items-center gap-3">
          {unreadCount > 0 && (
            <span className="text-sm text-gray-600">
              {unreadCount} unread
            </span>
          )}
          {unreadCount > 0 && (
            <button
              type="button"
              onClick={handleMarkAllAsRead}
              className="text-sm font-medium text-blue-600 hover:text-blue-800"
            >
              Mark all as read
            </button>
          )}
        </div>
      </div>

      {notifications.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <p className="text-gray-500 text-lg">No alerts yet</p>
          <p className="text-gray-400 text-sm mt-1">
            You&apos;ll see expiry and disposal notifications here.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {notifications.map((n) => (
            <div
              key={n.id}
              role="button"
              tabIndex={0}
              onClick={() => !n.read && handleMarkAsRead(n.id)}
              onKeyDown={(e) => {
                if ((e.key === 'Enter' || e.key === ' ') && !n.read) {
                  e.preventDefault()
                  handleMarkAsRead(n.id)
                }
              }}
              className={`rounded-lg border p-4 ${
                n.read
                  ? 'bg-white border-gray-200'
                  : 'bg-blue-50 border-blue-200'
              }`}
            >
              <div className="flex gap-3">
                {!n.read && (
                  <span className="flex-shrink-0 w-2 h-2 mt-1.5 rounded-full bg-blue-500" />
                )}
                <span className="text-xl flex-shrink-0" aria-hidden>
                  {typeEmoji(n.type)}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="text-gray-900">{n.message}</p>
                  <p className="text-sm text-gray-500 mt-1">
                    {formatDate(n.createdAt)}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  )
}
