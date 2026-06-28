"use client";
import { useState } from "react";
import { Check } from "lucide-react";

export default function Pricing() {
  const [currentPlan, setCurrentPlan] = useState("BÁSICO");
  const [isProcessing, setIsProcessing] = useState(false);
  const [targetPlan, setTargetPlan] = useState("");

  const basicFeatures = [
    "Inventario hasta 50 productos",
    "Registro de ventas básico",
    "Soporte de la comunidad"
  ];

  const proFeatures = [
    "Inventario Ilimitado",
    "Reportes avanzados y gráficas",
    "Exportación de datos a Excel y PDF",
    "Soporte Prioritario 24/7 por chat",
    "Gestión de Múltiples Usuarios y Roles",
    "Sincronización en la nube en tiempo real",
    "Alertas de stock bajo e informes automáticos",
    "Integración con impresoras térmicas",
    "Soporte para lector de código de barras",
    "Sin anuncios ni marcas de agua"
  ];

  const handleSubscribe = (plan: string) => {
    setTargetPlan(plan);
    setIsProcessing(true);
    
    // Aquí puedes integrar Stripe Web (Stripe Checkout) en el futuro
    setTimeout(() => {
      setIsProcessing(false);
      setCurrentPlan(plan);
      setTargetPlan("");
      if (plan === "PRO SAAS") {
        alert("¡Tu cuenta ha sido actualizada al plan PRO SAAS correctamente!");
      } else {
        alert("Suscripción cancelada. Has regresado al plan BÁSICO.");
      }
    }, 1500);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center py-16 px-4 font-sans">
      <div className="max-w-4xl w-full">
        <h1 className="text-4xl font-bold text-center mb-4 text-gray-900">Planes y Precios</h1>
        <p className="text-gray-500 text-center mb-12 text-lg">Elige el mejor plan para escalar tu negocio hoy mismo.</p>

        <div className="grid md:grid-cols-2 gap-8">
          {/* Plan Básico */}
          <div className="bg-white rounded-3xl p-8 shadow-sm border border-gray-200 flex flex-col">
            <h3 className="text-xl font-bold mb-2 text-gray-800">BÁSICO</h3>
            <div className="text-4xl font-black mb-2 text-gray-900">Gratis</div>
            <p className="text-gray-500 mb-8 text-sm">Para empezar tu negocio.</p>

            <ul className="space-y-4 mb-8 flex-1">
              {basicFeatures.map((f, i) => (
                <li key={i} className="flex items-start">
                  <Check className="w-5 h-5 text-blue-600 mr-3 shrink-0 mt-0.5" />
                  <span className="text-gray-700 font-medium">{f}</span>
                </li>
              ))}
            </ul>

            <button
              onClick={() => handleSubscribe("BÁSICO")}
              disabled={currentPlan === "BÁSICO" || isProcessing}
              className={`w-full py-4 rounded-2xl font-bold transition-all ${
                currentPlan === "BÁSICO"
                  ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                  : "bg-blue-50 text-blue-700 hover:bg-blue-100"
              }`}
            >
              {isProcessing && targetPlan === "BÁSICO" ? "Procesando..." : currentPlan === "BÁSICO" ? "Plan Actual" : "Cambiar a Básico"}
            </button>
          </div>

          {/* Plan PRO */}
          <div className="bg-blue-600 text-white rounded-3xl p-8 shadow-2xl flex flex-col relative transform md:-translate-y-4">
            <div className="absolute top-0 right-8 transform -translate-y-1/2">
              <span className="bg-gradient-to-r from-yellow-400 to-yellow-500 text-yellow-950 text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wide">
                Recomendado
              </span>
            </div>
            
            <h3 className="text-xl font-bold mb-2 text-blue-100">PRO SAAS</h3>
            <div className="text-4xl font-black mb-2 text-white">Q 149 <span className="text-xl font-normal opacity-80">/ mes</span></div>
            <p className="text-blue-200 mb-8 text-sm">Todo lo que necesitas para crecer.</p>

            <ul className="space-y-4 mb-8 flex-1">
              {proFeatures.map((f, i) => (
                <li key={i} className="flex items-start">
                  <Check className="w-5 h-5 text-blue-300 mr-3 shrink-0 mt-0.5" />
                  <span className="text-blue-50 font-medium">{f}</span>
                </li>
              ))}
            </ul>

            <button
              onClick={() => handleSubscribe("PRO SAAS")}
              disabled={currentPlan === "PRO SAAS" || isProcessing}
              className={`w-full py-4 rounded-2xl font-bold transition-all ${
                currentPlan === "PRO SAAS"
                  ? "bg-blue-800 text-blue-300 cursor-not-allowed"
                  : "bg-white text-blue-600 hover:bg-gray-50 shadow-lg hover:shadow-xl"
              }`}
            >
              {isProcessing && targetPlan === "PRO SAAS" ? "Procesando..." : currentPlan === "PRO SAAS" ? "Plan Actual" : "Actualizar a PRO"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
