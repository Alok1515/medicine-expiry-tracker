import Link from 'next/link'
import type { Medicine } from '@/lib/api'
import ExpiryBadge from './ExpiryBadge'

interface MedicineCardProps {
  medicine: Medicine
  onDelete: (id: string) => void
}

export default function MedicineCard({ medicine, onDelete }: MedicineCardProps) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <div className="flex justify-between items-start gap-2 mb-3">
        <h3 className="text-lg font-semibold text-gray-900 truncate flex-1">
          {medicine.name}
        </h3>
        <ExpiryBadge expiryDate={medicine.expiryDate} />
      </div>
      <div className="space-y-1 text-sm text-gray-600 mb-4">
        {medicine.manufacturer && (
          <p className="truncate">Manufacturer: {medicine.manufacturer}</p>
        )}
        {medicine.category && (
          <p className="truncate">Category: {medicine.category}</p>
        )}
        {medicine.dosage && (
          <p className="truncate">Dosage: {medicine.dosage}</p>
        )}
        <p>Quantity: {medicine.quantity}</p>
        <p>Expiry: {medicine.expiryDate}</p>
      </div>
      <div className="flex gap-3 pt-2 border-t border-gray-100">
        <Link
          href={`/medicines/edit/${medicine.id}`}
          className="text-sm font-medium text-blue-600 hover:text-blue-800"
        >
          Edit
        </Link>
        <button
          type="button"
          onClick={() => onDelete(medicine.id)}
          className="text-sm font-medium text-red-600 hover:text-red-800"
        >
          Delete
        </button>
      </div>
    </div>
  )
}
