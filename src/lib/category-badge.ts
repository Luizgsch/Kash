const PALETTE: Record<string, string> = {
  "Venda de Planta":
    "border-lime-500/25 bg-lime-500/10 text-lime-100/90",
  Fósforo:
    "border-orange-500/25 bg-orange-500/10 text-orange-100/90",
  Café:
    "border-amber-700/30 bg-amber-900/30 text-amber-100/90",
  Vendas:
    "border-amber-500/25 bg-amber-500/10 text-amber-200/90",
  Serviços:
    "border-sky-500/25 bg-sky-500/10 text-sky-200/90",
  Aluguel:
    "border-violet-500/25 bg-violet-500/10 text-violet-200/90",
  Utilities:
    "border-cyan-500/25 bg-cyan-500/10 text-cyan-200/90",
  Salários:
    "border-emerald-500/25 bg-emerald-500/10 text-emerald-200/90",
  Mercado:
    "border-teal-500/25 bg-teal-500/10 text-teal-100/90",
  Transporte:
    "border-indigo-500/25 bg-indigo-500/10 text-indigo-100/90",
  Lazer:
    "border-fuchsia-500/25 bg-fuchsia-500/10 text-fuchsia-100/90",
  Outros:
    "border-slate-500/30 bg-slate-800/80 text-slate-300",
};

export function categoryBadgeClass(category: string | null | undefined): string {
  const key = category?.trim() ?? "";
  if (!key) {
    return "border-slate-700 bg-slate-800/60 text-slate-400";
  }
  return (
    PALETTE[key] ??
    "border-slate-600/50 bg-slate-800/90 text-slate-300"
  );
}
