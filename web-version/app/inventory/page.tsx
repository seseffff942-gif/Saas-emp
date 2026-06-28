"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";
import { Plus, Edit, Trash2 } from "lucide-react";

export default function Inventory() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<any[]>([]);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUserId(session.user.id);
        fetchProducts(session.user.id);
      }
    };
    checkAuth();
  }, [router]);

  const fetchProducts = async (uid: string) => {
    try {
      const { data, error } = await supabase
        .from("products")
        .select("*")
        .eq("user_id", uid)
        .order("id", { ascending: false });

      if (error) throw error;
      setProducts(data || []);
    } catch (error) {
      console.error("Error fetching products:", error);
    } finally {
      setLoading(false);
    }
  };

  const deleteProduct = async (id: number) => {
    if (confirm("¿Estás seguro de eliminar este producto?")) {
      try {
        await supabase.from("products").delete().eq("id", id);
        if (userId) fetchProducts(userId);
      } catch (error) {
        console.error("Error deleting product:", error);
      }
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-gray-50">Cargando inventario...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar />
      <main className="flex-1 p-8 overflow-y-auto">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900">Inventario</h2>
          <button 
            onClick={() => router.push("/add-product")}
            className="bg-blue-600 text-white px-6 py-3 rounded-xl font-medium hover:bg-blue-700 transition-colors shadow-lg shadow-blue-200 flex items-center gap-2"
          >
            <Plus className="h-5 w-5" />
            Agregar Producto
          </button>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          {products.length === 0 ? (
            <div className="p-8 text-center text-gray-500">
              No hay productos registrados. ¡Agrega tu primer producto!
            </div>
          ) : (
            <table className="w-full text-left">
              <thead className="bg-gray-50 text-gray-700">
                <tr>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Nombre</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Categoría</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Precio</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200">Stock</th>
                  <th className="px-6 py-4 font-semibold border-b border-gray-200 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-gray-900">{product.name}</td>
                    <td className="px-6 py-4 text-gray-600">{product.category}</td>
                    <td className="px-6 py-4 text-gray-600">Q {product.price.toFixed(2)}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${product.stock < 10 ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
                        {product.stock}
                      </span>
                    </td>
                    <td className="px-6 py-4 flex justify-end gap-3">
                      <button className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                        <Edit className="h-5 w-5" />
                      </button>
                      <button 
                        onClick={() => deleteProduct(product.id)}
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
