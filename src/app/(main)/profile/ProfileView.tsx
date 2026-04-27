"use client";

import {
  User,
  Mail,
  Building,
  ChevronRight,
  LogOut,
  type LucideIcon,
} from "lucide-react";
import { signOut } from "next-auth/react";

export type ProfileViewProps = {
  displayName: string;
  email: string;
  organizationName: string;
  imageUrl: string | null;
  initialLetter: string;
};

export function ProfileView({
  displayName,
  email,
  organizationName,
  imageUrl,
  initialLetter,
}: ProfileViewProps) {
  return (
    <div className="flex w-full flex-1 flex-col gap-6">
      <header className="hidden md:block">
        <h1 className="text-2xl font-bold text-slate-100">Perfil</h1>
        <p className="text-slate-400">Suas informações</p>
      </header>

      <section className="bg-slate-900 rounded-2xl border border-slate-800 p-6">
        <div className="flex items-center gap-4">
          {imageUrl ? (
            // Avatares OAuth vêm de vários hosts; <img> evita configurar cada domínio em next/image.
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={imageUrl}
              alt=""
              className="w-20 h-20 rounded-full object-cover border border-slate-700 shrink-0"
            />
          ) : (
            <div
              className="w-20 h-20 rounded-full bg-gradient-to-br from-emerald-500 to-cyan-500 flex items-center justify-center text-2xl font-bold text-white shrink-0"
              aria-hidden
            >
              {initialLetter}
            </div>
          )}
          <div className="min-w-0">
            <h2 className="text-xl font-bold text-slate-100 truncate">{displayName}</h2>
            <p className="text-slate-400 truncate">{email}</p>
          </div>
        </div>
      </section>

      <section className="bg-slate-900 rounded-2xl border border-slate-800 overflow-hidden">
        <div className="p-4 border-b border-slate-800">
          <h3 className="font-semibold text-slate-100">Dados da Conta</h3>
        </div>
        <div className="divide-y divide-slate-800">
          <MenuItem icon={User} label="Nome" value={displayName} />
          <MenuItem icon={Mail} label="E-mail" value={email} />
          <MenuItem icon={Building} label="Organização" value={organizationName} />
        </div>
      </section>

      <section className="bg-slate-900 rounded-2xl border border-slate-800 overflow-hidden">
        <div className="p-4 border-b border-slate-800">
          <h3 className="font-semibold text-slate-100">Preferências</h3>
        </div>
        <div className="divide-y divide-slate-800">
          <ToggleRow label="Notificações push" defaultChecked />
          <ToggleRow label="Modo escuro" defaultChecked />
          <ToggleRow label="Resumo semanal" />
        </div>
      </section>

      <button
        type="button"
        onClick={() => signOut({ callbackUrl: "/" })}
        className="mt-auto w-full flex items-center justify-center gap-2 bg-rose-500/10 text-rose-500 py-4 rounded-2xl font-semibold hover:bg-rose-500/20 transition-colors border border-rose-500/20"
      >
        <LogOut size={20} />
        Sair
      </button>
    </div>
  );
}

function MenuItem({
  icon: Icon,
  label,
  value,
}: {
  icon: LucideIcon;
  label: string;
  value: string;
}) {
  return (
    <div className="p-4 flex items-center justify-between hover:bg-slate-800/50 transition-colors">
      <div className="flex items-center gap-3 min-w-0">
        <Icon size={20} className="text-slate-400 shrink-0" />
        <div className="min-w-0">
          <p className="text-sm text-slate-400">{label}</p>
          <p className="font-medium text-slate-200 break-words">{value}</p>
        </div>
      </div>
      <ChevronRight size={20} className="text-slate-600 shrink-0" aria-hidden />
    </div>
  );
}

function ToggleRow({
  label,
  defaultChecked = false,
}: {
  label: string;
  defaultChecked?: boolean;
}) {
  return (
    <div className="p-4 flex items-center justify-between hover:bg-slate-800/50 transition-colors">
      <span className="text-slate-200">{label}</span>
      <label className="relative inline-flex items-center cursor-pointer">
        <input type="checkbox" defaultChecked={defaultChecked} className="sr-only peer" />
        <div className="w-11 h-6 bg-slate-700 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-emerald-500/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-500" />
      </label>
    </div>
  );
}
