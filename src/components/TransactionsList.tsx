"use client";

import { useCallback, useState } from "react";
import type { TransactionType } from "@prisma/client";

import {
  TransactionEditDrawer,
  type EditableTransaction,
} from "@/components/TransactionEditDrawer";
import { categoryBadgeClass } from "@/lib/category-badge";
import { categoryEmoji } from "@/lib/category-emoji";
import { formatCurrency } from "@/lib/utils";

export type TransactionRowDTO = {
  id: string;
  amount: number;
  type: TransactionType;
  description: string | null;
  categoryId: string | null;
  categoryName: string;
  walletId: string;
  walletName: string;
  createdAt: string;
};

type Props = {
  rows: TransactionRowDTO[];
  categoriesByWallet: Record<string, { id: string; name: string }[]>;
};

export function TransactionsList({ rows, categoriesByWallet }: Props) {
  const [selected, setSelected] = useState<TransactionRowDTO | null>(null);

  const close = useCallback(() => setSelected(null), []);

  const editable: EditableTransaction | null = selected
    ? {
        id: selected.id,
        amount: selected.amount,
        type: selected.type,
        description: selected.description,
        categoryId: selected.categoryId,
        walletId: selected.walletId,
        createdAt: selected.createdAt,
      }
    : null;

  const categories = selected
    ? categoriesByWallet[selected.walletId] ?? []
    : [];

  const dateFmt = new Intl.DateTimeFormat("pt-BR", {
    day: "numeric",
    month: "short",
  });

  return (
    <>
      <ul className="divide-y divide-slate-800">
        {rows.map((tx) => {
          const label = tx.categoryName.trim() || "Sem categoria";
          const emoji = categoryEmoji(tx.categoryName);
          const kindLabel = tx.type === "INFLOW" ? "Entrada" : "Saída";
          return (
            <li key={tx.id}>
              <button
                type="button"
                onClick={() => setSelected(tx)}
                className="w-full p-4 text-left hover:bg-slate-800/50 transition-colors flex items-center justify-between gap-3"
              >
                <div className="flex items-center gap-3 min-w-0">
                  <div
                    className="w-10 h-10 shrink-0 rounded-full flex items-center justify-center text-lg leading-none bg-slate-800/90 border border-slate-700"
                    aria-hidden
                  >
                    {emoji}
                  </div>
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2 gap-y-1">
                      <p className="font-medium text-slate-200 truncate">
                        {tx.description?.trim() || label}
                      </p>
                      <span
                        className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${categoryBadgeClass(tx.categoryName)}`}
                      >
                        <span className="mr-1" aria-hidden>
                          {emoji}
                        </span>
                        {label}
                      </span>
                      <span
                        className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wide ${
                          tx.type === "INFLOW"
                            ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-300"
                            : "border-rose-500/30 bg-rose-500/10 text-rose-300"
                        }`}
                      >
                        {kindLabel}
                      </span>
                    </div>
                    <p className="text-sm text-slate-500">
                      {dateFmt.format(new Date(tx.createdAt))}
                      <span className="text-slate-600"> · </span>
                      {tx.walletName}
                    </p>
                  </div>
                </div>
                <span
                  className={`font-semibold shrink-0 tabular-nums ${
                    tx.type === "INFLOW"
                      ? "text-emerald-500"
                      : "text-rose-500"
                  }`}
                >
                  {tx.type === "INFLOW" ? "+ " : "- "}
                  {formatCurrency(tx.amount)}
                </span>
              </button>
            </li>
          );
        })}
      </ul>

      <TransactionEditDrawer
        open={!!selected}
        transaction={editable}
        categories={categories}
        onClose={close}
      />
    </>
  );
}
