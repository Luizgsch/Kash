"use client";

import { useEffect, useMemo, useState, useTransition } from "react";
import { usePathname, useRouter } from "next/navigation";
import { Layers } from "lucide-react";

import { setActiveWalletContext } from "@/app/actions/wallet-context";
import { ACTIVE_WALLET_GERAL } from "@/lib/wallet-context-constants";

type WalletOption = { id: string; name: string };

type Props = {
  wallets: WalletOption[];
  initialValue: string;
};

export function WalletSelector({ wallets, initialValue }: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const omitGeralOption = pathname === "/transactions/new";
  const [value, setValue] = useState(initialValue);
  const [error, setError] = useState<string | null>(null);
  const [isPending, startTransition] = useTransition();

  useEffect(() => {
    setValue(initialValue);
  }, [initialValue]);

  /** Em Adicionar não existe modo Geral: escolhe o primeiro espaço se a cookie ainda estiver em Geral. */
  useEffect(() => {
    if (!omitGeralOption) return;
    if (initialValue !== ACTIVE_WALLET_GERAL) return;
    const firstId = wallets[0]?.id;
    if (!firstId) return;
    let cancelled = false;
    startTransition(async () => {
      const res = await setActiveWalletContext(firstId);
      if (!cancelled && res.ok) router.refresh();
    });
    return () => {
      cancelled = true;
    };
  }, [omitGeralOption, initialValue, wallets, router]);

  const selectValue = useMemo(() => {
    if (
      omitGeralOption &&
      value === ACTIVE_WALLET_GERAL &&
      wallets[0]?.id
    ) {
      return wallets[0].id;
    }
    return value;
  }, [omitGeralOption, value, wallets]);

  const onChange = (next: string) => {
    setError(null);
    setValue(next);
    startTransition(async () => {
      const res = await setActiveWalletContext(next);
      if (!res.ok) {
        setError(res.message);
        setValue(initialValue);
        return;
      }
      router.refresh();
    });
  };

  return (
    <div className="flex flex-col gap-0.5 min-w-0 justify-center">
      <label htmlFor="wallet-context-select" className="sr-only">
        {omitGeralOption ? "Espaço para lançar" : "Contexto (Geral ou espaço)"}
      </label>
      <div className="flex items-center gap-1.5 min-w-0">
        <Layers size={14} className="shrink-0 text-slate-500" aria-hidden />
        <select
          id="wallet-context-select"
          value={selectValue}
          disabled={isPending || wallets.length === 0}
          onChange={(e) => onChange(e.target.value)}
          className="w-full min-w-0 max-w-full truncate rounded-md border border-slate-800 bg-slate-900/90 px-2 py-1 text-xs font-medium text-slate-200 focus:outline-none focus:ring-1 focus:ring-emerald-500/40 disabled:opacity-50"
        >
          {!omitGeralOption ? (
            <option value={ACTIVE_WALLET_GERAL}>Geral — soma de tudo</option>
          ) : null}
          {wallets.map((w) => (
            <option key={w.id} value={w.id}>
              {w.name}
            </option>
          ))}
        </select>
      </div>
      {error ? (
        <p className="text-[10px] leading-tight text-rose-400 truncate" role="alert">
          {error}
        </p>
      ) : null}
    </div>
  );
}
