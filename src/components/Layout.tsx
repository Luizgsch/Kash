import { AuthBar } from "./AuthBar";
import { BottomNav } from "./BottomNav";
import { WalletSelectorSection } from "./WalletSelectorSection";

interface LayoutProps {
  children: React.ReactNode;
}

export function Layout({ children }: LayoutProps) {
  return (
    <div className="flex flex-col h-[100dvh] min-h-[100dvh] max-h-[100dvh] bg-slate-950 overflow-hidden">
      <div className="shrink-0 border-b border-slate-800/80 px-3 py-1.5 safe-area-top flex justify-between items-center gap-2 min-h-[2.5rem] bg-slate-950/95 backdrop-blur supports-[backdrop-filter]:bg-slate-950/80">
        <div className="min-w-0 flex-1 max-w-[min(100%,20rem)]">
          <WalletSelectorSection />
        </div>
        <div className="shrink-0">
          <AuthBar compact />
        </div>
      </div>
      <main className="flex-1 min-h-0 overflow-y-auto overflow-x-hidden overscroll-contain scroll-pb-[calc(7.5rem+env(safe-area-inset-bottom,0px))] md:scroll-pb-[calc(9.25rem+env(safe-area-inset-bottom,0px))]">
        <div className="min-h-full flex flex-col max-w-7xl mx-auto px-3 sm:px-4 md:px-6 py-4 md:py-6 pb-[calc(7.5rem+env(safe-area-inset-bottom,0px))] md:pb-[calc(9.25rem+env(safe-area-inset-bottom,0px))]">
          {children}
        </div>
      </main>
      <BottomNav />
    </div>
  );
}
