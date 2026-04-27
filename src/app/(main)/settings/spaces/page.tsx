import { redirect } from "next/navigation";
import Link from "next/link";
import { ChevronLeft } from "lucide-react";

import { ManageSpacesClient } from "@/components/ManageSpacesClient";
import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import { QUICK_LANE_CATEGORY_NAME } from "@/lib/quick-lane-category";

type PageProps = {
  searchParams: Record<string, string | string[] | undefined>;
};

export default async function ManageSpacesPage({ searchParams }: PageProps) {
  const ctx = await getSessionContext();
  if (!ctx) {
    redirect("/login");
  }

  const wallets = await prisma.wallet.findMany({
    where: { organizationId: ctx.organizationId },
    orderBy: { name: "asc" },
    include: {
      _count: { select: { transactions: true } },
      categories: {
        orderBy: { name: "asc" },
        include: { _count: { select: { transactions: true } } },
      },
    },
  });

  const fromParam = searchParams.from;
  const fromAdd =
    fromParam === "add" || (Array.isArray(fromParam) && fromParam.includes("add"));

  const data = wallets.map((w) => ({
    id: w.id,
    name: w.name,
    transactionCount: w._count.transactions,
    categories: w.categories
      .filter((c) => c.name !== QUICK_LANE_CATEGORY_NAME)
      .map((c) => ({
        id: c.id,
        name: c.name,
        transactionCount: c._count.transactions,
      })),
  }));

  return (
    <div className="space-y-6">
      <header className="space-y-2">
        <Link
          href={fromAdd ? "/" : "/profile"}
          className="inline-flex items-center gap-1 text-sm text-emerald-500 hover:text-emerald-400"
        >
          <ChevronLeft size={18} aria-hidden />
          Voltar
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Espaços e categorias</h1>
          <p className="text-slate-400 text-sm">
            Classes (carteiras) e categorias da sua organização
          </p>
        </div>
      </header>

      {fromAdd ? (
        <p
          className="rounded-2xl border border-emerald-500/35 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-100/95"
          role="status"
        >
          Para usar <strong className="font-semibold">Adicionar</strong> no rodapé, crie
          abaixo pelo menos uma <strong className="font-semibold">classe</strong> (ex.: mercado, loja)
          e, se quiser, categorias.
        </p>
      ) : null}

      <ManageSpacesClient initialWallets={data} />
    </div>
  );
}
