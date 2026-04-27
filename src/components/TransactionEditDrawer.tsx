"use client";

import { useCallback, useEffect, useMemo, useRef, useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import type { TransactionType } from "@prisma/client";

import {
  deleteTransaction,
  updateTransaction,
} from "@/app/actions/transactions";
import { CurrencyInput } from "@/components/CurrencyInput";

export type EditableTransaction = {
  id: string;
  amount: number;
  type: TransactionType;
  description: string | null;
  categoryId: string;
  walletId: string;
  createdAt: string;
};

type Props = {
  open: boolean;
  transaction: EditableTransaction | null;
  categories: { id: string; name: string }[];
  onClose: () => void;
};

function toDatetimeLocalValue(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export function TransactionEditDrawer({
  open,
  transaction,
  categories,
  onClose,
}: Props) {
  const router = useRouter();
  const panelRef = useRef<HTMLDivElement>(null);
  const [digits, setDigits] = useState("");
  const [description, setDescription] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [txType, setTxType] = useState<TransactionType>("OUTFLOW");
  const [createdLocal, setCreatedLocal] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isPending, startTransition] = useTransition();

  useEffect(() => {
    if (!open || !transaction) return;
    setDigits(String(transaction.amount));
    setDescription(transaction.description ?? "");
    setCategoryId(transaction.categoryId);
    setTxType(transaction.type);
    setCreatedLocal(toDatetimeLocalValue(transaction.createdAt));
    setError(null);
  }, [open, transaction]);

  const cents = useMemo(() => {
    if (!digits) return 0;
    const v = parseInt(digits, 10);
    return Number.isFinite(v) ? v : 0;
  }, [digits]);

  const onBackdropPointerDown = useCallback(
    (e: React.MouseEvent | React.TouchEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        onClose();
      }
    },
    [onClose],
  );

  const submit = () => {
    if (!transaction) return;
    if (cents <= 0) {
      setError("Informe um valor maior que zero.");
      return;
    }
    const createdAt = new Date(createdLocal);
    if (Number.isNaN(createdAt.getTime())) {
      setError("Data e hora inválidas.");
      return;
    }
    setError(null);
    startTransition(async () => {
      const res = await updateTransaction({
        transactionId: transaction.id,
        amountCents: cents,
        description: description.trim() ? description.trim() : null,
        categoryId,
        type: txType,
        createdAtIso: createdAt.toISOString(),
      });
      if (!res.ok) {
        setError(res.message);
        return;
      }
      router.refresh();
      onClose();
    });
  };

  const onDelete = () => {
    if (!transaction || isPending) return;
    const ok = window.confirm(
      "Excluir esta transação? O saldo no dashboard será atualizado.",
    );
    if (!ok) return;
    startTransition(async () => {
      const res = await deleteTransaction(transaction.id);
      if (!res.ok) {
        setError(res.message);
        return;
      }
      router.refresh();
      onClose();
    });
  };

  if (!open || !transaction) return null;

  return (
    <div
      className="fixed inset-0 z-[60] flex flex-col justify-end bg-black/55 md:items-center md:justify-center md:p-4"
      role="presentation"
      onMouseDown={onBackdropPointerDown}
      onTouchStart={onBackdropPointerDown}
    >
      <div
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="tx-edit-title"
        className="w-full max-h-[min(92vh,640px)] overflow-y-auto rounded-t-3xl md:rounded-2xl border border-slate-800 bg-slate-950 shadow-2xl md:max-w-lg motion-safe:transition-transform motion-safe:duration-200"
        onMouseDown={(e) => e.stopPropagation()}
        onTouchStart={(e) => e.stopPropagation()}
      >
        <div className="sticky top-0 z-10 flex items-center justify-between gap-3 border-b border-slate-800 bg-slate-950/95 px-4 py-3 backdrop-blur-sm">
          <h2 id="tx-edit-title" className="text-lg font-semibold text-slate-100">
            Editar transação
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg px-3 py-1.5 text-sm font-medium text-slate-400 hover:bg-slate-800 hover:text-slate-200"
          >
            Fechar
          </button>
        </div>

        <div className="space-y-4 p-4 pb-[max(1rem,env(safe-area-inset-bottom))]">
          {error ? (
            <p
              className="rounded-xl border border-rose-500/35 bg-rose-500/10 px-3 py-2 text-sm text-rose-200"
              role="alert"
            >
              {error}
            </p>
          ) : null}

          <section aria-label="Tipo" className="grid grid-cols-2 gap-2">
            <button
              type="button"
              disabled={isPending}
              onClick={() => setTxType("INFLOW")}
              className={`py-3 rounded-xl text-sm font-bold transition-all border ${
                txType === "INFLOW"
                  ? "border-emerald-400/90 bg-emerald-500 text-slate-950"
                  : "border-slate-800 bg-slate-900/80 text-slate-500"
              }`}
            >
              Entrada
            </button>
            <button
              type="button"
              disabled={isPending}
              onClick={() => setTxType("OUTFLOW")}
              className={`py-3 rounded-xl text-sm font-bold transition-all border ${
                txType === "OUTFLOW"
                  ? "border-rose-400/90 bg-rose-600 text-white"
                  : "border-slate-800 bg-slate-900/80 text-slate-500"
              }`}
            >
              Saída
            </button>
          </section>

          <label className="block space-y-1.5">
            <span className="text-xs font-medium uppercase tracking-wide text-slate-500">
              Valor
            </span>
            <CurrencyInput
              id="edit-tx-amount"
              variant="default"
              digits={digits}
              onDigitsChange={setDigits}
              aria-invalid={!!error && cents <= 0}
            />
          </label>

          <label className="block space-y-1.5">
            <span className="text-xs font-medium uppercase tracking-wide text-slate-500">
              Descrição
            </span>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Opcional"
              className="w-full rounded-xl border border-slate-800 bg-slate-900 px-3 py-2.5 text-slate-100 placeholder:text-slate-600 focus:border-emerald-500/50 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
            />
          </label>

          <label className="block space-y-1.5">
            <span className="text-xs font-medium uppercase tracking-wide text-slate-500">
              Categoria
            </span>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              className="w-full rounded-xl border border-slate-800 bg-slate-900 px-3 py-2.5 text-slate-100 focus:border-emerald-500/50 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>

          <label className="block space-y-1.5">
            <span className="text-xs font-medium uppercase tracking-wide text-slate-500">
              Data
            </span>
            <input
              type="datetime-local"
              value={createdLocal}
              onChange={(e) => setCreatedLocal(e.target.value)}
              className="w-full rounded-xl border border-slate-800 bg-slate-900 px-3 py-2.5 text-slate-100 focus:border-emerald-500/50 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
            />
          </label>

          <div className="flex flex-col gap-2 pt-2">
            <button
              type="button"
              disabled={isPending}
              onClick={submit}
              className="w-full rounded-xl bg-emerald-500 py-3 text-sm font-bold text-slate-950 hover:bg-emerald-400 disabled:opacity-50"
            >
              Salvar alterações
            </button>
            <button
              type="button"
              disabled={isPending}
              onClick={onDelete}
              className="w-full rounded-xl border border-rose-500/40 bg-rose-500/10 py-3 text-sm font-semibold text-rose-200 hover:bg-rose-500/20 disabled:opacity-50"
            >
              Excluir transação
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
