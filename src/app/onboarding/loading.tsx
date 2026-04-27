export default function OnboardingLoading() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 flex flex-col">
      <header className="pt-6 pb-2 px-4 text-center">
        <p className="text-xl font-bold text-emerald-500">Kash</p>
      </header>
      <main className="flex-1 flex flex-col items-center justify-center px-4 pb-12">
        <div className="w-full max-w-md space-y-8 animate-pulse" aria-busy>
          <div className="space-y-2 text-center">
            <div className="mx-auto h-8 w-full max-w-sm rounded-lg bg-slate-800" />
            <div className="mx-auto h-4 w-full max-w-xs rounded bg-slate-800/80" />
          </div>
          <div className="space-y-4">
            <div className="h-[5.5rem] rounded-2xl border-2 border-slate-800 bg-slate-900/50" />
            <div className="h-[5.5rem] rounded-2xl border-2 border-slate-800 bg-slate-900/50" />
          </div>
          <div className="h-12 rounded-xl bg-slate-800" />
        </div>
      </main>
    </div>
  );
}
