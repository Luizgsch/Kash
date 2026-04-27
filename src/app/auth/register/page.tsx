import Link from "next/link";

import { RegisterForm } from "@/app/auth/register/RegisterForm";

export default function RegisterPage() {
  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center gap-8 px-4">
      <div className="text-center space-y-1">
        <h1 className="text-2xl font-bold text-slate-100">Criar conta no Kash</h1>
        <p className="text-slate-400 text-sm">
          Você recebe uma organização e os espaços Loja e Pessoal com categorias prontas.
        </p>
      </div>
      <RegisterForm />
      <Link href="/" className="text-sm text-emerald-500 hover:text-emerald-400">
        ← Voltar ao início
      </Link>
    </div>
  );
}
