"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";

const BASE = "/transactions";

function chipClass(active: boolean) {
  return [
    "px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors border transition-transform duration-150 ease-out active:scale-95 touch-manipulation inline-flex items-center justify-center",
    active
      ? "bg-emerald-500/15 border-emerald-500/40 text-emerald-200"
      : "bg-slate-900 border-slate-800 text-slate-400 hover:bg-slate-800 hover:text-slate-200",
  ].join(" ");
}

export function TransactionFilterBar() {
  const searchParams = useSearchParams();
  const typeRaw = searchParams.get("type");
  const type =
    typeRaw === "INCOME"
      ? "INFLOW"
      : typeRaw === "EXPENSE"
        ? "OUTFLOW"
        : typeRaw;
  const period = searchParams.get("period");

  const href = (updates: Record<string, string | null>) => {
    const sp = new URLSearchParams(searchParams.toString());
    for (const [k, v] of Object.entries(updates)) {
      if (v === null || v === "") sp.delete(k);
      else sp.set(k, v);
    }
    const q = sp.toString();
    return q ? `${BASE}?${q}` : BASE;
  };

  const hrefClearTypePeriod = () => {
    const sp = new URLSearchParams(searchParams.toString());
    sp.delete("type");
    sp.delete("period");
    const q = sp.toString();
    return q ? `${BASE}?${q}` : BASE;
  };

  return (
    <section className="flex gap-2 overflow-x-auto pb-2">
      <Link
        href={hrefClearTypePeriod()}
        className={chipClass(!type && !period)}
      >
        Todos
      </Link>
      <Link
        href={href({ type: "INFLOW" })}
        className={chipClass(type === "INFLOW")}
      >
        Entrada
      </Link>
      <Link
        href={href({ type: "OUTFLOW" })}
        className={chipClass(type === "OUTFLOW")}
      >
        Saída
      </Link>
      <Link
        href={href({ period: "this-month" })}
        className={chipClass(period === "this-month")}
      >
        Este Mês
      </Link>
      <Link
        href={href({ period: "last-month" })}
        className={chipClass(period === "last-month")}
      >
        Mês Passado
      </Link>
    </section>
  );
}
