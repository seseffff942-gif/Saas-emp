import './globals.css'
import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Planes y Precios | SAAS',
  description: 'Suscripción SAAS para tu negocio',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  )
}
