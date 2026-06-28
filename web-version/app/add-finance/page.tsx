"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";

export default function AddFinance() {
  const router = useRouter();
  const [userId, setUserId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  
  const [formData, setFormData] = useState({
    title: "",
    amount: "",
    category: "",
    type: "INCOME"
  });

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUserId(session.user.id);
      }
    };
    checkAuth();
  }, [router]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userId) return;
    setLoading(true);
    setError("");

    try {
      const { error: insertError } = await supabase.from("finance_logs").insert({
        user_id: userId,
        title: formData.title,
        amount: parseFloat(formData.amount),
        category: formData.category,
        type: formData.type,
        timestamp: Date.now()
      });

      if (insertError) throw insertError;
      router.push("/finances");
    } catch (err: any) {
      setError(err.message || "Error al agregar registro financiero");
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar />
      <main className="flex-1 p-8 overflow-y-auto">
        <h2 className="text-3xl font-bold text-gray-900 mb-8">Nuevo Registro Financiero</h2>
        
        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-2xl">
          {error && (
            <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm">
              {error}
            </div>
          )}
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Tipo de Transacción</label>
              <div className="flex gap-4">
                <label className="flex-1">
                  <input
                    type="radio"
                    name="type"
                    value="INCOME"
                    checked={formData.type === "INCOME"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <div className={`p-4 text-center rounded-xl border-2 cursor-pointer transition-colors ${formData.type === 'INCOME' ? 'border-green-500 bg-green-50 text-green-700 font-bold' : 'border-gray-200 hover:border-green-200 text-gray-600'}`}>
                    Ingreso
                  </div>
                </label>
                <label className="flex-1">
                  <input
                    type="radio"
                    name="type"
                    value="EXPENSE"
                    checked={formData.type === "EXPENSE"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <div className={`p-4 text-center rounded-xl border-2 cursor-pointer transition-colors ${formData.type === 'EXPENSE' ? 'border-red-500 bg-red-50 text-red-700 font-bold' : 'border-gray-200 hover:border-red-200 text-gray-600'}`}>
                    Gasto
                  </div>
                </label>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Título / Descripción</label>
              <input
                type="text"
                name="title"
                required
                value={formData.title}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-blue-500 focus:border-blue-500 bg-gray-50 outline-none text-gray-900"
                placeholder="Ej. Venta de producto, Pago de luz..."
              />
            </div>
            
            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Monto</label>
                <input
                  type="number"
                  step="0.01"
                  name="amount"
                  required
                  value={formData.amount}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-blue-500 focus:border-blue-500 bg-gray-50 outline-none text-gray-900"
                  placeholder="0.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Categoría</label>
                <select
                  name="category"
                  required
                  value={formData.category}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-blue-500 focus:border-blue-500 bg-gray-50 outline-none text-gray-900"
                >
                  <option value="" disabled>Seleccionar...</option>
                  <option value="Sales">Ventas</option>
                  <option value="Supplier">Proveedor</option>
                  <option value="Utilities">Servicios</option>
                  <option value="Supplies">Insumos</option>
                  <option value="Other">Otro</option>
                </select>
              </div>
            </div>

            <div className="flex justify-end gap-4">
              <button
                type="button"
                onClick={() => router.back()}
                className="px-6 py-3 bg-gray-100 text-gray-700 rounded-xl font-medium hover:bg-gray-200 transition-colors"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-6 py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors shadow-lg shadow-blue-200 disabled:opacity-50"
              >
                {loading ? "Guardando..." : "Guardar Registro"}
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}
