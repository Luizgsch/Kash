"use client";

import { useSession, signOut } from "next-auth/react";
import Link from "next/link";
import { LogIn, LogOut } from "lucide-react";

type AuthBarProps = {
  compact?: boolean;
};

export function AuthBar({ compact }: AuthBarProps) {
  const { data: session, status } = useSession();

  if (status === "loading") {
    return <p className="text-xs text-slate-500">…</p>;
  }

  if (session?.user) {
    if (compact) {
      return (
        <div className="flex items-center gap-2 max-w-[min(100%,14rem)] sm:max-w-xs">
          <p
            className="text-xs text-slate-500 truncate min-w-0"
            title={session.user.email ?? undefined}
          >
            {session.user.name || session.user.email}
          </p>
          <button
            type="button"
            onClick={() => signOut({ callbackUrl: "/" })}
            className="shrink-0 p-2 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-200 active:scale-95 transition-transform touch-manipulation"
            aria-label="Sair"
          >
            <LogOut size={18} />
          </button>
        </div>
      );
    }

    return (
      <div className="space-y-2">
        <p className="text-xs text-slate-500 truncate" title={session.user.email ?? undefined}>
          {session.user.name || session.user.email}
        </p>
        <button
          type="button"
          onClick={() => signOut({ callbackUrl: "/" })}
          className="flex items-center gap-2 w-full px-3 py-2 text-sm text-slate-300 hover:bg-slate-800 rounded-lg transition-colors active:scale-95 touch-manipulation"
        >
          <LogOut size={16} />
          Sair
        </button>
      </div>
    );
  }

  const linkClass = compact
    ? "flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-medium text-emerald-500 hover:bg-slate-800/80 rounded-lg active:scale-95 transition-transform touch-manipulation"
    : "flex items-center gap-2 px-3 py-2 text-sm text-emerald-500 hover:bg-slate-800 rounded-lg transition-colors active:scale-95 touch-manipulation";

  return (
    <Link href="/login" className={linkClass}>
      <LogIn size={compact ? 15 : 16} />
      Entrar
    </Link>
  );
}
