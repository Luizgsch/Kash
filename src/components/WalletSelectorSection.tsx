import { Suspense } from "react";

import { getWalletSelectorViewModel } from "@/lib/wallet-context-server";

import { WalletSelector } from "./WalletSelector";

function WalletSelectorSkeleton() {
  return (
    <div
      className="flex items-center gap-1.5 min-w-0 animate-pulse"
      aria-hidden
    >
      <div className="h-3.5 w-3.5 shrink-0 rounded bg-slate-800" />
      <div className="h-7 w-full min-w-0 max-w-full rounded-md border border-slate-800 bg-slate-800/80" />
    </div>
  );
}

async function WalletSelectorLoader() {
  const walletVm = await getWalletSelectorViewModel();
  if (!walletVm) return null;
  return (
    <WalletSelector
      wallets={walletVm.wallets}
      initialValue={walletVm.initialValue}
    />
  );
}

/** Busca wallets no servidor; Suspense evita área vazia enquanto o Neon acorda. */
export function WalletSelectorSection() {
  return (
    <Suspense fallback={<WalletSelectorSkeleton />}>
      <WalletSelectorLoader />
    </Suspense>
  );
}
