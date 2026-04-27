import { cookies } from "next/headers";

import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import {
  ACTIVE_WALLET_COOKIE,
  ACTIVE_WALLET_GERAL,
} from "@/lib/wallet-context-constants";
import type { ResolvedWalletContext } from "@/lib/wallet-context-shared";

/**
 * Resolve o contexto ativo a partir da cookie e das wallets da organização.
 * Cookie inválida ou ausente → modo Geral.
 */
export async function resolveActiveWalletContext(
  organizationId: string,
): Promise<ResolvedWalletContext> {
  const wallets = await prisma.wallet.findMany({
    where: { organizationId },
    select: { id: true },
  });
  const allowed = new Set(wallets.map((w) => w.id));
  const raw = cookies().get(ACTIVE_WALLET_COOKIE)?.value;
  if (raw && raw !== ACTIVE_WALLET_GERAL && allowed.has(raw)) {
    return { mode: "wallet", walletId: raw };
  }
  return { mode: "geral" };
}

export type WalletSelectorViewModel = {
  wallets: { id: string; name: string }[];
  initialValue: string;
};

/** Dados para o seletor no layout (null = utilizador sem organização / não autenticado). */
export async function getWalletSelectorViewModel(): Promise<WalletSelectorViewModel | null> {
  const sessionCtx = await getSessionContext();
  if (!sessionCtx) return null;

  const wallets = await prisma.wallet.findMany({
    where: { organizationId: sessionCtx.organizationId },
    orderBy: { name: "asc" },
    select: { id: true, name: true },
  });

  const resolved = await resolveActiveWalletContext(sessionCtx.organizationId);
  const initialValue =
    resolved.mode === "wallet" ? resolved.walletId : ACTIVE_WALLET_GERAL;

  return { wallets, initialValue };
}
