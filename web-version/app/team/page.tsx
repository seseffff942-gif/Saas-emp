"use client";

import { Sidebar } from "@/components/Sidebar";
import { Plus, User, Mail, Phone, Shield } from "lucide-react";

export default function Team() {
  const team = [
    { id: 1, name: "Administrador General", email: "admin@prosaas.com", phone: "+502 1234 5678", role: "ADMINISTRADOR", initial: "A" },
    { id: 2, name: "Vendedor 1", email: "vendedor1@prosaas.com", phone: "+502 8765 4321", role: "VENDEDOR", initial: "V" },
    { id: 3, name: "Gerente Operaciones", email: "gerente@prosaas.com", phone: "+502 5555 5555", role: "ADMINISTRADOR", initial: "G" },
  ];

  return (
    <div className="min-h-screen bg-[#f8fafc] flex">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white border-b border-slate-200 px-8 py-6 flex justify-between items-center sticky top-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Equipo Pro SAAS</h2>
            <p className="text-sm text-slate-500 mt-1">Directorio de personal y accesos autorizados</p>
          </div>
          
          <button className="flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-xl font-bold hover:bg-emerald-700 transition-colors shadow-lg shadow-emerald-200">
            <Plus className="w-5 h-5" /> Agregar Miembro
          </button>
        </header>

        <div className="p-8 max-w-7xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {team.map(member => (
              <div key={member.id} className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden">
                <div className={`absolute top-0 left-0 w-full h-2 ${member.role === 'ADMINISTRADOR' ? 'bg-blue-500' : 'bg-emerald-500'}`}></div>
                
                <div className="flex flex-col items-center text-center">
                  <div className="w-20 h-20 rounded-full bg-slate-100 flex items-center justify-center text-2xl font-bold text-slate-400 mb-4 border-4 border-white shadow-sm">
                    {member.initial}
                  </div>
                  
                  <h3 className="font-bold text-slate-800 text-lg mb-1">{member.name}</h3>
                  
                  <div className={`px-3 py-1 rounded-full text-[10px] font-bold mb-6 flex items-center gap-1 ${member.role === 'ADMINISTRADOR' ? 'bg-blue-50 text-blue-600 border border-blue-100' : 'bg-emerald-50 text-emerald-600 border border-emerald-100'}`}>
                    <Shield className="w-3 h-3" /> {member.role}
                  </div>

                  <div className="w-full space-y-3 bg-slate-50 p-4 rounded-xl text-left">
                    <div className="flex items-center gap-3 text-sm text-slate-600">
                      <Mail className="w-4 h-4 text-slate-400" />
                      <span className="truncate">{member.email}</span>
                    </div>
                    <div className="flex items-center gap-3 text-sm text-slate-600">
                      <Phone className="w-4 h-4 text-slate-400" />
                      <span>{member.phone}</span>
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
