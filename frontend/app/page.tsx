'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import { medicineAPI } from '@/lib/api'

interface Stats {
  total: number
  expired: number
  expiringSoon: number
  safe: number
}

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    medicineAPI
      .getStats()
      .then((res) => setStats(res.data))
      .catch(() => setStats({ total: 0, expired: 0, expiringSoon: 0, safe: 0 }))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <main className="max-w-7xl mx-auto px-4 py-8">
        <p className="text-gray-500">Loading...</p>
      </main>
    )
  }

  const s = stats ?? { total: 0, expired: 0, expiringSoon: 0, safe: 0 }

  return (
    <main className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-10">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h2 className="text-sm font-medium text-gray-500">Total Medicines</h2>
          <p className="text-2xl font-bold text-gray-900 mt-1">{s.total}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border-l-4 border-l-green-500 border border-gray-200 p-4">
          <h2 className="text-sm font-medium text-gray-500">Safe</h2>
          <p className="text-2xl font-bold text-gray-900 mt-1">{s.safe}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border-l-4 border-l-yellow-500 border border-gray-200 p-4">
          <h2 className="text-sm font-medium text-gray-500">Expiring Soon</h2>
          <p className="text-2xl font-bold text-gray-900 mt-1">{s.expiringSoon}</p>
        </div>
        <div className="bg-white rounded-lg shadow-sm border-l-4 border-l-red-500 border border-gray-200 p-4">
          <h2 className="text-sm font-medium text-gray-500">Expired</h2>
          <p className="text-2xl font-bold text-gray-900 mt-1">{s.expired}</p>
        </div>
      </div>

      <section>
        <h2 className="text-lg font-semibold text-gray-900 mb-3">Quick Actions</h2>
        <div className="flex flex-wrap gap-3">
          <Link
            href="/medicines/add"
            className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
          >
            + Add Medicine
          </Link>
          <Link
            href="/medicines"
            className="inline-flex items-center px-4 py-2 bg-white border border-gray-300 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-50"
          >
            View All Medicines
          </Link>
          <Link
            href="/alerts"
            className="inline-flex items-center px-4 py-2 bg-white border border-gray-300 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-50"
          >
            View Alerts
          </Link>
        </div>
      </section>
    </main>
  )
}
