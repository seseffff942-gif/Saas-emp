"use client";

import { Sidebar } from "@/components/Sidebar";
import { Search, Plus, Users, DollarSign, Activity } from "lucide-react";

export default function Clients() {
  const clients = [
    { id: 1, name: "Abraham Cale", alias: "Finca El Ganadero", phone: "4512-1234", debt: 0.00, total: 0.00, init: "AC" },
    { id: 2, name: "Alex Pinto Juárez", alias: "Agroveterinaria San Pedro", phone: "5845-2390", debt: 0.00, total: 0.00, init: "AP" },
    { id: 3, name: "Angel España", alias: "Agro El Chal", phone: "4659-3332", debt: 2201.00, total: 2201.00, init: "AE" },
  ];

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-6 flex justify-between items-center sticky top-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Directorio Premium de Clientes</h2>
            <p className="text-sm text-slate-500 mt-1">Información comercial, asignación de asesores y gestión de facturación.</p>
          </div>
          
          <button className="flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-xl font-bold hover:bg-emerald-700 transition-colors shadow-lg shadow-emerald-200">
            <Plus className="w-5 h-5" /> Nuevo Cliente
          </button>
        </header>

        <div className="p-8 max-w-7xl mx-auto">
          {/* Stats */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
             <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
               <div className="bg-emerald-50 p-3 rounded-xl text-emerald-600"><Users className="w-6 h-6"/></div>
               <div>
                 <p className="text-[10px] font-bold text-slate-400">TOTAL CLIENTES</p>
                 <p className="font-bold text-slate-800 text-xl">59</p>
               </div>
             </div>
             <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
               <div className="bg-blue-50 p-3 rounded-xl text-blue-600"><Activity className="w-6 h-6"/></div>
               <div>
                 <p className="text-[10px] font-bold text-slate-400">VENTAS FACTURADAS</p>
                 <p className="font-bold text-slate-800 text-xl">Q 132,509.55</p>
               </div>
             </div>
             <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
               <div className="bg-rose-50 p-3 rounded-xl text-rose-600"><DollarSign className="w-6 h-6"/></div>
               <div>
                 <p className="text-[10px] font-bold text-rose-500">CARTERA PENDIENTE</p>
                 <p className="font-bold text-rose-600 text-xl">Q 60,204.60</p>
               </div>
             </div>
             <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
               <div className="bg-emerald-50 p-3 rounded-xl text-emerald-600"><DollarSign className="w-6 h-6"/></div>
               <div>
                 <p className="text-[10px] font-bold text-emerald-600">RECAUDADO</p>
                 <p className="font-bold text-emerald-700 text-xl">Q 4,547.00</p>
               </div>
             </div>
          </div>

          <div className="flex gap-4 mb-6">
            <div className="relative flex-1">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-slate-400" />
              </div>
              <input
                type="text"
                placeholder="Buscar por nombre, agro-veterinaria o NIT..."
                className="pl-11 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none font-medium text-slate-700"
              />
            </div>
            <div className="flex items-center gap-3">
               <span className="text-sm font-bold text-slate-400">ORDENAR POR:</span>
               <select className="bg-white border border-slate-200 px-4 py-3 rounded-xl font-bold text-slate-600 outline-none">
                 <option>Nombre</option>
                 <option>Ventas Totales</option>
                 <option>Deuda</option>
               </select>
            </div>
          </div>

          <div className="space-y-4">
            {clients.map(client => (
              <div key={client.id} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-start gap-6 hover:shadow-md transition-shadow">
                <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 font-bold flex items-center justify-center shrink-0">
                  {client.init}
                </div>
                
                <div className="flex-1 grid grid-cols-1 md:grid-cols-4 gap-6">
                   <div className="col-span-2">
                     <h3 className="font-bold text-slate-800 text-lg mb-1">{client.name}</h3>
                     <p className="text-sm text-slate-500 mb-2">{client.alias}</p>
                     
                     <div className="bg-slate-50 p-3 rounded-xl">
                       <p className="text-[10px] font-bold text-slate-400 mb-1">DATOS DEL ESTABLECIMIENTO</p>
                       <p className="text-xs text-slate-600 flex items-center gap-2"><span className="w-3 h-3 bg-slate-200 rounded-full inline-block"></span> {client.phone}</p>
                     </div>
                   </div>

                   <div className="bg-slate-50 p-4 rounded-xl flex flex-col justify-center">
                     <p className="text-[10px] font-bold text-slate-400 mb-1">ASESOR COMERCIAL ASIGNADO</p>
                     <p className="font-bold text-slate-700 text-sm">Administrador Central</p>
                   </div>

                   <div className="flex justify-end gap-8 text-right">
                      <div>
                        <p className="text-[10px] font-bold text-rose-500 mb-1">DEUDA PENDIENTE</p>
                        <p className={`font-bold ${client.debt > 0 ? 'text-rose-600' : 'text-slate-400'}`}>Q {client.debt.toFixed(2)}</p>
                      </div>
                      <div>
                        <p className="text-[10px] font-bold text-slate-400 mb-1">TOTAL VENTAS</p>
                        <p className="font-bold text-slate-800">Q {client.total.toFixed(2)}</p>
                      </div>
                   </div>
                </div>
              </div>
            ))}
          </div>

        </div>
      </main>
    </div>
  );
}
