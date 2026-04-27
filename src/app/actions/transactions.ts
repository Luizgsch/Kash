"use server";

import { revalidatePath } from "next/cache";
import { TransactionType } from "@prisma/client";

import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import { QUICK_LANE_CATEGORY_NAME } from "@/lib/quick-lane-category";
import {
  isNextRedirectError,
  logServerActionError,
  SERVER_ACTION_UNAVAILABLE_PT,
} from "@/lib/server-action-error";

const QUICK_DESC_MAX = 500;

function normalizeQuickDescription(raw: string | null | undefined): string | null {
  const t = (raw ?? "").replace(/\s+/g, " ").trim();
  if (t.length === 0) return null;
  return t.slice(0, QUICK_DESC_MAX);
}

async function getOrCreateQuickLaneCategoryId(walletId: string): Promise<string> {
  const existing = await prisma.category.findFirst({
    where: { walletId, name: QUICK_LANE_CATEGORY_NAME },
    select: { id: true },
  });
  if (existing) return existing.id;

  try {
    const created = await prisma.category.create({
      data: {
        walletId,
        name: QUICK_LANE_CATEGORY_NAME,
      },
      select: { id: true },
    });
    return created.id;
  } catch (e: unknown) {
    const code =
      e && typeof e === "object" && "code" in e
        ? (e as { code?: string }).code
        : undefined;
    if (code === "P2002") {
      const again = await prisma.category.findFirst({
        where: { walletId, name: QUICK_LANE_CATEGORY_NAME },
        select: { id: true },
      });
      if (again) return again.id;
    }
    throw e;
  }
}

export type QuickCreateResult =
  | { ok: true; transactionId: string }
  | { ok: false; message: string };

export async function quickCreateTransaction(input: {
  walletId: string;
  /** Omitido ou vazio: usa categoria interna de lançamento rápido (título em `description`). */
  categoryId?: string | null;
  amountCents: number;
  type: TransactionType;
  description?: string | null;
}): Promise<QuickCreateResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) {
      return { ok: false, message: "Entre na sua conta para registrar a transação." };
    }

    const amountCents = Math.abs(Math.round(input.amountCents));
    if (!Number.isFinite(amountCents) || amountCents <= 0) {
      return { ok: false, message: "Informe um valor maior que zero." };
    }

    if (input.type !== "INFLOW" && input.type !== "OUTFLOW") {
      return { ok: false, message: "Selecione entrada ou saída." };
    }

    const wallet = await prisma.wallet.findFirst({
      where: {
        id: input.walletId,
        organizationId: ctx.organizationId,
      },
      select: { id: true },
    });

    if (!wallet) {
      return { ok: false, message: "Espaço inválido." };
    }

    const description = normalizeQuickDescription(input.description);
    const cidRaw =
      typeof input.categoryId === "string" ? input.categoryId.trim() : "";

    let categoryId: string;
    if (cidRaw.length > 0) {
      const category = await prisma.category.findFirst({
        where: {
          id: cidRaw,
          walletId: input.walletId,
          wallet: { organizationId: ctx.organizationId },
        },
        select: { id: true },
      });

      if (!category) {
        return { ok: false, message: "Categoria inválida para este espaço." };
      }
      categoryId = category.id;
    } else {
      categoryId = await getOrCreateQuickLaneCategoryId(wallet.id);
    }

    const created = await prisma.transaction.create({
      data: {
        amount: amountCents,
        type: input.type,
        walletId: wallet.id,
        categoryId,
        userId: ctx.userId,
        organizationId: ctx.organizationId,
        description,
      },
      select: { id: true },
    });

    revalidatePath("/transactions");
    revalidatePath("/");
    revalidatePath("/transactions/new");
    revalidatePath("/settings/spaces");
    return { ok: true, transactionId: created.id };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("quickCreateTransaction", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export type UpdateTransactionResult =
  | { ok: true }
  | { ok: false; message: string };

export async function updateTransaction(input: {
  transactionId: string;
  amountCents: number;
  description: string | null;
  categoryId: string;
  type: TransactionType;
  createdAtIso: string;
}): Promise<UpdateTransactionResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) {
      return { ok: false, message: "Entre na sua conta para editar." };
    }

    const amountCents = Math.abs(Math.round(input.amountCents));
    if (!Number.isFinite(amountCents) || amountCents <= 0) {
      return { ok: false, message: "Informe um valor maior que zero." };
    }

    if (input.type !== "INFLOW" && input.type !== "OUTFLOW") {
      return { ok: false, message: "Selecione entrada ou saída." };
    }

    const row = await prisma.transaction.findFirst({
      where: {
        id: input.transactionId,
        organizationId: ctx.organizationId,
      },
      select: { id: true, walletId: true },
    });

    if (!row) {
      return { ok: false, message: "Transação não encontrada." };
    }

    const category = await prisma.category.findFirst({
      where: {
        id: input.categoryId,
        walletId: row.walletId,
        wallet: { organizationId: ctx.organizationId },
      },
      select: { id: true },
    });

    if (!category) {
      return { ok: false, message: "Categoria inválida para este espaço." };
    }

    const createdAt = new Date(input.createdAtIso);
    if (Number.isNaN(createdAt.getTime())) {
      return { ok: false, message: "Data inválida." };
    }

    const description =
      typeof input.description === "string" && input.description.trim()
        ? input.description.trim()
        : null;

    await prisma.transaction.update({
      where: { id: row.id },
      data: {
        amount: amountCents,
        type: input.type,
        description,
        categoryId: category.id,
        createdAt,
      },
    });

    revalidatePath("/transactions");
    revalidatePath("/");
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("updateTransaction", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

export type UndoResult = { ok: true } | { ok: false; message: string };

export async function undoTransaction(transactionId: string): Promise<UndoResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) {
      return { ok: false, message: "Sessão inválida. Entre de novo." };
    }

    const found = await prisma.transaction.findFirst({
      where: { id: transactionId, organizationId: ctx.organizationId },
      select: { id: true },
    });

    if (!found) {
      return { ok: false, message: "Transação não encontrada." };
    }

    await prisma.transaction.delete({ where: { id: transactionId } });
    revalidatePath("/transactions");
    revalidatePath("/");
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("undoTransaction", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}

/** Mesma regra que desfazer: remove a transação da organização atual. */
export async function deleteTransaction(
  transactionId: string,
): Promise<UndoResult> {
  return undoTransaction(transactionId);
}
