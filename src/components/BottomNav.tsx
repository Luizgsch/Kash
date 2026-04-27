"use client";

import { usePathname } from "next/navigation";
import { Home, Wallet, Plus, User, Layers } from "lucide-react";
import Link from "next/link";

const ADD_HREF = "/transactions/new" as const;
const SPACES_HREF = "/settings/spaces" as const;

const navItems = [
  { href: "/", label: "Dashboard", icon: Home },
  { href: "/transactions", label: "Transações", icon: Wallet },
  { href: SPACES_HREF, label: "Espaços", icon: Layers },
  { href: ADD_HREF, label: "Adicionar", icon: Plus },
  { href: "/profile", label: "Perfil", icon: User },
] as const;

const navLinkBase =
  "flex flex-col items-center justify-end gap-0.5 px-1.5 sm:px-2 py-1 min-w-0 flex-1 max-w-[5.5rem] transition-transform duration-150 ease-out active:scale-95 touch-manipulation select-none";

export function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 bg-slate-950/95 backdrop-blur-md border-t border-slate-800 safe-area-bottom">
      <ul className="flex items-end justify-between sm:justify-around pt-2 pb-2 gap-0.5 sm:gap-1 max-w-lg mx-auto px-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isAdd = item.href === ADD_HREF;
          const isSpaces = item.href === SPACES_HREF;
          const isActive = isAdd
            ? pathname === item.href || pathname.startsWith(`${item.href}/`)
            : item.href === "/"
              ? pathname === "/"
              : item.href === "/transactions"
                ? pathname.startsWith("/transactions") &&
                  pathname !== ADD_HREF &&
                  !pathname.startsWith(`${ADD_HREF}/`)
                : isSpaces
                  ? pathname === SPACES_HREF || pathname.startsWith(`${SPACES_HREF}/`)
                  : pathname === item.href || pathname.startsWith(`${item.href}/`);

          if (isAdd) {
            return (
              <li key={item.href} className="flex flex-col items-center -mt-7 shrink-0 mx-0.5">
                <Link
                  href={item.href}
                  aria-label={item.label}
                  className={`flex flex-col items-center justify-end gap-0.5 px-1 py-1 min-w-[3.25rem] rounded-full transition-transform duration-150 ease-out active:scale-95 touch-manipulation select-none ${
                    isActive ? "text-emerald-400" : ""
                  }`}
                >
                  <span
                    className={`flex items-center justify-center rounded-full text-slate-950 w-[3.75rem] h-[3.75rem] shadow-lg ring-4 ring-slate-950 transition-[box-shadow,transform] ${
                      isActive
                        ? "bg-emerald-400 shadow-emerald-400/40 scale-105"
                        : "bg-emerald-500 shadow-emerald-500/30"
                    }`}
                  >
                    <Icon size={34} strokeWidth={2.25} aria-hidden />
                  </span>
                  <span className="text-[0.65rem] font-medium text-slate-400 mt-1 leading-none">
                    {item.label}
                  </span>
                </Link>
              </li>
            );
          }

          return (
            <li key={item.href} className="flex-1 flex justify-center min-w-0">
              <Link
                href={item.href}
                className={`${navLinkBase} ${
                  isActive ? "text-emerald-500" : "text-slate-400"
                }`}
              >
                <Icon
                  size={24}
                  strokeWidth={isActive ? 2.5 : 2}
                  className="shrink-0"
                  aria-hidden
                />
                <span className="text-[0.65rem] font-medium leading-tight text-center max-w-[4.5rem] truncate">
                  {item.label}
                </span>
              </Link>
            </li>
          );
        })}
      </ul>
    </nav>
  );
}
