import Link from "next/link";

import { LoginForm } from "@/app/login/LoginForm";

type LoginPageProps = {
  searchParams?: { registered?: string };
};

/** Página de login canônica (`pages.signIn` do NextAuth → `/login`). */
export default function LoginPage({ searchParams }: LoginPageProps) {
  const canEmailLogin = Boolean(process.env.RESEND_API_KEY?.trim());
  const justRegistered = searchParams?.registered === "1";

  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center gap-8 px-4">
      <div className="text-center space-y-1">
        <h1 className="text-2xl font-bold text-slate-100">Entrar no Kash</h1>
        <p className="text-slate-400 text-sm">
          Google, e-mail e senha, ou link no e-mail (se configurado).
        </p>
        {justRegistered ? (
          <p className="text-emerald-400 text-sm max-w-sm mx-auto pt-2">
            Conta criada. Entre com seu e-mail e senha.
          </p>
        ) : null}
        {!canEmailLogin ? (
          <p className="text-slate-500 text-xs max-w-sm mx-auto">
            Entrada por e-mail: defina <code className="text-slate-400">RESEND_API_KEY</code> (e
            opcionalmente <code className="text-slate-400">EMAIL_FROM</code>).
          </p>
        ) : null}
      </div>
      <LoginForm canEmailLogin={canEmailLogin} />
      <Link href="/" className="text-sm text-emerald-500 hover:text-emerald-400">
        ← Voltar ao início
      </Link>
    </div>
  );
}
