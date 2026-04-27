"use client";

import { useCallback, useEffect, useMemo, useRef, useState, useTransition } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { ArrowLeft, CircleHelp } from "lucide-react";
import type { TransactionType } from "@prisma/client";

import {
  quickCreateTransaction,
  undoTransaction,
} from "@/app/actions/transactions";
import { CurrencyInput } from "@/components/CurrencyInput";
import { categoryEmoji } from "@/lib/category-emoji";
import type { ResolvedWalletContext } from "@/lib/wallet-context-shared";

export type WalletQuick = {
  id: string;
  name: string;
  categories: { id: string; name: string }[];
};

type Props = {
  wallets: WalletQuick[];
  activeContext: ResolvedWalletContext;
};

function walletScreenTint(name: string): string {
  const n = name.trim().toLowerCase();
  if (n === "pessoal" || n.includes("pessoal")) {
    return "bg-gradient-to-b from-sky-950/35 via-slate-950 to-slate-950";
  }
  if (n === "loja" || n.includes("loja")) {
    return "bg-gradient-to-b from-emerald-950/40 via-slate-950 to-slate-950";
  }
  return "bg-slate-950";
}

export function QuickAddTransaction({ wallets, activeContext }: Props) {
  const router = useRouter();
  const amountRef = useRef<HTMLInputElement>(null);
  const toastTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const [txType, setTxType] = useState<TransactionType>("INFLOW");
  const [digits, setDigits] = useState("");
  const [descriptionDraft, setDescriptionDraft] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<{ transactionId: string } | null>(null);
  const [isPending, startTransition] = useTransition();

  const isGeral = activeContext.mode === "geral";

  const activeWallet = useMemo(() => {
    if (activeContext.mode !== "wallet") return undefined;
    return wallets.find((w) => w.id === activeContext.walletId);
  }, [wallets, activeContext]);

  const quickCategories = useMemo(() => {
    if (!activeWallet) return [];
    return [...activeWallet.categories].sort((a, b) =>
      a.name.localeCompare(b.name, "pt-BR"),
    );
  }, [activeWallet]);

  const cents = useMemo(() => {
    if (!digits) return 0;
    const v = parseInt(digits, 10);
    return Number.isFinite(v) ? v : 0;
  }, [digits]);

  const dismissToast = useCallback(() => {
    if (toastTimerRef.current) {
      clearTimeout(toastTimerRef.current);
      toastTimerRef.current = null;
    }
    setToast(null);
  }, []);

  const showToast = useCallback((transactionId: string) => {
    if (toastTimerRef.current) clearTimeout(toastTimerRef.current);
    setToast({ transactionId });
    toastTimerRef.current = setTimeout(() => {
      setToast(null);
      toastTimerRef.current = null;
    }, 3000);
  }, []);

  useEffect(() => {
    return () => {
      if (toastTimerRef.current) clearTimeout(toastTimerRef.current);
    };
  }, []);

  const submit = useCallback(
    (categoryId: string | null) => {
      if (activeContext.mode === "geral") {
        setError(
          "Está em modo Geral. Escolha um espaço no seletor Contexto (topo) antes de guardar.",
        );
        return;
      }
      if (!activeWallet) {
        setError("Espaço inválido. Ajuste o Contexto no topo.");
        return;
      }
      if (cents <= 0) {
        setError("Digite o valor antes de salvar.");
        return;
      }
      setError(null);
      startTransition(async () => {
        const res = await quickCreateTransaction({
          walletId: activeWallet.id,
          categoryId: categoryId ?? undefined,
          amountCents: cents,
          type: txType,
          description: descriptionDraft.trim() || null,
        });
        if (!res.ok) {
          setError(res.message);
          return;
        }
        setDigits("");
        setDescriptionDraft("");
        showToast(res.transactionId);
        amountRef.current?.focus();
        router.refresh();
      });
    },
    [activeContext, activeWallet, cents, descriptionDraft, txType, router, showToast],
  );

  const onCategoryTap = (categoryId: string) => {
    if (isPending || activeContext.mode === "geral") return;
    submit(categoryId);
  };

  const onSaveWithoutShortcut = () => {
    if (isPending || activeContext.mode === "geral") return;
    submit(null);
  };

  const onUndo = () => {
    if (!toast || isPending) return;
    const id = toast.transactionId;
    startTransition(async () => {
      const res = await undoTransaction(id);
      if (res.ok) {
        dismissToast();
        router.refresh();
      } else {
        setError(res.message);
      }
    });
  };

  const lockedWalletName =
    activeContext.mode === "wallet"
      ? wallets.find((w) => w.id === activeContext.walletId)?.name
      : null;

  const shellTint = useMemo(() => {
    if (isGeral || !activeWallet) return "bg-slate-950";
    return walletScreenTint(activeWallet.name);
  }, [isGeral, activeWallet]);

  if (!wallets.length) {
    return (
      <p className="text-slate-400 text-sm">
        Nenhum espaço encontrado para esta organização.
      </p>
    );
  }

  return (
    <div
      className={[
        "relative -mx-3 sm:-mx-4 md:-mx-6 -mt-4 px-3 sm:px-4 md:px-6 pt-4 pb-28 min-h-[calc(100dvh-6rem)] transition-[background] duration-500 rounded-none",
        shellTint,
      ].join(" ")}
    >
      <header className="flex items-center gap-3 mb-6">
        <Link
          href="/transactions"
          className="p-2 -ml-2 hover:bg-slate-800/80 rounded-xl transition-colors text-slate-400 shrink-0 active:scale-95 touch-manipulation"
        >
          <ArrowLeft size={22} />
        </Link>
        <div className="min-w-0 flex-1">
          <h1 className="text-lg font-semibold text-slate-200 truncate">
            Adicionar
          </h1>
        </div>
        {isGeral ? (
          <span className="shrink-0 rounded-lg border border-amber-500/35 bg-amber-500/10 px-2 py-1 text-[10px] font-bold uppercase tracking-wide text-amber-200/95">
            Geral
          </span>
        ) : (
          <span
            className="shrink-0 max-w-[42%] sm:max-w-xs truncate rounded-lg border border-slate-700/90 bg-slate-900/70 px-2.5 py-1.5 text-xs font-semibold text-slate-200"
            title={lockedWalletName ?? "Carteira"}
          >
            {lockedWalletName ?? "Carteira"}
          </span>
        )}
      </header>

      <section aria-label="Valor" className="mb-6">
        <CurrencyInput
          ref={amountRef}
          id="quick-amount"
          variant="quickInsert"
          digits={digits}
          onDigitsChange={setDigits}
          autoFocus
          aria-invalid={!!error && cents <= 0}
        />
      </section>

      <section aria-label="Fluxo" className="grid grid-cols-2 gap-3 mb-6">
        <button
          type="button"
          disabled={isGeral}
          onClick={() => setTxType("INFLOW")}
          className={`min-h-[3.75rem] sm:min-h-[4.25rem] py-4 rounded-2xl text-base sm:text-lg font-extrabold transition-all border-2 touch-manipulation active:scale-95 ${
            txType === "INFLOW"
              ? "border-emerald-400/90 bg-emerald-500 text-slate-950 shadow-lg shadow-emerald-500/25"
              : "border-slate-800/90 bg-slate-900/60 text-slate-500"
          } disabled:opacity-45 disabled:pointer-events-none`}
        >
          Entrada
        </button>
        <button
          type="button"
          disabled={isGeral}
          onClick={() => setTxType("OUTFLOW")}
          className={`min-h-[3.75rem] sm:min-h-[4.25rem] py-4 rounded-2xl text-base sm:text-lg font-extrabold transition-all border-2 touch-manipulation active:scale-95 ${
            txType === "OUTFLOW"
              ? "border-rose-400/90 bg-rose-600 text-white shadow-lg shadow-rose-600/25"
              : "border-slate-800/90 bg-slate-900/60 text-slate-500"
          } disabled:opacity-45 disabled:pointer-events-none`}
        >
          Saída
        </button>
      </section>

      {isGeral ? (
        <p
          className="mb-5 rounded-2xl border border-amber-500/35 bg-amber-500/10 px-4 py-3 text-sm font-medium text-amber-100/95"
          role="status"
        >
          Modo <strong className="font-semibold">Geral</strong> agrega tudo. Para
          lançar, escolha um <strong className="font-semibold">espaço</strong> no seletor{" "}
          <strong className="font-semibold">Contexto</strong> no topo.
        </p>
      ) : null}

      {error ? (
        <p
          className="mb-4 rounded-xl border border-rose-500/35 bg-rose-500/10 px-3 py-2 text-sm text-rose-200"
          role="alert"
        >
          {error}
        </p>
      ) : null}

      {!isGeral && activeWallet ? (
        <div className="mb-5">
          <label
            htmlFor="quick-title"
            className="mb-1.5 block text-xs font-semibold uppercase tracking-wide text-slate-500"
          >
            Título da entrada ou saída (opcional)
          </label>
          <input
            id="quick-title"
            type="text"
            value={descriptionDraft}
            onChange={(e) => setDescriptionDraft(e.target.value)}
            maxLength={500}
            placeholder="Ex.: almoço com cliente, boleto do carro"
            disabled={isPending}
            className="w-full rounded-xl border border-slate-700/90 bg-slate-900/80 px-3 py-3 text-sm text-slate-100 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/35"
          />
        </div>
      ) : null}

      <section aria-label="Acesso rápido por categoria" className="pt-1">
        <p className="sr-only">Acesso rápido: categorias desta classe</p>
        {isGeral ? (
          <p className="text-center text-sm text-slate-500 py-8">
            Atalhos aparecem após escolher um espaço no contexto do topo.
          </p>
        ) : !activeWallet ? null : (
          <>
            <div className="mb-2 flex items-center justify-between gap-2 min-h-[1.25rem]">
              <div className="flex items-center gap-1 min-w-0">
                <h2 className="text-sm font-semibold text-slate-300 shrink-0">
                  Acesso rápido
                </h2>
                <span
                  className="inline-flex shrink-0 text-slate-600 hover:text-slate-500 cursor-default touch-manipulation"
                  title="As categorias desta classe vêm de Espaços. Também pode lançar só com valor e Salvar, sem tocar na grelha."
                >
                  <CircleHelp className="w-3.5 h-3.5" strokeWidth={2} aria-hidden />
                  <span className="sr-only">
                    Categorias são criadas em Espaços; lançar com valor e Salvar não
                    exige escolher uma categoria.
                  </span>
                </span>
                <span className="text-[10px] font-medium uppercase tracking-wide text-slate-600 shrink-0">
                  opcional
                </span>
              </div>
              <Link
                href="/settings/spaces"
                className="shrink-0 text-[10px] font-semibold uppercase tracking-wide text-slate-600 hover:text-emerald-400/90 transition-colors"
              >
                Espaços
              </Link>
            </div>
            {quickCategories.length === 0 ? (
              <p className="mb-4 text-center text-[11px] leading-snug text-slate-500 px-1">
                Sem categorias nesta classe — crie em{" "}
                <Link
                  href="/settings/spaces"
                  className="text-emerald-500/90 hover:text-emerald-400"
                >
                  Espaços
                </Link>{" "}
                ou use <span className="font-medium text-slate-400">Salvar</span> sem categoria.
              </p>
            ) : (
              <div className="mb-4 grid grid-cols-3 gap-2 sm:gap-3">
                {quickCategories.map((c) => (
                  <button
                    key={c.id}
                    type="button"
                    disabled={isPending || isGeral}
                    onClick={() => onCategoryTap(c.id)}
                    className="flex min-h-[5.5rem] sm:min-h-[6.25rem] flex-col items-center justify-center gap-1.5 rounded-2xl border border-slate-700/90 bg-slate-900/80 px-2 py-3 text-center shadow-sm transition-transform duration-150 ease-out touch-manipulation active:scale-95 hover:bg-slate-800/90 hover:border-slate-600 disabled:pointer-events-none disabled:opacity-40"
                  >
                    <span className="text-2xl sm:text-3xl leading-none" aria-hidden>
                      {categoryEmoji(c.name)}
                    </span>
                    <span className="text-xs sm:text-sm font-bold leading-tight text-slate-100 line-clamp-2">
                      {c.name}
                    </span>
                  </button>
                ))}
              </div>
            )}
            <button
              type="button"
              disabled={isPending || isGeral || cents <= 0}
              onClick={onSaveWithoutShortcut}
              className={`w-full min-h-[3.25rem] rounded-2xl text-sm font-bold transition-all border-2 touch-manipulation active:scale-[0.99] disabled:pointer-events-none disabled:opacity-40 ${
                txType === "INFLOW"
                  ? "border-emerald-500/50 bg-emerald-600/90 text-white hover:bg-emerald-500"
                  : "border-rose-500/50 bg-rose-600/90 text-white hover:bg-rose-500"
              }`}
            >
              Salvar {txType === "INFLOW" ? "entrada" : "saída"}
            </button>
          </>
        )}
      </section>

      {toast ? (
        <div
          className="fixed z-50 left-3 right-3 bottom-[max(1rem,env(safe-area-inset-bottom))] md:left-auto md:right-6 md:w-full md:max-w-md"
          role="status"
        >
          <div className="flex items-stretch gap-0 overflow-hidden rounded-2xl border border-emerald-600/40 bg-slate-900/98 shadow-2xl shadow-black/45 backdrop-blur-sm">
            <div className="flex flex-1 flex-col justify-center px-4 py-3">
              <p className="text-sm font-semibold text-emerald-100">
                Lançamento salvo.
              </p>
            </div>
            <button
              type="button"
              disabled={isPending}
              onClick={onUndo}
              className="shrink-0 border-l border-slate-700 px-4 py-3 text-sm font-bold text-amber-300 hover:bg-slate-800/80 disabled:opacity-50"
            >
              Desfazer
            </button>
          </div>
        </div>
      ) : null}
    </div>
  );
}
