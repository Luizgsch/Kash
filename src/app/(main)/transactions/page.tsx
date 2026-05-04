import { Suspense } from "react";
import { TransactionType } from "@prisma/client";

import { TransactionFilterBar } from "@/components/TransactionFilterBar";
import { TransactionsList } from "@/components/TransactionsList";
import { getSessionContext } from "@/lib/auth-context";
import { prisma } from "@/lib/prisma";
import { createdAtRangeForPeriod } from "@/lib/transaction-filters";
import { orgTransactionsWhere } from "@/lib/wallet-context-shared";
import { resolveActiveWalletContext } from "@/lib/wallet-context-server";
type PageProps = {
  searchParams: Record<string, string | string[] | undefined>;
};

function firstString(
  v: string | string[] | undefined,
): string | undefined {
  if (typeof v === "string") return v;
  if (Array.isArray(v)) return v[0];
  return undefined;
}

export default async function TransactionsPage({ searchParams }: PageProps) {
  const typeParamRaw = firstString(searchParams.type);
  const typeParam =
    typeParamRaw === "INCOME"
      ? "INFLOW"
      : typeParamRaw === "EXPENSE"
        ? "OUTFLOW"
        : typeParamRaw;
  const periodParam = firstString(searchParams.period);

  const type =
    typeParam === "INFLOW" || typeParam === "OUTFLOW"
      ? (typeParam as TransactionType)
      : undefined;
  const period =
    periodParam === "this-month" || periodParam === "last-month"
      ? periodParam
      : undefined;

  const ctx = await getSessionContext();

  const wallets =
    ctx &&
    (await prisma.wallet.findMany({
      where: { organizationId: ctx.organizationId },
      orderBy: { name: "asc" },
      select: {
        id: true,
        name: true,
        categories: {
          select: { id: true, name: true },
          orderBy: { name: "asc" },
        },
      },
    }));

  const range = createdAtRangeForPeriod(period ?? null);

  const walletCtx = ctx
    ? await resolveActiveWalletContext(ctx.organizationId)
    : null;
  const txWhereBase =
    ctx && walletCtx ? orgTransactionsWhere(ctx.organizationId, walletCtx) : null;

  const transactions =
    ctx && txWhereBase
      ? await prisma.transaction.findMany({
          where: {
            ...txWhereBase,
            ...(type ? { type } : {}),
            ...(range
              ? {
                  createdAt: {
                    gte: range.gte,
                    lte: range.lte,
                  },
                }
              : {}),
          },
          orderBy: { createdAt: "desc" },
          take: 100,
          include: {
            category: { select: { name: true } },
            wallet: { select: { name: true } },
          },
        })
      : null;

  const categoriesByWallet = wallets
    ? Object.fromEntries(wallets.map((w) => [w.id, w.categories]))
    : {};

  const rowDtos =
    transactions?.map((tx) => ({
      id: tx.id,
      amount: tx.amount,
      type: tx.type,
      description: tx.description,
      categoryId: tx.categoryId,
      categoryName: tx.category?.name ?? "Mobile",
      walletId: tx.walletId,
      walletName: tx.wallet.name,
      createdAt: tx.createdAt.toISOString(),
    })) ?? [];

  return (
    <div className="space-y-6">
      <header className="mb-4 md:mb-8">
        <h1 className="hidden md:block text-2xl font-bold text-slate-100">
          Transações
        </h1>
        <p className="hidden md:block text-slate-400">
          Histórico de movimentações
          {ctx && walletCtx?.mode === "wallet" ? (
            <span className="block text-xs text-slate-500 mt-1">
              Mostrando só o espaço selecionado no topo — escolha &quot;Geral&quot;
              para ver tudo.
            </span>
          ) : null}
        </p>
      </header>

      <Suspense
        fallback={
          <div className="flex gap-2 overflow-x-auto pb-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <div
                key={i}
                className="h-9 w-24 shrink-0 rounded-full bg-slate-900 border border-slate-800 animate-pulse"
              />
            ))}
          </div>
        }
      >
        <TransactionFilterBar />
      </Suspense>

      <section className="bg-slate-900 rounded-2xl border border-slate-800">
        {!ctx ? (
          <p className="p-6 text-slate-400 text-sm">
            Entre na sua conta para ver o histórico vinculado à sua organização.
          </p>
        ) : !transactions?.length ? (
          <p className="p-6 text-slate-400 text-sm">
            Nenhuma transação encontrada para estes filtros.
          </p>
        ) : (
          <TransactionsList
            rows={rowDtos}
            categoriesByWallet={categoriesByWallet}
          />
        )}
      </section>
    </div>
  );
}
