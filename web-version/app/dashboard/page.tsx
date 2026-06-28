"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Package, TrendingUp, DollarSign, MapPin, BarChart3 } from "lucide-react";
import { Sidebar } from "@/components/Sidebar";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, LineChart, Line } from 'recharts';

export default function Dashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState<any>(null);
  
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalSales: 0,
    revenue: 0
  });

  const [salesData, setSalesData] = useState<any[]>([]);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUser(session.user);
        fetchData(session.user.id);
      }
    };
    checkAuth();
  }, [router]);

  const fetchData = async (userId: string) => {
    try {
      const { count: productsCount } = await supabase
        .from("products")
        .select("*", { count: "exact", head: true })
        .eq("user_id", userId);

      const { data: finances } = await supabase
        .from("finance_logs")
        .select("amount, timestamp, type")
        .eq("user_id", userId);

      const incomes = finances?.filter(f => f.type === 'INCOME') || [];
      const revenue = incomes.reduce((acc, curr) => acc + curr.amount, 0) || 0;
      
      // Dummy data for charts mixed with real data if available
      const weeklyData = [
        { name: 'Lunes', ventas: 4000 },
        { name: 'Martes', ventas: 3000 },
        { name: 'Miércoles', ventas: 2000 },
        { name: 'Jueves', ventas: 2780 },
        { name: 'Viernes', ventas: 8000 },
        { name: 'Sábado', ventas: 2390 },
        { name: 'Domingo', ventas: 3490 },
      ];

      setSalesData(weeklyData);

      setStats({
        totalProducts: productsCount || 0,
        totalSales: incomes.length,
        revenue: revenue
      });
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-slate-50 text-slate-500">Cargando panel de control...</div>;
  }

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-4 flex justify-between items-center sticky top-0 z-10">
          <h2 className="text-xl font-bold text-slate-800">Panel de Control</h2>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-sm font-semibold text-slate-900">{user?.email}</p>
              <p className="text-xs text-emerald-600 font-medium">Administrador</p>
            </div>
            <div className="h-10 w-10 bg-emerald-100 text-emerald-700 rounded-full flex items-center justify-center font-bold border border-emerald-200">
              {user?.email?.charAt(0).toUpperCase() || 'A'}
            </div>
          </div>
        </header>

        <div className="p-8 max-w-7xl mx-auto space-y-6">
          
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <div className="flex items-center gap-3 mb-6 border-b border-slate-100 pb-4">
              <BarChart3 className="text-slate-400" />
              <h3 className="font-bold text-slate-700">IDENTIFICADOR DE PATRONES Y TEMPORALIDAD</h3>
            </div>
            
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={salesData} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                    <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: '#64748b', fontSize: 12}} />
                    <YAxis axisLine={false} tickLine={false} tick={{fill: '#64748b', fontSize: 12}} />
                    <RechartsTooltip cursor={{fill: '#f1f5f9'}} contentStyle={{borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'}} />
                    <Bar dataKey="ventas" fill="#10b981" radius={[4, 4, 0, 0]} maxBarSize={40} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
              <div className="h-64">
                 <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={salesData} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                    <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: '#64748b', fontSize: 12}} />
                    <YAxis axisLine={false} tickLine={false} tick={{fill: '#64748b', fontSize: 12}} />
                    <RechartsTooltip contentStyle={{borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'}} />
                    <Line type="monotone" dataKey="ventas" stroke="#10b981" strokeWidth={3} dot={{r: 4, fill: '#10b981', strokeWidth: 2, stroke: '#fff'}} activeDot={{r: 6}} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
            
            <div className="mt-6 bg-slate-50 p-4 rounded-xl text-sm text-slate-600 flex gap-3 items-start">
              <div className="mt-0.5 text-slate-400">ℹ️</div>
              <p>El comportamiento de las ventas totales refleja los ciclos y distribución interna. Los picos mensuales suelen coincidir con los abastecimientos programados. Analiza la tendencia diaria para anticipar la carga en bodega, coordinar despachos y asignar personal en los días de mayor movimiento comercial.</p>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
             <div className="flex items-center gap-3 mb-6 pb-4 border-b border-slate-100">
              <MapPin className="text-slate-400" />
              <h3 className="font-bold text-slate-700">Ubicación de Bodega</h3>
            </div>
            <div className="bg-slate-100 rounded-xl h-64 flex flex-col items-center justify-center border border-slate-200 relative overflow-hidden">
               <div className="text-center space-y-4 max-w-sm relative z-10 bg-white/90 p-6 rounded-2xl shadow-lg backdrop-blur-sm border border-white">
                 <p className="text-slate-600 text-sm">Ingresa la contraseña de la bodega para visualizar la ubicación detallada y los puntos de recolección.</p>
                 <input type="password" placeholder="Contraseña" className="w-full px-4 py-2 border border-slate-300 rounded-lg text-center focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500" />
                 <button className="w-full bg-emerald-50 text-emerald-600 font-semibold py-2 rounded-lg border border-emerald-200 hover:bg-emerald-100 transition-colors">
                   Revelar Ubicación
                 </button>
               </div>
               {/* Decorative background map pattern */}
               <div className="absolute inset-0 opacity-10 pointer-events-none" style={{backgroundImage: 'radial-gradient(#94a3b8 2px, transparent 2px)', backgroundSize: '24px 24px'}}></div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
