"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";
import { Plus, Trash2, ArrowUpCircle, ArrowDownCircle } from "lucide-react";

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

  const deleteLog = async (id: number) => {
    if (confirm("¿Estás seguro de eliminar este registro?")) {
      try {
        await supabase.from("finance_logs").delete().eq("id", id);
        if (userId) fetchLogs(userId);
      } catch (error) {
        console.error("Error deleting log:", error);
      }
    }
  };

  const balance = logs.reduce((acc, log) => {
    return log.type === "INCOME" ? acc + log.amount : acc - log.amount;
  }, 0);

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-gray-50">Cargando finanzas...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar />
      <main className="flex-1 p-8 overflow-y-auto">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900">Finanzas</h2>
          <button 
            onClick={() => router.push("/add-finance")}
            className="bg-blue-600 text-white px-6 py-3 rounded-xl font-medium hover:bg-blue-700 transition-colors shadow-lg shadow-blue-200 flex items-center gap-2"
          >
            <Plus className="h-5 w-5" />
            Nuevo Registro
          </button>
        </div>

        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 mb-8 inline-block min-w-[300px]">
          <h3 className="text-gray-500 font-medium mb-2">Balance Total</h3>
          <p className={`text-4xl font-bold ${balance >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            Q {balance.toFixed(2)}
          </p>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          {logs.length === 0 ? (
            <div className="p-8 text-center text-gray-500">
              No hay registros financieros.
            </div>
          ) : (
            <table className="w-full text-left">
              <thead className="bg-gray-50 text-gray-700">
                <tr>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Tipo</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Título</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Categoría</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Monto</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      {log.type === "INCOME" ? (
                        <span className="flex items-center gap-2 text-green-600 font-medium">
                          <ArrowUpCircle className="h-5 w-5" /> Ingreso
                        </span>
                      ) : (
                        <span className="flex items-center gap-2 text-red-600 font-medium">
                          <ArrowDownCircle className="h-5 w-5" /> Gasto
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 font-medium text-gray-900">{log.title}</td>
                    <td className="px-6 py-4 text-gray-600">{log.category}</td>
                    <td className={`px-6 py-4 font-bold ${log.type === 'INCOME' ? 'text-green-600' : 'text-red-600'}`}>
                      {log.type === 'INCOME' ? '+' : '-'} Q {log.amount.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 flex justify-end gap-3">
                      <button 
                        onClick={() => deleteLog(log.id)}
                        className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <Trash2 className="h-5 w-5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  );
}
