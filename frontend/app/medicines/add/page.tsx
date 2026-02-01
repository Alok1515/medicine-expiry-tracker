'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useState } from 'react'
import { medicineAPI } from '@/lib/api'

const CATEGORIES = [
  'Pain Relief',
  'Antibiotic',
  'Vitamin',
  'Allergy',
  'Digestive',
  'Cold & Flu',
  'Other',
]

export default function AddMedicinePage() {
  const router = useRouter()
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [form, setForm] = useState({
    name: '',
    manufacturer: '',
    batchNumber: '',
    category: '',
    dosage: '',
    purchaseDate: '',
    expiryDate: '',
    quantity: 1,
    notes: '',
  })

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: name === 'quantity' ? Math.max(1, parseInt(value, 10) || 1) : value,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    medicineAPI
      .add({
        name: form.name,
        manufacturer: form.manufacturer || undefined,
        batchNumber: form.batchNumber || undefined,
        category: form.category || undefined,
        dosage: form.dosage || undefined,
        purchaseDate: form.purchaseDate || undefined,
        expiryDate: form.expiryDate,
        quantity: form.quantity,
        notes: form.notes || undefined,
      })
      .then(() => router.push('/medicines'))
      .catch((err) => {
        setError(err.response?.data?.error ?? 'Failed to add medicine')
      })
      .finally(() => setSubmitting(false))
  }

  return (
    <main className="max-w-2xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Add Medicine</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div className="rounded-md bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
            Name *
          </label>
          <input
            id="name"
            name="name"
            type="text"
            required
            value={form.name}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div>
          <label htmlFor="manufacturer" className="block text-sm font-medium text-gray-700 mb-1">
            Manufacturer
          </label>
          <input
            id="manufacturer"
            name="manufacturer"
            type="text"
            value={form.manufacturer}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div>
          <label htmlFor="batchNumber" className="block text-sm font-medium text-gray-700 mb-1">
            Batch Number
          </label>
          <input
            id="batchNumber"
            name="batchNumber"
            type="text"
            value={form.batchNumber}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div>
          <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">
            Category
          </label>
          <select
            id="category"
            name="category"
            value={form.category}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          >
            <option value="">Select category</option>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="dosage" className="block text-sm font-medium text-gray-700 mb-1">
            Dosage
          </label>
          <input
            id="dosage"
            name="dosage"
            type="text"
            value={form.dosage}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="purchaseDate" className="block text-sm font-medium text-gray-700 mb-1">
              Purchase Date
            </label>
            <input
              id="purchaseDate"
              name="purchaseDate"
              type="date"
              value={form.purchaseDate}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
          <div>
            <label htmlFor="expiryDate" className="block text-sm font-medium text-gray-700 mb-1">
              Expiry Date *
            </label>
            <input
              id="expiryDate"
              name="expiryDate"
              type="date"
              required
              value={form.expiryDate}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
        </div>

        <div>
          <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-1">
            Quantity (min 1) *
          </label>
          <input
            id="quantity"
            name="quantity"
            type="number"
            min={1}
            required
            value={form.quantity}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div>
          <label htmlFor="notes" className="block text-sm font-medium text-gray-700 mb-1">
            Notes
          </label>
          <textarea
            id="notes"
            name="notes"
            rows={3}
            value={form.notes}
            onChange={handleChange}
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div className="flex gap-3 pt-2">
          <button
            type="submit"
            disabled={submitting}
            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            {submitting ? 'Adding...' : 'Add Medicine'}
          </button>
          <Link
            href="/medicines"
            className="px-4 py-2 text-gray-700 text-sm font-medium hover:text-gray-900"
          >
            Cancel
          </Link>
        </div>
      </form>
    </main>
  )
}
