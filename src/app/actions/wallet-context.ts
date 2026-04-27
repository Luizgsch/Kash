"use server";

import { revalidatePath } from "next/cache";
import { cookies } from "next/headers";

import { getSessionContext } from "@/lib/auth-context";
import {
  ACTIVE_WALLET_COOKIE,
  ACTIVE_WALLET_GERAL,
} from "@/lib/wallet-context-constants";
import { prisma } from "@/lib/prisma";
import {
  isNextRedirectError,
  logServerActionError,
  SERVER_ACTION_UNAVAILABLE_PT,
} from "@/lib/server-action-error";

const COOKIE_MAX_AGE = 60 * 60 * 24 * 400;

export type SetWalletContextResult =
  | { ok: true }
  | { ok: false; message: string };

export async function setActiveWalletContext(
  value: string,
): Promise<SetWalletContextResult> {
  try {
    const ctx = await getSessionContext();
    if (!ctx) {
      return { ok: false, message: "Entre na sua conta para alterar o contexto." };
    }

    const jar = cookies();

    if (value === ACTIVE_WALLET_GERAL) {
      jar.set(ACTIVE_WALLET_COOKIE, ACTIVE_WALLET_GERAL, {
        path: "/",
        maxAge: COOKIE_MAX_AGE,
        sameSite: "lax",
        httpOnly: true,
      });
    } else {
      const wallet = await prisma.wallet.findFirst({
        where: { id: value, organizationId: ctx.organizationId },
        select: { id: true },
      });
      if (!wallet) {
        return { ok: false, message: "Espaço inválido para esta organização." };
      }
      jar.set(ACTIVE_WALLET_COOKIE, wallet.id, {
        path: "/",
        maxAge: COOKIE_MAX_AGE,
        sameSite: "lax",
        httpOnly: true,
      });
    }

    revalidatePath("/");
    revalidatePath("/transactions");
    revalidatePath("/transactions/new");
    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("setActiveWalletContext", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}
