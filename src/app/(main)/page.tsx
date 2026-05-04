import Link from "next/link";
import { Wallet, TrendingUp, TrendingDown } from "lucide-react";
import type { TransactionType } from "@prisma/client";

import { getSessionContext } from "@/lib/auth-context";
import { categoryEmoji } from "@/lib/category-emoji";
import { prisma } from "@/lib/prisma";
import {
  orgTransactionsWhere,
  type ResolvedWalletContext,
} from "@/lib/wallet-context-shared";
import { resolveActiveWalletContext } from "@/lib/wallet-context-server";
import { formatCurrency } from "@/lib/utils";

type RecentTx = {
  id: string;
  description: string | null;
  category: { name: string } | null;
  amount: number;
  type: TransactionType;
  createdAt: Date;
};

export default async function Home() {
  const ctx = await getSessionContext();

  let balance = 0;
  let incomeTotal = 0;
  let expenseTotal = 0;
  let recent: RecentTx[] = [];
  let walletCtx: ResolvedWalletContext | null = null;

  if (ctx) {
    walletCtx = await resolveActiveWalletContext(ctx.organizationId);
    const txWhereBase = orgTransactionsWhere(ctx.organizationId, walletCtx);

    const [inc, exp, last] = await Promise.all([
      prisma.transaction.aggregate({
        where: { ...txWhereBase, type: "INFLOW" },
        _sum: { amount: true },
      }),
      prisma.transaction.aggregate({
        where: { ...txWhereBase, type: "OUTFLOW" },
        _sum: { amount: true },
      }),
      prisma.transaction.findMany({
        where: txWhereBase,
        orderBy: { createdAt: "desc" },
        take: 3,
        select: {
          id: true,
          description: true,
          amount: true,
          type: true,
          createdAt: true,
          category: { select: { name: true } },
        },
      }),
    ]);

    incomeTotal = inc._sum.amount ?? 0;
    expenseTotal = exp._sum.amount ?? 0;
    balance = incomeTotal - expenseTotal;
    recent = last;
  }

  const timeFmt = new Intl.DateTimeFormat("pt-BR", {
    day: "numeric",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <div className="space-y-5 md:space-y-6">
      <header className="hidden md:block mb-8">
        <h1 className="text-2xl font-bold text-slate-100">Dashboard</h1>
        <p className="text-slate-400">
          Visão geral da sua organização
          {ctx && walletCtx?.mode === "wallet" ? (
            <span className="block text-xs text-slate-500 mt-1">
              Filtrado por um espaço — use &quot;Contexto&quot; no topo para Geral
              ou outro espaço.
            </span>
          ) : null}
        </p>
      </header>

      <section className="space-y-3 sm:space-y-4">
        <div className="bg-slate-900 rounded-2xl p-5 sm:p-6 border border-slate-800">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center">
              <Wallet size={20} className="text-slate-300" />
            </div>
            <span className="text-slate-400">Saldo Atual</span>
          </div>
          <p className="text-4xl font-bold text-slate-100">
            {formatCurrency(balance)}
          </p>
          {!ctx ? (
            <p className="mt-2 text-xs text-slate-500">
              Entre na sua conta para acompanhar saldo e transações reais.
            </p>
          ) : null}
        </div>

        <div className="grid grid-cols-2 gap-3 sm:gap-4">
          <Link
            href="/transactions?type=INFLOW"
            className="block bg-slate-900 rounded-2xl p-4 border border-slate-800 transition-transform duration-150 ease-out active:scale-95 touch-manipulation hover:border-slate-700"
          >
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-full bg-emerald-500/10 flex items-center justify-center">
                <TrendingUp size={18} className="text-emerald-500" />
              </div>
              <span className="text-slate-400 text-sm">Entrada</span>
            </div>
            <p className="text-xl font-bold text-emerald-500">
              {formatCurrency(incomeTotal)}
            </p>
          </Link>

          <Link
            href="/transactions?type=OUTFLOW"
            className="block bg-slate-900 rounded-2xl p-4 border border-slate-800 transition-transform duration-150 ease-out active:scale-95 touch-manipulation hover:border-slate-700"
          >
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-full bg-rose-500/10 flex items-center justify-center">
                <TrendingDown size={18} className="text-rose-500" />
              </div>
              <span className="text-slate-400 text-sm">Saída</span>
            </div>
            <p className="text-xl font-bold text-rose-500">
              {formatCurrency(expenseTotal)}
            </p>
          </Link>
        </div>
      </section>

      <section className="bg-slate-900 rounded-2xl border border-slate-800 p-4 sm:p-5">
        <h2 className="text-base sm:text-lg font-semibold text-slate-100 mb-3 sm:mb-4">
          Últimas Transações
        </h2>
        {!ctx || recent.length === 0 ? (
          <p className="text-sm text-slate-500 py-2">
            {ctx
              ? "Nenhuma transação ainda."
              : "Faça login para ver as últimas movimentações."}
          </p>
        ) : (
          <div className="space-y-3">
            {recent.map((tx) => {
              const catName = tx.category?.name.trim() || "Sem categoria";
              const emoji = categoryEmoji(tx.category?.name ?? "");
              const kind = tx.type === "INFLOW" ? "Entrada" : "Saída";
              return (
              <div
                key={tx.id}
                className="flex items-center justify-between py-3 border-b border-slate-800 last:border-0 gap-3"
              >
                <div className="flex items-center gap-3 min-w-0">
                  <div
                    className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center shrink-0 text-lg leading-none border border-slate-700"
                    aria-hidden
                  >
                    {emoji}
                  </div>
                  <div className="min-w-0">
                    <p className="font-medium text-slate-200 truncate">
                      {tx.description?.trim() || catName}
                    </p>
                    <p className="text-sm text-slate-500">
                      <span className="text-slate-400">{emoji}</span>{" "}
                      {catName}
                      <span className="text-slate-600"> · </span>
                      {kind}
                      <span className="text-slate-600"> · </span>
                      {timeFmt.format(tx.createdAt)}
                    </p>
                  </div>
                </div>
                <span
                  className={`font-semibold shrink-0 ${
                    tx.type === "INFLOW"
                      ? "text-emerald-500"
                      : "text-rose-500"
                  }`}
                >
                  {tx.type === "INFLOW" ? "+ " : "- "}
                  {formatCurrency(tx.amount)}
                </span>
              </div>
            );
            })}
          </div>
        )}
      </section>
    </div>
  );
}
