import type { Prisma } from "@prisma/client";

export type ResolvedWalletContext =
  | { mode: "geral" }
  | { mode: "wallet"; walletId: string };

export function orgTransactionsWhere(
  organizationId: string,
  ctx: ResolvedWalletContext,
): Prisma.TransactionWhereInput {
  const base: Prisma.TransactionWhereInput = { organizationId };
  if (ctx.mode === "wallet") {
    base.walletId = ctx.walletId;
  }
  return base;
}
