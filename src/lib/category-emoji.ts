/** Emoji por nome de categoria (seed + fallback) para identificação rápida no histórico. */
const BY_NAME: Record<string, string> = {
  "Venda de Planta": "🌿",
  Fósforo: "🔥",
  Café: "☕",
  Água: "💧",
  Embalagem: "📦",
  Delivery: "🛵",
  "Pedido Online": "🛒",
  Desconto: "🏷️",
  Ajuste: "⚖️",
  Outros: "📌",
  Vendas: "💰",
  Serviços: "🛠️",
  Aluguel: "🏠",
  Utilities: "💡",
  Salários: "👷",
  Mercado: "🧺",
  Transporte: "🚌",
  Lazer: "🎉",
  Fornecedores: "📋",
  Contas: "📄",
  Saúde: "💊",
  Pet: "🐾",
  Presentes: "🎁",
  Vestuário: "👕",
};

export function categoryEmoji(name: string | null | undefined): string {
  const key = name?.trim() ?? "";
  if (!key) return "📌";
  return BY_NAME[key] ?? "📌";
}
