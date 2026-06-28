"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";

export default function Settings() {
  const router = useRouter();
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUser(session.user);
        setLoading(false);
      }
    };
    checkAuth();
  }, [router]);

  const handleSignOut = async () => {
    await supabase.auth.signOut();
    router.push("/");
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-gray-50">Cargando...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar />
      <main className="flex-1 p-8 overflow-y-auto">
        <h2 className="text-3xl font-bold text-gray-900 mb-8">Ajustes</h2>
        
        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-3xl space-y-8">
          <div>
            <h3 className="text-xl font-bold text-gray-900 mb-4">Información de la Cuenta</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Correo Electrónico</label>
                <div className="text-gray-900 font-medium px-4 py-3 bg-gray-50 rounded-xl border border-gray-200">
                  {user?.email}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">ID de Usuario</label>
                <div className="text-gray-900 font-medium px-4 py-3 bg-gray-50 rounded-xl border border-gray-200 truncate">
                  {user?.id}
                </div>
              </div>
            </div>
          </div>

          <div className="pt-8 border-t border-gray-100">
            <h3 className="text-xl font-bold text-gray-900 mb-4">Suscripción</h3>
            <div className="bg-blue-50 border border-blue-100 rounded-xl p-6 flex items-center justify-between">
              <div>
                <p className="text-blue-900 font-bold mb-1">Plan Básico</p>
                <p className="text-blue-700 text-sm">Estás en el plan gratuito.</p>
              </div>
              <button 
                onClick={() => router.push("/pricing")}
                className="px-6 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
              >
                Actualizar Plan
              </button>
            </div>
          </div>

          <div className="pt-8 border-t border-gray-100">
            <h3 className="text-xl font-bold text-gray-900 mb-4">Sesión</h3>
            <button 
              onClick={handleSignOut}
              className="px-6 py-3 bg-red-50 text-red-600 font-medium rounded-xl hover:bg-red-100 transition-colors"
            >
              Cerrar Sesión
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
