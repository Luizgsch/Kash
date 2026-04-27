"use client";

import { useState, useTransition } from "react";
import { Building2, Home, Loader2 } from "lucide-react";

import { completeOnboarding, type CompleteOnboardingResult } from "@/app/actions/onboarding";

type OnboardingClientProps = {
  /** Valores vindos do servidor (evita divergência hidratação / estado inicial vazio). */
  initialPessoal?: boolean;
  initialLoja?: boolean;
};

export function OnboardingClient({
  initialPessoal = true,
  initialLoja = true,
}: OnboardingClientProps) {
  const [pessoal, setPessoal] = useState(initialPessoal);
  const [loja, setLoja] = useState(initialLoja);
  const [err, setErr] = useState<string | null>(null);
  const [pending, start] = useTransition();

  const togglePessoal = () => {
    if (pessoal && !loja) return;
    setPessoal((v) => !v);
  };
  const toggleLoja = () => {
    if (loja && !pessoal) return;
    setLoja((v) => !v);
  };

  const submit = () => {
    setErr(null);
    if (!pessoal && !loja) {
      setErr("Selecione ao menos Vida pessoal ou Minha loja (ou as duas).");
      return;
    }
    start(() => {
      (async () => {
        const r: CompleteOnboardingResult = await completeOnboarding({
          pessoal,
          loja,
        });
        if (!r.ok) setErr(r.error);
      })();
    });
  };

  return (
    <div className="space-y-8 w-full max-w-md mx-auto">
      <header className="text-center space-y-2">
        <h1 className="text-2xl font-bold text-slate-100">
          O que vamos administrar hoje?
        </h1>
        <p className="text-sm text-slate-500">
          Escolhe um ou dois contextos. Podes ajustar depois.
        </p>
      </header>

      <div className="space-y-4">
        <button
          type="button"
          onClick={togglePessoal}
          className={[
            "w-full rounded-2xl border-2 p-5 text-left transition-colors",
            "flex items-start gap-4 min-h-[5.5rem] touch-manipulation",
            pessoal
              ? "border-emerald-500 bg-emerald-500/10"
              : "border-slate-800 bg-slate-900/50 hover:border-slate-600",
          ].join(" ")}
        >
          <div
            className={`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 ${
              pessoal ? "bg-emerald-500/20 text-emerald-400" : "bg-slate-800 text-slate-500"
            }`}
          >
            <Home className="w-6 h-6" />
          </div>
          <div>
            <p className="text-lg font-semibold text-slate-100">Vida pessoal</p>
            <p className="text-sm text-slate-500 mt-1">
              Receitas e despesas pessoais (ex. mercado, aluguel)
            </p>
          </div>
        </button>

        <button
          type="button"
          onClick={toggleLoja}
          className={[
            "w-full rounded-2xl border-2 p-5 text-left transition-colors",
            "flex items-start gap-4 min-h-[5.5rem] touch-manipulation",
            loja
              ? "border-emerald-500 bg-emerald-500/10"
              : "border-slate-800 bg-slate-900/50 hover:border-slate-600",
          ].join(" ")}
        >
          <div
            className={`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 ${
              loja ? "bg-emerald-500/20 text-emerald-400" : "bg-slate-800 text-slate-500"
            }`}
          >
            <Building2 className="w-6 h-6" />
          </div>
          <div>
            <p className="text-lg font-semibold text-slate-100">Minha loja</p>
            <p className="text-sm text-slate-500 mt-1">
              O teu pequeno negócio (ex. vendas, fornecedores)
            </p>
          </div>
        </button>
      </div>

      {err ? (
        <p className="text-sm text-rose-400 text-center" role="alert">
          {err}
        </p>
      ) : null}

      <button
        type="button"
        onClick={submit}
        disabled={pending || (!pessoal && !loja)}
        className="w-full py-3.5 rounded-xl font-semibold text-slate-950 bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 disabled:pointer-events-none flex items-center justify-center gap-2 min-h-12"
      >
        {pending ? (
          <>
            <Loader2 className="w-5 h-5 animate-spin" />
            A preparar o teu espaço…
          </>
        ) : (
          "Continuar para o dashboard"
        )}
      </button>
    </div>
  );
}
