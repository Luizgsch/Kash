"use server";

import { redirect } from "next/navigation";

import { auth } from "@/auth";
import { prisma } from "@/lib/prisma";
import {
  isNextRedirectError,
  logServerActionError,
  SERVER_ACTION_UNAVAILABLE_PT,
} from "@/lib/server-action-error";
import {
  createWalletWithCategoriesInTx,
  ONBOARDING_LOJA_CATEGORIES,
  ONBOARDING_PESSOAL_CATEGORIES,
} from "@/lib/wallet-seed";

export type CompleteOnboardingResult =
  | { ok: true }
  | { ok: false; error: string };

export async function completeOnboarding(input: {
  pessoal: boolean;
  loja: boolean;
}): Promise<CompleteOnboardingResult> {
  try {
    if (!input.pessoal && !input.loja) {
      return { ok: false, error: "Selecione Vida pessoal, Minha loja, ou as duas." };
    }

    const session = await auth();
    if (!session?.user?.id) {
      return { ok: false, error: "Sessão inválida. Entre de novo." };
    }

    const userId = session.user.id;
    const already = await prisma.user.findUnique({
      where: { id: userId },
      select: { organizationId: true },
    });
    if (already?.organizationId) redirect("/");

    await prisma.$transaction(async (tx) => {
      const org = await tx.organization.create({
        data: { name: "Meu Kash" },
      });

      const updated = await tx.user.updateMany({
        where: { id: userId, organizationId: null },
        data: { organizationId: org.id },
      });
      if (updated.count === 0) {
        await tx.organization.delete({ where: { id: org.id } });
        return;
      }

      if (input.pessoal) {
        await createWalletWithCategoriesInTx(
          tx,
          org.id,
          "Pessoal",
          ONBOARDING_PESSOAL_CATEGORIES
        );
      }
      if (input.loja) {
        await createWalletWithCategoriesInTx(
          tx,
          org.id,
          "Loja",
          ONBOARDING_LOJA_CATEGORIES
        );
      }
    });

    const linked = await prisma.user.findUnique({
      where: { id: userId },
      select: { organizationId: true },
    });
    if (linked?.organizationId) redirect("/");

    return { ok: false, error: "Não foi possível concluir. Tente novamente." };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("completeOnboarding", e);
    return { ok: false, error: SERVER_ACTION_UNAVAILABLE_PT };
  }
}
