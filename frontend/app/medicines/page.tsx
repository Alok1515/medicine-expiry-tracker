'use client'

import Link from 'next/link'
import { useCallback, useEffect, useState } from 'react'
import { medicineAPI, type Medicine } from '@/lib/api'
import MedicineCard from '@/components/MedicineCard'

export default function MedicinesPage() {
  const [medicines, setMedicines] = useState<Medicine[]>([])
  const [loading, setLoading] = useState(true)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [searching, setSearching] = useState(false)

  const fetchMedicines = useCallback(() => {
    setLoading(true)
    medicineAPI
      .getAll()
      .then((res) => setMedicines(res.data))
      .catch(() => setMedicines([]))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    fetchMedicines()
  }, [fetchMedicines])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchKeyword.trim()) {
      fetchMedicines()
      return
    }
    setSearching(true)
    medicineAPI
      .search(searchKeyword.trim())
      .then((res) => setMedicines(res.data))
      .catch(() => setMedicines([]))
      .finally(() => setSearching(false))
  }

  const handleDelete = (id: string) => {
    if (!confirm('Are you sure you want to delete this medicine?')) return
    medicineAPI
      .delete(id)
      .then(() => fetchMedicines())
      .catch(() => {})
  }

  if (loading && medicines.length === 0) {
    return (
      <main className="max-w-7xl mx-auto px-4 py-8">
        <p className="text-gray-500">Loading...</p>
      </main>
    )
  }

  return (
    <main className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Medicines</h1>
        <Link
          href="/medicines/add"
          className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
        >
          + Add Medicine
        </Link>
      </div>

      <form onSubmit={handleSearch} className="mb-6">
        <div className="flex gap-2">
          <input
            type="text"
            placeholder="Search medicines..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="flex-1 rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          <button
            type="submit"
            disabled={searching}
            className="px-4 py-2 bg-gray-800 text-white text-sm font-medium rounded-md hover:bg-gray-700 disabled:opacity-50"
          >
            Search
          </button>
        </div>
      </form>

      {medicines.length === 0 ? (
        <p className="text-center text-gray-500 py-12">No medicines found</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {medicines.map((medicine) => (
            <MedicineCard
              key={medicine.id}
              medicine={medicine}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </main>
  )
}
