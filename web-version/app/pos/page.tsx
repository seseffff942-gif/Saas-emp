"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Sidebar } from "@/components/Sidebar";
import { Search, ShoppingCart, Plus, Minus, Trash2 } from "lucide-react";

export default function POS() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<any[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [cart, setCart] = useState<any[]>([]);
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const checkAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (!session) {
        router.push("/");
      } else {
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

  const addToCart = (product: any) => {
    setCart(prev => {
      const existing = prev.find(item => item.id === product.id);
      if (existing) {
        return prev.map(item => item.id === product.id ? { ...item, qty: item.qty + 1 } : item);
      }
      return [...prev, { ...product, qty: 1 }];
    });
  };

  const removeFromCart = (id: number) => {
    setCart(prev => prev.filter(item => item.id !== id));
  };

  const updateQty = (id: number, delta: number) => {
    setCart(prev => prev.map(item => {
      if (item.id === id) {
        const newQty = Math.max(1, item.qty + delta);
        return { ...item, qty: newQty };
      }
      return item;
    }));
  };

  const processSale = async (type: 'CASH' | 'CREDIT') => {
    if (cart.length === 0) return;
    try {
      const total = cart.reduce((acc, item) => acc + (item.price * item.qty), 0);
      
      const { error } = await supabase.from("finance_logs").insert({
        user_id: user.id,
        title: `Venta en ${type === 'CASH' ? 'Efectivo' : 'Crédito'}`,
        amount: total,
        category: 'Sales',
        type: 'INCOME',
        timestamp: Date.now()
      });

      if (error) throw error;
      
      alert(`Venta por Q ${total.toFixed(2)} registrada exitosamente.`);
      setCart([]);
    } catch (e) {
      console.error(e);
      alert("Error procesando venta.");
    }
  };

  const filteredProducts = products.filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()));
  const cartTotal = cart.reduce((acc, item) => acc + (item.price * item.qty), 0);

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center bg-slate-50 text-slate-500">Cargando catálogo...</div>;
  }

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 flex overflow-hidden">
        
        {/* Left Side: Products */}
        <div className="flex-1 flex flex-col overflow-hidden border-r border-slate-200 bg-white">
          <header className="px-8 py-4 border-b border-slate-200">
            <h2 className="text-xl font-bold text-slate-800">Catálogo de Productos</h2>
            <p className="text-sm text-slate-500">Selecciona insumos veterinarios y agrícolas para procesar la venta</p>
            
            <div className="mt-4 relative">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-slate-400" />
              </div>
              <input
                type="text"
                placeholder="Buscar por código SKU, nombre de producto..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-11 w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none text-slate-800 font-medium"
              />
            </div>
          </header>
          
          <div className="flex-1 overflow-y-auto p-6 bg-slate-50/50">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredProducts.map(product => (
                <div key={product.id} className="bg-white rounded-2xl border border-slate-200 p-4 flex flex-col items-center text-center hover:shadow-md transition-shadow relative">
                   <div className="absolute top-3 left-3 bg-slate-100 text-slate-600 text-[10px] font-bold px-2 py-0.5 rounded-md">
                     DISPONIBLES: {product.stock}
                   </div>
                   <div className="w-16 h-16 bg-slate-50 rounded-xl mt-4 mb-3 flex items-center justify-center overflow-hidden relative">
                     {product.image_uri ? (
                       <img src={product.image_uri} alt={product.name} className="w-full h-full object-cover" />
                     ) : (
                       <ShoppingCart className="w-8 h-8 text-slate-300" />
                     )}
                   </div>
                   <p className="text-xs font-bold text-slate-400 uppercase">{product.category}</p>
                   <h3 className="font-bold text-slate-800 text-sm line-clamp-2 mt-1 mb-2">{product.name}</h3>
                   <div className="flex items-center justify-between w-full mt-auto pt-3 border-t border-slate-100">
                     <span className="font-black text-emerald-600">Q {product.price.toFixed(2)}</span>
                     <button 
                       onClick={() => addToCart(product)}
                       className="bg-emerald-500 text-white w-8 h-8 rounded-full flex items-center justify-center hover:bg-emerald-600 transition-colors"
                     >
                       <Plus className="w-5 h-5" />
                     </button>
                   </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Side: Cart */}
        <div className="w-96 flex flex-col bg-white">
          <header className="px-6 py-4 border-b border-slate-200 flex items-center gap-3">
             <ShoppingCart className="w-6 h-6 text-slate-700" />
            <h2 className="text-xl font-bold text-slate-800">CARRITO DE COMPRA</h2>
          </header>

          <div className="p-6 border-b border-slate-200 bg-slate-50/50">
             <p className="text-xs font-bold text-slate-500 mb-2">👤 INFORMACIÓN DEL CLIENTE</p>
             <input type="text" placeholder="Buscar o registrar cliente..." className="w-full px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm mb-3 focus:outline-none focus:border-emerald-500" />
             <div className="grid grid-cols-2 gap-3">
               <input type="text" placeholder="NIT / C/F" className="w-full px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-emerald-500" />
               <input type="text" placeholder="Teléfono" className="w-full px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-emerald-500" />
             </div>
          </div>

          <div className="flex-1 overflow-y-auto p-4">
            {cart.length === 0 ? (
              <div className="h-full flex items-center justify-center text-slate-400 text-sm">
                El carrito está vacío
              </div>
            ) : (
              <div className="space-y-4">
                {cart.map(item => (
                  <div key={item.id} className="flex gap-3 border-b border-slate-100 pb-4">
                    <div className="w-12 h-12 bg-slate-100 rounded-lg flex-shrink-0 overflow-hidden">
                      {item.image_uri ? (
                        <img src={item.image_uri} alt={item.name} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center">
                           <ShoppingCart className="w-5 h-5 text-slate-300" />
                        </div>
                      )}
                    </div>
                    <div className="flex-1">
                      <h4 className="text-sm font-bold text-slate-800 line-clamp-1">{item.name}</h4>
                      <p className="text-xs font-bold text-emerald-600">Q {item.price.toFixed(2)}</p>
                      <div className="flex items-center gap-3 mt-2">
                        <button onClick={() => updateQty(item.id, -1)} className="w-6 h-6 bg-slate-100 rounded flex items-center justify-center hover:bg-slate-200 text-slate-600"><Minus className="w-3 h-3" /></button>
                        <span className="text-sm font-bold text-slate-700">{item.qty}</span>
                        <button onClick={() => updateQty(item.id, 1)} className="w-6 h-6 bg-slate-100 rounded flex items-center justify-center hover:bg-slate-200 text-slate-600"><Plus className="w-3 h-3" /></button>
                      </div>
                    </div>
                    <button onClick={() => removeFromCart(item.id)} className="text-rose-400 self-start p-1 hover:bg-rose-50 rounded">
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="p-6 border-t border-slate-200 bg-slate-50">
            <div className="flex justify-between items-center mb-6">
              <span className="font-bold text-slate-600">TOTAL DEL PEDIDO</span>
              <span className="text-3xl font-black text-slate-800">Q {cartTotal.toFixed(2)}</span>
            </div>
            
            <label className="flex items-center gap-2 mb-4 text-sm font-medium text-slate-600 cursor-pointer">
              <input type="checkbox" className="rounded text-emerald-500 focus:ring-emerald-500 border-slate-300 w-4 h-4" />
              Auto-Imprimir Comprobante
            </label>

            <button onClick={() => processSale('CASH')} disabled={cart.length === 0} className="w-full bg-emerald-600 text-white font-bold py-4 rounded-xl hover:bg-emerald-700 transition-colors shadow-lg shadow-emerald-200 disabled:opacity-50 disabled:shadow-none mb-3 flex items-center justify-center gap-2">
              PROCESAR EN EFECTIVO
            </button>
            <button onClick={() => processSale('CREDIT')} disabled={cart.length === 0} className="w-full bg-slate-800 text-white font-bold py-4 rounded-xl hover:bg-slate-900 transition-colors shadow-lg shadow-slate-200 disabled:opacity-50 disabled:shadow-none flex items-center justify-center gap-2">
              REGISTRAR AL CRÉDITO
            </button>
          </div>

        </div>

      </main>
    </div>
  );
}
