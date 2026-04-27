"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Lock, Mail, User } from "lucide-react";

import { signUp } from "@/app/actions/auth";

export function RegisterForm() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    setPending(true);
    const r = await signUp({
      name: name.trim(),
      email: email.trim(),
      password,
    });
    setPending(false);
    if (r.ok) {
      router.push("/login?registered=1");
      return;
    }
    setMessage(r.message);
  }

  return (
    <form onSubmit={handleSubmit} className="max-w-sm mx-auto space-y-4 w-full">
      <div className="space-y-2">
        <label className="sr-only" htmlFor="reg-name">
          Nome
        </label>
        <div className="relative">
          <User
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
            size={18}
            aria-hidden
          />
          <input
            id="reg-name"
            name="name"
            type="text"
            autoComplete="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Seu nome"
            required
            className="w-full pl-10 pr-3 py-3 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 placeholder:text-slate-600"
          />
        </div>
      </div>
      <div className="space-y-2">
        <label className="sr-only" htmlFor="reg-email">
          E-mail
        </label>
        <div className="relative">
          <Mail
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
            size={18}
            aria-hidden
          />
          <input
            id="reg-email"
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
      </div>
      <div className="space-y-2">
        <label className="sr-only" htmlFor="reg-password">
          Senha
        </label>
        <div className="relative">
          <Lock
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
            size={18}
            aria-hidden
          />
          <input
            id="reg-password"
            name="password"
            type="password"
            required
            autoComplete="new-password"
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Senha (mín. 8 caracteres)"
            className="w-full pl-10 pr-3 py-3 rounded-xl border border-slate-700 bg-slate-900 text-slate-100 placeholder:text-slate-600"
          />
        </div>
      </div>
      <button
        type="submit"
        disabled={pending}
        className="w-full py-3 px-4 rounded-xl bg-emerald-500 text-slate-950 font-semibold hover:bg-emerald-400 disabled:opacity-60"
      >
        {pending ? "Criando conta…" : "Criar conta"}
      </button>
      {message ? (
        <p className="text-sm text-amber-400 text-center" role="alert">
          {message}
        </p>
      ) : null}
      <p className="text-center text-sm text-slate-500">
        Já tem conta?{" "}
        <Link href="/login" className="text-emerald-500 hover:text-emerald-400">
          Entrar
        </Link>
      </p>
    </form>
  );
}
