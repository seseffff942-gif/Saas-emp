import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { LayoutDashboard, Package, DollarSign, Settings, LogOut, ShoppingCart, Users, Truck, Briefcase } from 'lucide-react';
import { supabase } from '@/lib/supabase';
import { useRouter } from 'next/navigation';

export function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  const handleSignOut = async () => {
    await supabase.auth.signOut();
    router.push('/');
  };

  const navItems = [
    { name: 'Panel de Control', href: '/dashboard', icon: LayoutDashboard },
    { name: 'Bodega', href: '/inventory', icon: Package },
    { name: 'Ventas', href: '/pos', icon: ShoppingCart },
    { name: 'Finanzas y Cuentas', href: '/finances', icon: DollarSign },
    { name: 'Proveedores', href: '/suppliers', icon: Truck },
    { name: 'Directorio Premium', href: '/clients', icon: Briefcase },
    { name: 'Equipo', href: '/team', icon: Users },
  ];

  return (
    <div className="w-64 bg-[#0f172a] text-slate-300 border-r border-slate-800 min-h-screen flex flex-col">
      <div className="p-6 flex items-center gap-3">
        <div className="bg-emerald-500 text-white p-2 rounded-lg">
          <Package className="h-6 w-6" />
        </div>
        <h1 className="text-2xl font-bold text-white">Pro SAAS</h1>
      </div>
      
      <div className="px-6 mb-4">
        <div className="flex items-center gap-2 text-xs font-medium bg-emerald-500/10 text-emerald-400 px-3 py-1.5 rounded-full border border-emerald-500/20">
          <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></div>
          SINCRONIZADO (TIEMPO REAL)
        </div>
      </div>
      
      <nav className="flex-1 px-4 space-y-1 mt-4 overflow-y-auto">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href;
          
          return (
            <Link
              key={item.name}
              href={item.href}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                isActive 
                  ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 shadow-sm' 
                  : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
              }`}
            >
              <Icon className={`h-5 w-5 ${isActive ? 'text-emerald-400' : 'text-slate-400'}`} />
              <span className="font-medium text-sm">{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-slate-800">
        <button
          onClick={handleSignOut}
          className="flex items-center gap-3 px-4 py-3 text-rose-400 hover:bg-rose-500/10 rounded-xl w-full transition-colors"
        >
          <LogOut className="h-5 w-5" />
          <span className="font-medium text-sm">Cerrar Sesión</span>
        </button>
        
        <div className="mt-4 text-xs text-slate-500 px-4 pb-2">
          <p>© 2026 Pro SAAS.</p>
          <p>Todos los derechos reservados.</p>
        </div>
      </div>
    </div>
  );
}
