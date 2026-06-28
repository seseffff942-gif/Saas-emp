"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";
import { Search, History, Calendar, CheckCircle, Clock } from "lucide-react";

export default function Finances() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [logs, setLogs] = useState<any[]>([]);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUserId(session.user.id);
        fetchLogs(session.user.id);
      }
    };
    checkAuth();
  }, [router]);

  const fetchLogs = async (uid: string) => {
    try {
      const { data, error } = await supabase
        .from("finance_logs")
        .select("*")
        .eq("user_id", uid)
        .order("id", { ascending: false });

      if (error) throw error;
      setLogs(data || []);
    } catch (error) {
      console.error("Error fetching finance logs:", error);
    } finally {
      setLoading(false);
    }
  };

  const totalDirectSales = logs.filter(l => l.type === 'INCOME').reduce((acc, log) => acc + log.amount, 0);
  const totalCollected = logs.filter(l => l.type === 'INCOME').reduce((acc, log) => acc + log.amount, 0); // Simplified
  const pendingPortfolio = logs.filter(l => l.type === 'EXPENSE').reduce((acc, log) => acc + log.amount, 0); // Mock for pending

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-slate-50 text-slate-500">Cargando facturación...</div>;
  }

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-6 flex justify-between items-center sticky top-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-slate-800 flex items-center gap-2">
              <span className="bg-slate-100 p-2 rounded-lg"><History className="w-5 h-5 text-slate-600" /></span>
              Facturación & Créditos
            </h2>
            <p className="text-sm text-slate-500 mt-1 ml-10">Gestión avanzada de cuentas por cobrar, abonos y folios</p>
          </div>
          
          <div className="flex gap-4">
            <div className="bg-white px-4 py-2 border border-slate-200 rounded-xl">
              <p className="text-[10px] font-bold text-slate-400">VENTA DIRECTA</p>
              <p className="font-bold text-slate-800">Q {totalDirectSales.toFixed(2)}</p>
            </div>
            <div className="bg-white px-4 py-2 border border-slate-200 rounded-xl">
              <p className="text-[10px] font-bold text-emerald-500">TOTAL COBRADO</p>
              <p className="font-bold text-emerald-600">Q {totalCollected.toFixed(2)}</p>
            </div>
             <div className="bg-rose-50 px-4 py-2 border border-rose-100 rounded-xl">
              <p className="text-[10px] font-bold text-rose-500">CARTERA PENDIENTE</p>
              <p className="font-bold text-rose-600">Q {pendingPortfolio.toFixed(2)}</p>
            </div>
          </div>
        </header>

        <div className="p-8 max-w-6xl mx-auto">
          {/* Controls */}
          <div className="flex gap-4 mb-6 items-center">
            <div className="relative flex-1 max-w-md">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-slate-400" />
              </div>
              <input
                type="text"
                placeholder="Buscar por cliente, vendedor o ID..."
                className="pl-11 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none font-medium"
              />
            </div>
            
            <div className="flex items-center gap-2 ml-auto">
              <label className="flex items-center gap-2 text-sm font-medium text-slate-600 mr-4 cursor-pointer">
                <input type="checkbox" className="rounded text-emerald-500 focus:ring-emerald-500 w-4 h-4" />
                Mostrar Anulados/Rechazados
              </label>
              
              <button className="px-4 py-2 bg-emerald-50 text-emerald-600 border border-emerald-200 rounded-lg font-bold text-sm">
                Por Día
              </button>
              <button className="px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-lg font-bold text-sm flex items-center gap-2">
                <History className="w-4 h-4" /> Historial
              </button>
              <button className="px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-lg font-bold text-sm flex items-center gap-2">
                <Calendar className="w-4 h-4" /> {new Date().toLocaleDateString('es-ES')}
              </button>
            </div>
          </div>

          <div className="flex justify-between items-center mb-4">
             <p className="text-sm font-medium text-slate-500">Mostrando <span className="font-bold text-slate-800">{logs.length}</span> facturas filtradas</p>
          </div>

          {/* List */}
          <div className="space-y-4">
            {logs.length === 0 ? (
              <div className="bg-white p-12 text-center rounded-2xl border border-slate-200 text-slate-500">
                No hay facturas registradas.
              </div>
            ) : (
              logs.map((log) => (
                <div key={log.id} className="bg-white p-6 rounded-2xl border border-slate-200 flex items-center justify-between shadow-sm hover:shadow-md transition-shadow">
                  <div className="flex-1">
                     <div className="flex items-center gap-3 mb-2">
                       <h3 className="font-bold text-slate-800 text-lg">{log.title || 'Cliente General'}</h3>
                       <span className="text-[10px] bg-slate-100 text-slate-600 px-2 py-0.5 rounded font-bold border border-slate-200">FOLIO #00{log.id}</span>
                     </div>
                     <div className="flex items-center gap-4 text-xs font-medium text-slate-500">
                       <span className="flex items-center gap-1"><Calendar className="w-3 h-3" /> {new Date(log.timestamp).toLocaleDateString()}</span>
                       <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {new Date(log.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
                       <span>Vendedor: Administrador</span>
                     </div>
                  </div>
                  
                  <div className="flex items-center gap-8">
                    <div className="text-center">
                      <p className="text-[10px] font-bold text-slate-400 mb-1">MONTO</p>
                      <p className="font-bold text-slate-800">Q {log.amount.toFixed(2)}</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[10px] font-bold text-emerald-500 mb-1">ABONADO</p>
                      <p className="font-bold text-emerald-600">Q {log.type === 'INCOME' ? log.amount.toFixed(2) : '0.00'}</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[10px] font-bold text-rose-400 mb-1">RESTANTE</p>
                      <p className="font-bold text-rose-500">Q {log.type === 'EXPENSE' ? log.amount.toFixed(2) : '0.00'}</p>
                    </div>
                    
                    <div className="w-32 flex flex-col items-center">
                      {log.type === 'INCOME' ? (
                        <>
                          <div className="text-emerald-500 bg-emerald-50 border border-emerald-200 px-3 py-1 rounded-full text-xs font-bold w-full text-center flex items-center justify-center gap-1 mb-2">
                            <CheckCircle className="w-3 h-3" /> Enviada
                          </div>
                          <span className="text-[10px] font-bold text-slate-400">Cancelado</span>
                        </>
                      ) : (
                        <>
                          <div className="text-amber-600 bg-amber-50 border border-amber-200 px-3 py-1 rounded-full text-xs font-bold w-full text-center mb-2">
                            Pendiente
                          </div>
                          <button className="text-[10px] font-bold bg-slate-100 text-slate-600 px-2 py-1 rounded w-full hover:bg-slate-200">
                            Marcar Enviado
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
