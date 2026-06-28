"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Package, TrendingUp, DollarSign, LogOut } from "lucide-react";

export default function Dashboard() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState<any>(null);
  
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalSales: 0,
    revenue: 0
  });

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
      // Fetch products count
      const { count: productsCount } = await supabase
        .from("products")
        .select("*", { count: "exact", head: true })
        .eq("user_id", userId);

      // Fetch today's sales (simplified for now, fetching all incomes)
      const { data: finances } = await supabase
        .from("finances")
        .select("amount")
        .eq("user_id", userId)
        .eq("type", "INCOME");

      const revenue = finances?.reduce((acc, curr) => acc + curr.amount, 0) || 0;
      const totalSales = finances?.length || 0;

      setStats({
        totalProducts: productsCount || 0,
        totalSales: totalSales,
        revenue: revenue
      });
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSignOut = async () => {
    await supabase.auth.signOut();
    router.push("/");
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-gray-50 text-gray-500">Cargando dashboard...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold text-gray-900">Mi Negocio</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-sm text-gray-500">{user?.email}</span>
              <button
                onClick={handleSignOut}
                className="p-2 text-gray-400 hover:text-gray-600 rounded-full hover:bg-gray-100 transition-colors"
                title="Cerrar sesión"
              >
                <LogOut className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Resumen General</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <div className="flex items-center gap-4 mb-4">
              <div className="p-3 bg-blue-50 text-blue-600 rounded-xl">
                <Package className="h-6 w-6" />
              </div>
              <h3 className="text-lg font-semibold text-gray-700">Productos</h3>
            </div>
            <p className="text-3xl font-bold text-gray-900">{stats.totalProducts}</p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <div className="flex items-center gap-4 mb-4">
              <div className="p-3 bg-green-50 text-green-600 rounded-xl">
                <TrendingUp className="h-6 w-6" />
              </div>
              <h3 className="text-lg font-semibold text-gray-700">Ventas Registradas</h3>
            </div>
            <p className="text-3xl font-bold text-gray-900">{stats.totalSales}</p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <div className="flex items-center gap-4 mb-4">
              <div className="p-3 bg-yellow-50 text-yellow-600 rounded-xl">
                <DollarSign className="h-6 w-6" />
              </div>
              <h3 className="text-lg font-semibold text-gray-700">Ingresos Totales</h3>
            </div>
            <p className="text-3xl font-bold text-gray-900">Q {stats.revenue.toFixed(2)}</p>
          </div>
        </div>
        
        <div className="mt-8 bg-blue-50 border border-blue-100 rounded-2xl p-6 flex flex-col md:flex-row items-center justify-between">
          <div>
            <h3 className="text-lg font-bold text-blue-900">Actualiza a PRO</h3>
            <p className="text-blue-700 mt-1">Obtén reportes avanzados, múltiples usuarios y más.</p>
          </div>
          <button onClick={() => router.push('/pricing')} className="mt-4 md:mt-0 bg-blue-600 text-white px-6 py-2 rounded-xl font-medium hover:bg-blue-700 transition-colors">
            Ver Planes
          </button>
        </div>
      </main>
    </div>
  );
}
