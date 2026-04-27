import { type Prisma as PrismaTypes } from "@prisma/client";

/** Categorias sugeridas após o onboarding (Vida pessoal). */
export const ONBOARDING_PESSOAL_CATEGORIES = [
  "Mercado",
  "Aluguel",
  "Outros",
] as const;

/** Categorias sugeridas após o onboarding (Loja). */
export const ONBOARDING_LOJA_CATEGORIES = [
  "Vendas",
  "Fornecedores",
  "Outros",
] as const;

type Tx = PrismaTypes.TransactionClient;

function categoryRows(names: readonly string[], walletId: string) {
  return names.map((name) => ({
    name,
    walletId,
  }));
}

/**
 * Cria o Espaço (carteira) e categorias sugeridas, dentro de uma transação
 * (passar o mesmo `tx` do onboarding).
 */
export async function createWalletWithCategoriesInTx(
  tx: Tx,
  organizationId: string,
  walletName: string,
  categoryNames: readonly string[],
) {
  const wallet = await tx.wallet.create({
    data: { name: walletName, organizationId },
  });
  await tx.category.createMany({
    data: categoryRows(categoryNames, wallet.id),
  });
  return wallet;
}
