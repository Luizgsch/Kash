import Link from "next/link";
import { redirect } from "next/navigation";

import { QuickAddTransaction } from "./QuickAddTransaction";
import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import { QUICK_LANE_CATEGORY_NAME } from "@/lib/quick-lane-category";
import { resolveActiveWalletContext } from "@/lib/wallet-context-server";

export default async function NewTransactionPage() {
  const ctx = await getSessionContext();

  if (!ctx) {
    return (
      <div className="space-y-4">
        <Link
          href="/transactions"
          className="text-sm text-emerald-500 hover:text-emerald-400"
        >
          ← Voltar
        </Link>
        <p className="text-slate-400 text-sm">
          <Link href="/login" className="text-emerald-500 hover:text-emerald-400">
            Entre na sua conta
          </Link>{" "}
          para lançar transações.
        </p>
      </div>
    );
  }

  const wallets = await prisma.wallet.findMany({
    where: { organizationId: ctx.organizationId },
    orderBy: { name: "asc" },
    include: {
      categories: {
        orderBy: { name: "asc" },
      },
    },
  });

  if (wallets.length === 0) {
    redirect("/settings/spaces?from=add");
  }

  const payload = wallets.map((w) => ({
    id: w.id,
    name: w.name,
    categories: w.categories
      .filter((c) => c.name !== QUICK_LANE_CATEGORY_NAME)
      .map((c) => ({
        id: c.id,
        name: c.name,
      })),
  }));

  const activeContext = await resolveActiveWalletContext(ctx.organizationId);

  return (
    <QuickAddTransaction wallets={payload} activeContext={activeContext} />
  );
}
