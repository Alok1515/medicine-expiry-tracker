interface ExpiryBadgeProps {
  expiryDate: string
}

function getDaysLeft(expiryDate: string): number {
  const expiry = new Date(expiryDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  expiry.setHours(0, 0, 0, 0)
  return Math.round((expiry.getTime() - today.getTime()) / (24 * 60 * 60 * 1000))
}

export default function ExpiryBadge({ expiryDate }: ExpiryBadgeProps) {
  const daysLeft = getDaysLeft(expiryDate)

  let label: string
  let bgClass: string

  if (daysLeft < 0) {
    label = `Expired ${Math.abs(daysLeft)} days ago`
    bgClass = 'bg-red-500 text-white'
  } else if (daysLeft === 0) {
    label = 'Expires Today'
    bgClass = 'bg-red-500 text-white'
  } else if (daysLeft <= 7) {
    label = `Expires in ${daysLeft} days`
    bgClass = 'bg-orange-500 text-white'
  } else if (daysLeft <= 30) {
    label = `Expires in ${daysLeft} days`
    bgClass = 'bg-yellow-500 text-white'
  } else {
    label = `Expires in ${daysLeft} days`
    bgClass = 'bg-green-500 text-white'
  }

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${bgClass}`}
    >
      {label}
    </span>
  )
}
