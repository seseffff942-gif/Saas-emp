"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";
import { Plus, Search, Filter, RefreshCcw, Package, AlertTriangle, CheckCircle2 } from "lucide-react";
import Link from "next/link";

export default function Inventory() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<any[]>([]);
  const [userId, setUserId] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
        setUserId(session.user.id);
        setUser(session.user);
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

  const filteredProducts = products.filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()));

  const totalStock = products.reduce((acc, p) => acc + (p.stock || 0), 0);
  const totalValue = products.reduce((acc, p) => acc + ((p.stock || 0) * (p.price || 0)), 0);
  const criticalStock = products.filter(p => p.stock < 10).length;

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-slate-50 text-slate-500">Cargando inventario...</div>;
  }

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-4 flex justify-between items-center sticky top-0 z-10">
          <div>
            <h2 className="text-xl font-bold text-slate-800">Catálogo de Productos</h2>
            <p className="text-sm text-slate-500">Presentación y especificaciones técnicas oficiales para asesorar correctamente a cada productor.</p>
          </div>
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

        <div className="p-8 max-w-7xl mx-auto">
          {/* Stats Row */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-white rounded-2xl p-6 border border-slate-200 flex justify-between items-center shadow-sm">
              <div>
                <p className="text-sm font-bold text-slate-500 mb-1">CATÁLOGO ACTIVO</p>
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-black text-slate-800">{products.length}</span>
                </div>
              </div>
              <div className="bg-emerald-50 p-4 rounded-xl text-emerald-600">
                <CheckCircle2 className="w-8 h-8" />
              </div>
            </div>
            
            <div className="bg-white rounded-2xl p-6 border border-slate-200 flex justify-between items-center shadow-sm">
              <div>
                <p className="text-sm font-bold text-slate-500 mb-1">TOTAL EN BODEGA</p>
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-black text-slate-800">{totalStock}</span>
                  <span className="text-sm font-bold text-slate-400">unids</span>
                </div>
                <p className="text-xs font-bold text-emerald-600 mt-1">Q {totalValue.toFixed(2)} VALOR</p>
              </div>
              <div className="bg-blue-50 p-4 rounded-xl text-blue-600">
                <Package className="w-8 h-8" />
              </div>
            </div>

            <div className="bg-white rounded-2xl p-6 border border-slate-200 flex justify-between items-center shadow-sm relative overflow-hidden">
              <div className="absolute top-0 right-0 p-2">
                 <span className="bg-rose-100 text-rose-700 text-[10px] font-bold px-2 py-0.5 rounded-full">VER TODO</span>
              </div>
              <div>
                <p className="text-sm font-bold text-slate-500 mb-1">STOCK CRÍTICO O AGOTADO</p>
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-black text-slate-800">{criticalStock}</span>
                </div>
              </div>
              <div className="bg-rose-50 p-4 rounded-xl text-rose-600">
                <AlertTriangle className="w-8 h-8" />
              </div>
            </div>
          </div>

          {/* Action Bar */}
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <div className="relative flex-1">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-slate-400" />
              </div>
              <input
                type="text"
                placeholder="Buscar insumo, marca o SKU..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-11 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none transition-shadow text-slate-800 font-medium"
              />
            </div>
            
            <div className="flex gap-2">
              <button className="flex items-center gap-2 px-4 py-3 bg-white border border-slate-200 rounded-xl text-slate-700 font-bold hover:bg-slate-50 transition-colors">
                 <Package className="w-5 h-5 text-slate-400" /> MOD.
              </button>
              <button className="flex items-center gap-2 px-4 py-3 bg-white border border-slate-200 rounded-xl text-slate-700 font-bold hover:bg-slate-50 transition-colors">
                 <Filter className="w-5 h-5 text-slate-400" /> LIST.
              </button>
              <button className="flex items-center gap-2 px-4 py-3 bg-white border border-slate-200 rounded-xl text-slate-700 font-bold hover:bg-slate-50 transition-colors">
                 <Filter className="w-5 h-5 text-slate-400" /> VAL.
              </button>
              <button onClick={() => fetchProducts(userId!)} className="flex items-center gap-2 px-4 py-3 bg-white border border-slate-200 rounded-xl text-slate-700 font-bold hover:bg-slate-50 transition-colors">
                 <RefreshCcw className="w-5 h-5 text-slate-400" /> REFRESCAR
              </button>
              <Link href="/add-product" className="flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-xl font-bold hover:bg-emerald-700 transition-colors shadow-lg shadow-emerald-200">
                <Plus className="w-5 h-5" /> NUEVO PRODUCTO
              </Link>
            </div>
          </div>
          
          {/* Categories */}
          <div className="flex gap-3 overflow-x-auto pb-4 mb-4 scrollbar-hide">
            <button className="flex items-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-full font-bold whitespace-nowrap">
               Todos <span className="bg-white/20 px-2 py-0.5 rounded-full text-xs">{products.length}</span>
            </button>
            <button className="flex items-center gap-2 px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-full font-bold whitespace-nowrap hover:bg-slate-50">
               <span className="w-2 h-2 rounded-full bg-blue-500"></span> Veterinaria <span className="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-xs">0</span>
            </button>
            <button className="flex items-center gap-2 px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-full font-bold whitespace-nowrap hover:bg-slate-50">
               <span className="w-2 h-2 rounded-full bg-emerald-500"></span> Agroquímicos <span className="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-xs">0</span>
            </button>
            <button className="flex items-center gap-2 px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-full font-bold whitespace-nowrap hover:bg-slate-50">
               <span className="w-2 h-2 rounded-full bg-yellow-500"></span> Semillas <span className="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-xs">0</span>
            </button>
             <button className="flex items-center gap-2 px-4 py-2 bg-white text-slate-600 border border-slate-200 rounded-full font-bold whitespace-nowrap hover:bg-slate-50">
               <span className="w-2 h-2 rounded-full bg-orange-500"></span> Herramientas <span className="bg-slate-100 text-slate-500 px-2 py-0.5 rounded-full text-xs">0</span>
            </button>
          </div>

          {/* Product Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredProducts.map(product => (
              <div key={product.id} className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition-shadow flex flex-col">
                <div className="p-4 border-b border-slate-100 flex justify-between items-start">
                  <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">{product.category || 'SIN CATEGORÍA'}</span>
                  <button onClick={() => deleteProduct(product.id)} className="text-rose-400 hover:text-rose-600 bg-rose-50 hover:bg-rose-100 p-1.5 rounded-md transition-colors text-xs font-bold">
                    Eliminar
                  </button>
                </div>
                
                <div className="p-6 flex flex-col items-center flex-1">
                  <div className="w-24 h-24 bg-slate-100 rounded-xl mb-4 flex items-center justify-center text-slate-400">
                    {/* Placeholder for image */}
                    <Package className="w-10 h-10" />
                  </div>
                  <h3 className="font-bold text-slate-800 text-center mb-2 line-clamp-2">{product.name}</h3>
                  <div className="text-2xl font-black text-emerald-600 mb-4">Q {product.price.toFixed(2)}</div>
                  
                  <div className="w-full bg-slate-50 rounded-lg p-3 text-center mb-4">
                    <p className="text-xs font-bold text-slate-500 mb-1">DISPONIBILIDAD</p>
                    <div className="flex justify-between items-center">
                      <div className="flex-1 h-2 bg-slate-200 rounded-full overflow-hidden mr-2">
                        <div className={`h-full rounded-full ${product.stock < 10 ? 'bg-rose-500' : 'bg-emerald-500'}`} style={{width: `${Math.min((product.stock / 100) * 100, 100)}%`}}></div>
                      </div>
                      <span className={`text-sm font-bold ${product.stock < 10 ? 'text-rose-600' : 'text-slate-700'}`}>{product.stock} unidades</span>
                    </div>
                  </div>
                </div>
                
                <div className="bg-slate-50 p-4 border-t border-slate-200">
                   <p className="text-xs font-bold text-slate-500 text-center mb-3">CONTROLES ADMINISTRATIVOS</p>
                   <div className="flex justify-center gap-2">
                     <button className="w-10 h-10 bg-white border border-slate-200 rounded-lg flex flex-col items-center justify-center text-slate-500 hover:text-emerald-600 hover:border-emerald-300 transition-colors">
                       <span className="text-[10px] font-bold">Stock</span>
                     </button>
                     <button className="w-10 h-10 bg-white border border-slate-200 rounded-lg flex flex-col items-center justify-center text-slate-500 hover:text-emerald-600 hover:border-emerald-300 transition-colors">
                       <span className="text-[10px] font-bold">Precio</span>
                     </button>
                      <button className="w-10 h-10 bg-white border border-slate-200 rounded-lg flex flex-col items-center justify-center text-slate-500 hover:text-emerald-600 hover:border-emerald-300 transition-colors">
                       <span className="text-[10px] font-bold">Variantes</span>
                     </button>
                     <button className="w-10 h-10 bg-white border border-slate-200 rounded-lg flex flex-col items-center justify-center text-slate-500 hover:text-emerald-600 hover:border-emerald-300 transition-colors">
                       <span className="text-[10px] font-bold">Especific.</span>
                     </button>
                   </div>
                   <button className="w-full mt-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-bold text-slate-600 hover:bg-slate-100 transition-colors">
                     Consultar Ficha Técnica
                   </button>
                </div>
              </div>
            ))}
          </div>

          {filteredProducts.length === 0 && (
            <div className="text-center py-12 text-slate-500 font-medium">
              No se encontraron productos.
            </div>
          )}

        </div>
      </main>
    </div>
  );
}
