import React from 'react'
import AppLayout from './components/layout/AppLayout'
import { AuthProvider } from './context/AuthContext'
import { TaskProvider } from './context/TaskContext'

function App() {
  

  return (
    <TaskProvider>
      <AuthProvider>
        <AppLayout />
      </AuthProvider>
    </TaskProvider>
  )
}

export default App
