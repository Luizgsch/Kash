"use client";

import { useState } from "react";
import { signIn } from "next-auth/react";
import Link from "next/link";
import { Lock, Mail } from "lucide-react";

type LoginFormProps = {
  canEmailLogin: boolean;
};

export function LoginForm({ canEmailLogin }: LoginFormProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [sending, setSending] = useState(false);
  const [credentialsPending, setCredentialsPending] = useState(false);
  const [googlePending, setGooglePending] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function handleGoogleSignIn() {
    setMessage(null);
    setGooglePending(true);
    try {
      const r = await signIn("google", { callbackUrl: "/", redirect: false });
      if (r && typeof r === "object" && "ok" in r) {
        if (!r.ok || r.error) {
          setMessage(
            r.error === "Configuration"
              ? "Google não está configurado: defina GOOGLE_CLIENT_ID e GOOGLE_CLIENT_SECRET (ou AUTH_GOOGLE_ID e AUTH_GOOGLE_SECRET)."
              : "Não foi possível iniciar o login com Google. Tente de novo."
          );
          setGooglePending(false);
          return;
        }
        if ("url" in r && typeof r.url === "string" && r.url) {
          window.location.href = r.url;
          return;
        }
      }
      setMessage("Não foi possível iniciar o login com Google.");
    } catch {
      setMessage("Não foi possível iniciar o login com Google.");
    }
    setGooglePending(false);
  }

  async function handleCredentials(e: React.FormEvent) {
    e.preventDefault();
    if (!email.trim() || !password) return;
    setCredentialsPending(true);
    setMessage(null);
    const r = await signIn("credentials", {
      email: email.trim().toLowerCase(),
      password,
      callbackUrl: "/",
      redirect: false,
    });
    setCredentialsPending(false);
    const err = r && typeof r === "object" && "error" in r ? r.error : null;
    if (err) {
      setMessage("E-mail ou senha incorretos.");
      return;
    }
    if (r && typeof r === "object" && "url" in r && typeof r.url === "string") {
      window.location.href = r.url;
    }
  }

  async function handleEmail(e: React.FormEvent) {
    e.preventDefault();
    if (!canEmailLogin || !email.trim()) return;
    setSending(true);
    setMessage(null);
    const r = await signIn("resend", {
      email: email.trim(),
      callbackUrl: "/",
      redirect: false,
    });
    setSending(false);
    const err = r && typeof r === "object" && "error" in r ? r.error : null;
    if (err) {
      setMessage("Não foi possível enviar o link. Tente de novo.");
    } else {
      setMessage("Abra o link enviado ao seu e-mail para concluir o acesso.");
    }
  }

  return (
    <div className="max-w-sm mx-auto space-y-6 w-full">
      <button
        type="button"
        onClick={handleGoogleSignIn}
        disabled={googlePending}
        className="w-full flex items-center justify-center gap-3 py-3 px-4 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 font-medium hover:bg-slate-800 transition-colors disabled:opacity-60"
      >
        <GoogleGlyph />
        {googlePending ? "Abrindo Google…" : "Continuar com Google"}
      </button>

      <form onSubmit={handleCredentials} className="space-y-3">
        <p className="text-xs text-slate-500 text-center">ou com e-mail e senha</p>
        <div className="space-y-2">
          <label className="sr-only" htmlFor="cred-email">
            E-mail
          </label>
          <div className="relative">
            <Mail
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
              size={18}
              aria-hidden
            />
            <input
              id="cred-email"
              name="email"
              type="email"
              required
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="seu@email.com"
              className="w-full pl-10 pr-3 py-3 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 placeholder:text-slate-600"
            />
          </div>
          <label className="sr-only" htmlFor="cred-password">
            Senha
          </label>
          <div className="relative">
            <Lock
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
              size={18}
              aria-hidden
            />
            <input
              id="cred-password"
              name="password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Senha"
              className="w-full pl-10 pr-3 py-3 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 placeholder:text-slate-600"
            />
          </div>
          <button
            type="submit"
            disabled={credentialsPending}
            className="w-full py-3 px-4 rounded-xl border border-slate-600 bg-slate-800 text-slate-100 font-medium hover:bg-slate-700 disabled:opacity-60"
          >
            {credentialsPending ? "Entrando…" : "Entrar com senha"}
          </button>
        </div>
      </form>

      <p className="text-center text-sm text-slate-500">
        Novo por aqui?{" "}
        <Link href="/auth/register" className="text-emerald-500 hover:text-emerald-400">
          Criar conta
        </Link>
      </p>

      {canEmailLogin ? (
        <form onSubmit={handleEmail} className="space-y-3">
          <p className="text-xs text-slate-500 text-center">ou receba um link no e-mail</p>
          <div className="flex gap-2">
            <label className="sr-only" htmlFor="email">
              E-mail
            </label>
            <div className="relative flex-1">
              <Mail
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
                size={18}
                aria-hidden
              />
              <input
                id="email"
                name="email"
                type="email"
                required
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="seu@email.com"
                className="w-full pl-10 pr-3 py-3 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 placeholder:text-slate-600"
              />
            </div>
            <button
              type="submit"
              disabled={sending}
              className="shrink-0 py-3 px-4 rounded-xl bg-emerald-500 text-slate-950 font-semibold hover:bg-emerald-400 disabled:opacity-60"
            >
              {sending ? "Enviando…" : "Link"}
            </button>
          </div>
        </form>
      ) : null}

      {message ? <p className="text-sm text-slate-400 text-center">{message}</p> : null}
    </div>
  );
}

function GoogleGlyph() {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 48 48"
      className="shrink-0"
      aria-hidden
    >
      <path
        fill="#EA4335"
        d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
      />
      <path
        fill="#4285F4"
        d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
      />
      <path
        fill="#FBBC05"
        d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
      />
      <path
        fill="#34A853"
        d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.2C6.51 42.63 14.62 48 24 48z"
      />
    </svg>
  );
}
