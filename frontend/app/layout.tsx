import type { Metadata } from 'next'
import Link from 'next/link'
import './globals.css'
import { ClientProviders } from '@/components/ClientProviders'
import { NotificationBadge } from '@/components/NotificationBadge'

export const metadata: Metadata = {
  title: 'Medicine Expiry Tracker',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>
        <ClientProviders>
          <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow-sm border-b border-gray-200">
              <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-14 items-center">
                  <Link href="/" className="text-xl font-semibold text-gray-800">
                    💊 MedTracker
                  </Link>
                  <div className="flex gap-6">
                    <Link href="/" className="text-gray-600 hover:text-gray-900">
                      Dashboard
                    </Link>
                    <Link href="/medicines" className="text-gray-600 hover:text-gray-900">
                      Medicines
                    </Link>
                    <Link href="/alerts" className="relative text-gray-600 hover:text-gray-900">
                      Alerts
                      <NotificationBadge />
                    </Link>
                  </div>
                </div>
              </div>
            </nav>
            {children}
          </div>
        </ClientProviders>
      </body>
    </html>
  )
}
