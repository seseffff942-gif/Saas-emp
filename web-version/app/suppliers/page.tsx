"use client";

import { useState } from "react";
import { Sidebar } from "@/components/Sidebar";
import { Search, Clock, CheckCircle } from "lucide-react";

export default function Suppliers() {
  const [activeTab, setActiveTab] = useState('abiertas');

  // Dummy data
  const suppliers = [
    { id: 1, name: "AGRO-INVERSIONES S.A", pending: 3600.00, total: 3600.00, due: 180 },
    { id: 2, name: "COMERCIAL LA GRANJA", pending: 3300.00, total: 3300.00, due: 210 },
    { id: 3, name: "VETERINARIA UNIVERSAL", pending: 1202.32, total: 1202.32, due: 0 },
  ];

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-6 flex justify-between items-center sticky top-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Proveedores</h2>
            <p className="text-sm text-slate-500 mt-1">Control de compras a proveedores, plazos de crédito y alertas.</p>
          </div>
          
          <div className="flex gap-4">
            <div className="bg-white px-4 py-2 border border-slate-200 rounded-xl">
              <p className="text-[10px] font-bold text-slate-400">SALDO PENDIENTE GENERAL</p>
              <p className="font-bold text-slate-800 text-xl">Q 8,102.32</p>
            </div>
            <div className="bg-rose-50 px-4 py-2 border border-rose-100 rounded-xl flex items-center gap-3">
              <div>
                <p className="text-[10px] font-bold text-rose-500">ATENCIÓN INMEDIATA</p>
                <p className="font-bold text-rose-600 text-sm">Vencidas o prontas a vencer</p>
              </div>
              <div className="bg-white p-2 rounded text-rose-500"><Clock className="w-5 h-5"/></div>
            </div>
             <div className="bg-emerald-50 px-4 py-2 border border-emerald-100 rounded-xl flex items-center gap-3">
               <div>
                <p className="text-[10px] font-bold text-emerald-600">PAGOS HISTÓRICOS REALIZADOS</p>
                <p className="font-bold text-emerald-700 text-xl">Q 0.00</p>
               </div>
               <div className="bg-white p-2 rounded text-emerald-500"><CheckCircle className="w-5 h-5"/></div>
            </div>
          </div>
        </header>

        <div className="p-8 max-w-6xl mx-auto">
          <div className="flex gap-2 border-b border-slate-200 mb-6">
            <button 
              onClick={() => setActiveTab('abiertas')}
              className={`px-6 py-3 font-bold text-sm border-b-2 transition-colors ${activeTab === 'abiertas' ? 'border-emerald-500 text-emerald-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}
            >
              FACTURAS ABIERTAS
            </button>
            <button 
              onClick={() => setActiveTab('proveedores')}
              className={`px-6 py-3 font-bold text-sm border-b-2 transition-colors flex items-center gap-2 ${activeTab === 'proveedores' ? 'border-emerald-500 text-emerald-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}
            >
              PROVEEDORES <span className="bg-slate-100 text-slate-600 px-2 py-0.5 rounded-full text-xs">14</span>
            </button>
            <button 
              onClick={() => setActiveTab('historial')}
              className={`px-6 py-3 font-bold text-sm border-b-2 transition-colors flex items-center gap-2 ${activeTab === 'historial' ? 'border-emerald-500 text-emerald-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}
            >
              <Clock className="w-4 h-4" /> HISTORIAL
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {suppliers.map(sup => (
              <div key={sup.id} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col">
                 <div className="flex justify-between items-start mb-4">
                   <h3 className="font-bold text-slate-800 text-sm">Factura: {sup.name}</h3>
                   <span className="text-[10px] font-bold bg-slate-100 text-slate-500 px-2 py-1 rounded">{sup.due} REST</span>
                 </div>
                 <p className="text-[10px] font-bold text-slate-400 mb-6 tracking-wide">NO CATALOGADO</p>
                 
                 <div className="flex justify-between mb-6">
                   <div>
                     <p className="text-[10px] font-bold text-slate-400 mb-1">TOTAL ORIG.</p>
                     <p className="font-bold text-slate-800">Q {sup.total.toFixed(2)}</p>
                   </div>
                   <div className="text-right">
                     <p className="text-[10px] font-bold text-rose-500 mb-1">RESTANTE / DEUDA</p>
                     <p className="font-bold text-rose-600">Q {sup.pending.toFixed(2)}</p>
                   </div>
                 </div>

                 <button className="w-full mt-auto py-3 bg-slate-800 text-white font-bold rounded-xl hover:bg-slate-900 transition-colors shadow-lg shadow-slate-200">
                   LIQUIDAR
                 </button>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}
