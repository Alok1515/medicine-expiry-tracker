import axios from 'axios'

const baseURL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api'

const api = axios.create({ baseURL })

api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      if (typeof window !== 'undefined') {
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('userName')
        localStorage.removeItem('userEmail')
        window.location.href = '/auth/login'
      }
    }
    return Promise.reject(error)
  }
)

// Types
export interface MedicinePayload {
  name: string
  manufacturer?: string
  batchNumber?: string
  category?: string
  purchaseDate?: string
  expiryDate: string
  quantity: number
  dosage?: string
  notes?: string
  imageUrl?: string
}

export interface Medicine {
  id: string
  userId: string
  name: string
  manufacturer?: string
  batchNumber?: string
  category?: string
  purchaseDate?: string
  expiryDate: string
  quantity: number
  dosage?: string
  notes?: string
  imageUrl?: string
  createdAt?: string
  updatedAt?: string
}

export interface NotificationItem {
  id: string
  medicineId: string
  medicineName: string
  type: 'EXPIRING_SOON' | 'EXPIRED' | 'DISPOSED'
  message: string
  read: boolean
  createdAt: string
}

// Auth API
export const authAPI = {
  register: (data: { name: string; email: string; password: string }) =>
    api.post('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post('/auth/login', data),
  updatePreferences: (data: { emailNotifications?: boolean }) =>
    api.put('/auth/preferences', data),
}

// Medicine API
export const medicineAPI = {
  add: (data: MedicinePayload) => api.post<Medicine>('/medicines', data),
  getAll: () => api.get<Medicine[]>('/medicines'),
  getOne: (id: string) => api.get<Medicine>(`/medicines/${id}`),
  update: (id: string, data: MedicinePayload) =>
    api.put<Medicine>(`/medicines/${id}`, data),
  delete: (id: string) => api.delete(`/medicines/${id}`),
  search: (keyword: string) =>
    api.get<Medicine[]>('/medicines/search', { params: { keyword } }),
  getStats: () => api.get<{ total: number; expired: number; expiringSoon: number; safe: number }>('/medicines/stats'),
}

// Notification API
export const notificationAPI = {
  getAll: () => api.get<NotificationItem[]>('/notifications'),
  markAsRead: (id: string) => api.put(`/notifications/${id}/read`),
  markAllAsRead: () => api.put('/notifications/read-all'),
  getUnreadCount: () =>
    api.get<{ unreadCount: number }>('/notifications/unread-count'),
}
