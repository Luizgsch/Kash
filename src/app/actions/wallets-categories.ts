"use server";

import { revalidatePath } from "next/cache";

import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import { QUICK_LANE_CATEGORY_NAME } from "@/lib/quick-lane-category";
import {
  isNextRedirectError,
  logServerActionError,
  SERVER_ACTION_UNAVAILABLE_PT,
} from "@/lib/server-action-error";

function revalidateOrgSurface() {
  revalidatePath("/settings/spaces");
  revalidatePath("/profile");
  revalidatePath("/settings");
  revalidatePath("/transactions/new");
  revalidatePath("/transactions");
  /** Dashboard (saldo geral / agregados) e layout com seletor de contexto */
  revalidatePath("/");
  revalidatePath("/", "layout");
}

const NAME_MAX = 120;

function normalizeName(raw: string) {
  return raw.replace(/\s+/g, " ").trim();
}

function isReservedCategoryName(n: string) {
  return normalizeName(n) === QUICK_LANE_CATEGORY_NAME;
}

export type WalletCategoryActionResult =
  | { ok: true }
  | { ok: false; message: string; transactionCount?: number };

export async function createWallet(name: string): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const n = normalizeName(name);
    if (n.length === 0) return { ok: false, message: "Informe o nome da classe." };
    if (n.length > NAME_MAX) return { ok: false, message: "Nome muito longo." };

    await prisma.wallet.create({
      data: { name: n, organizationId: ctx.organizationId },
    });
    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("createWallet", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export async function updateWallet(
  walletId: string,
  name: string,
): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const n = normalizeName(name);
    if (n.length === 0) return { ok: false, message: "Informe o nome da classe." };
    if (n.length > NAME_MAX) return { ok: false, message: "Nome muito longo." };

    const row = await prisma.wallet.findFirst({
      where: { id: walletId, organizationId: ctx.organizationId },
      select: { id: true },
    });
    if (!row) return { ok: false, message: "Classe não encontrada." };

    await prisma.wallet.update({
      where: { id: row.id },
      data: { name: n },
    });
    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("updateWallet", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

/**
 * Remove a classe (wallet) e todos os dados associados: transações e categorias.
 */
export async function deleteWallet(walletId: string): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const row = await prisma.wallet.findFirst({
      where: { id: walletId, organizationId: ctx.organizationId },
      select: { id: true },
    });
    if (!row) return { ok: false, message: "Classe não encontrada." };

    await prisma.$transaction([
      prisma.transaction.deleteMany({ where: { walletId: row.id } }),
      prisma.category.deleteMany({ where: { walletId: row.id } }),
      prisma.wallet.delete({ where: { id: row.id } }),
    ]);

    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("deleteWallet", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export async function createCategory(input: {
  walletId: string;
  name: string;
}): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const n = normalizeName(input.name);
    if (n.length === 0) return { ok: false, message: "Informe o nome da categoria." };
    if (n.length > NAME_MAX) return { ok: false, message: "Nome muito longo." };

    const w = await prisma.wallet.findFirst({
      where: { id: input.walletId, organizationId: ctx.organizationId },
      select: { id: true },
    });
    if (!w) return { ok: false, message: "Classe inválida." };

    if (isReservedCategoryName(n)) {
      return {
        ok: false,
        message: `O nome “${QUICK_LANE_CATEGORY_NAME}” é reservado para lançamentos sem atalho.`,
      };
    }

    try {
      await prisma.category.create({
        data: {
          name: n,
          walletId: w.id,
        },
      });
    } catch (e: unknown) {
      const code =
        e && typeof e === "object" && "code" in e
          ? (e as { code?: string }).code
          : undefined;
      if (code === "P2002") {
        return { ok: false, message: "Já existe uma categoria com este nome nesta classe." };
      }
      throw e;
    }
    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("createCategory", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export async function updateCategory(input: {
  categoryId: string;
  name: string;
}): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const n = normalizeName(input.name);
    if (n.length === 0) return { ok: false, message: "Informe o nome da categoria." };
    if (n.length > NAME_MAX) return { ok: false, message: "Nome muito longo." };

    const row = await prisma.category.findFirst({
      where: {
        id: input.categoryId,
        wallet: { organizationId: ctx.organizationId },
      },
      select: { id: true, walletId: true },
    });
    if (!row) return { ok: false, message: "Categoria não encontrada." };

    if (isReservedCategoryName(n)) {
      return {
        ok: false,
        message: `O nome “${QUICK_LANE_CATEGORY_NAME}” é reservado para lançamentos sem atalho.`,
      };
    }

    try {
      await prisma.category.update({
        where: { id: row.id },
        data: { name: n },
      });
    } catch (e: unknown) {
      const code =
        e && typeof e === "object" && "code" in e
          ? (e as { code?: string }).code
          : undefined;
      if (code === "P2002") {
        return { ok: false, message: "Já existe uma categoria com este nome nesta classe." };
      }
      throw e;
    }
    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("updateCategory", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export async function deleteCategory(categoryId: string): Promise<WalletCategoryActionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) return { ok: false, message: "Sessão inválida." };

    const row = await prisma.category.findFirst({
      where: {
        id: categoryId,
        wallet: { organizationId: ctx.organizationId },
      },
      select: { id: true, _count: { select: { transactions: true } } },
    });
    if (!row) return { ok: false, message: "Categoria não encontrada." };
    if (row._count.transactions > 0) {
      return {
        ok: false,
        message:
          "Não é possível excluir esta categoria enquanto houver transações vinculadas.",
        transactionCount: row._count.transactions,
      };
    }

    await prisma.category.delete({ where: { id: row.id } });
    revalidateOrgSurface();
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("deleteCategory", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}
