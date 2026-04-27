/**
 * Formata valor monetário (centavos) para BRL no padrão pt-BR.
 * @param amount valor inteiro em centavos; ex.: 100 → R$ 1,00
 */
export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(amount / 100);
}

/** Valor em centavos formatado só com número (ex.: 1.234,56), sem símbolo R$. */
export function formatBrlAmountFromCents(cents: number): string {
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(cents / 100);
}

/**
 * Converte valor em reais para centavos (integer)
 * @param value Valor em reais
 * @returns Valor em centavos
 */
export function toCents(value: number): number {
  return Math.round(value * 100);
}
