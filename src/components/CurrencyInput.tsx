"use client";

import { forwardRef, useMemo, type ChangeEvent } from "react";

import { formatBrlAmountFromCents, formatCurrency } from "@/lib/utils";

const MAX_DIGITS = 15;

export type CurrencyInputProps = {
  id?: string;
  digits: string;
  onDigitsChange: (digits: string) => void;
  autoFocus?: boolean;
  disabled?: boolean;
  /** PDV mobile: valor gigante e centralizado */
  variant?: "default" | "flash" | "quickInsert";
  className?: string;
  "aria-invalid"?: boolean;
};

/**
 * Valor só em dígitos (centavos acumulados à direita). Ex.: "100" → R$ 1,00.
 */
export const CurrencyInput = forwardRef<HTMLInputElement, CurrencyInputProps>(
  function CurrencyInput(
    {
      id,
      digits,
      onDigitsChange,
      autoFocus,
      disabled,
      variant = "default",
      className,
      "aria-invalid": ariaInvalid,
    },
    ref,
  ) {
    const sizeClass =
      variant === "quickInsert"
        ? "py-4 sm:py-6 text-4xl sm:text-5xl md:text-6xl"
        : variant === "flash"
          ? "min-h-[5.5rem] sm:min-h-[6.5rem] py-5 sm:py-8 text-4xl sm:text-5xl md:text-6xl rounded-3xl border-slate-600/70"
          : "py-4 text-3xl rounded-2xl";

    const display = useMemo(() => {
      if (!digits) return "";
      const cents = parseInt(digits, 10);
      if (!Number.isFinite(cents)) return "";
      return variant === "quickInsert"
        ? formatBrlAmountFromCents(cents)
        : formatCurrency(cents);
    }, [digits, variant]);

    /** Largura só ao conteúdo para o grupo R$ + valor poder centrar na vista. */
    const quickInsertInputCh = useMemo(() => {
      if (variant !== "quickInsert") return 6;
      if (!display) return 6;
      return Math.min(Math.max(display.length + 1, 5), 22);
    }, [variant, display]);

    const sharedInputProps = {
      id,
      type: "text" as const,
      inputMode: "numeric" as const,
      autoComplete: "off" as const,
      autoFocus,
      disabled,
      "aria-invalid": ariaInvalid,
      onChange: (e: ChangeEvent<HTMLInputElement>) => {
        if (disabled) return;
        const next = e.target.value.replace(/\D/g, "").slice(0, MAX_DIGITS);
        onDigitsChange(next);
      },
    };

    if (variant === "quickInsert") {
      return (
        <div
          className={[
            "flex w-full items-center justify-center px-2",
            className ?? "",
          ].join(" ")}
        >
          <div className="flex w-max max-w-full min-w-0 items-center gap-1.5 sm:gap-2">
            <span
              className="select-none text-3xl sm:text-4xl md:text-5xl font-bold text-slate-500 tabular-nums shrink-0"
              aria-hidden
            >
              R$
            </span>
            <input
              ref={ref}
              {...sharedInputProps}
              placeholder="0,00"
              value={display}
              style={{ width: `${quickInsertInputCh}ch` }}
              className={[
                "min-w-0 max-w-[min(calc(100vw-6rem),28rem)] shrink overflow-x-auto bg-transparent text-left font-bold tracking-tight tabular-nums text-slate-50 placeholder:text-slate-600 border-0 shadow-none outline-none focus:outline-none focus:ring-0",
                sizeClass,
                ariaInvalid ? "text-rose-200" : "",
                disabled ? "opacity-45 cursor-not-allowed" : "",
              ].join(" ")}
            />
          </div>
        </div>
      );
    }

    return (
      <input
        ref={ref}
        {...sharedInputProps}
        placeholder="R$ 0,00"
        value={display}
        className={[
          "w-full px-3 border-2 focus:outline-none focus:ring-4 bg-slate-950 text-slate-50 text-center font-bold tracking-tight tabular-nums",
          sizeClass,
          ariaInvalid
            ? "border-rose-500 focus:ring-rose-500/25"
            : "border-slate-700 focus:border-emerald-500/60 focus:ring-emerald-500/15",
          disabled ? "opacity-45 cursor-not-allowed" : "",
          className ?? "",
        ].join(" ")}
      />
    );
  },
);
