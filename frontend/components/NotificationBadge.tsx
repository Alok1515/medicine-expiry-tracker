'use client'

import { useWebSocket } from './WebSocketProvider'
import { useEffect, useState } from 'react'
import { notificationAPI } from '@/lib/api'

export function NotificationBadge() {
  const { unreadCount: socketUnreadCount, notifications } = useWebSocket()
  const [initialUnreadCount, setInitialUnreadCount] = useState(0)

  useEffect(() => {
    notificationAPI.getUnreadCount()
      .then(res => setInitialUnreadCount(res.data.unreadCount))
      .catch(() => setInitialUnreadCount(0))
  }, [])

  // The total unread count is the initial count plus any new notifications received via websocket
  // that haven't been accounted for yet.
  // Actually, a simpler way is to just use the socketUnreadCount if we fetch initial ones into the provider.
  // But for now, let's just show the socket count if it's > 0, otherwise show the initial count.
  const displayCount = initialUnreadCount + socketUnreadCount

  if (displayCount === 0) return null

  return (
    <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
      {displayCount > 9 ? '9+' : displayCount}
    </span>
  )
}
