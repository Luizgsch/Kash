import { redirect } from "next/navigation";

import { auth } from "@/auth";
import { prisma } from "@/lib/prisma";

import { OnboardingClient } from "./OnboardingClient";

export default async function OnboardingPage() {
  const session = await auth();
  if (!session?.user?.id) {
    redirect("/auth/signin");
  }

  const user = await prisma.user.findUnique({
    where: { id: session.user.id },
    select: { organizationId: true },
  });
  if (user?.organizationId) {
    redirect("/");
  }

  /** Escolhas padrão definidas no servidor; o formulário só altera no cliente após hidratação. */
  const initialPessoal = true;
  const initialLoja = true;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 flex flex-col">
      <header className="pt-6 pb-2 px-4 text-center">
        <p className="text-xl font-bold text-emerald-500">Kash</p>
      </header>
      <main className="flex-1 flex flex-col items-center justify-center px-4 pb-12">
        <OnboardingClient
          initialPessoal={initialPessoal}
          initialLoja={initialLoja}
        />
      </main>
    </div>
  );
}
