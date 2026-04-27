"use client";

import Link from "next/link";
import { Building, User, Bell, Lock, LogOut, ChevronRight, Layers } from "lucide-react";

export default function SettingsPage() {
  return (
    <div className="space-y-6">
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Configurações</h1>
        <p className="text-gray-500">Gerencie suas preferências</p>
      </header>

      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Layers size={20} className="text-emerald-600" />
            Gerenciar espaços
          </h2>
          <p className="text-sm text-gray-500 mt-1">
            Classes, categorias e acesso rápido em Adicionar
          </p>
        </div>
        <Link
          href="/settings/spaces"
          className="p-6 flex items-center justify-between hover:bg-gray-50 transition-colors"
        >
          <span className="text-gray-700 font-medium">Abrir espaços e categorias</span>
          <ChevronRight size={20} className="text-gray-400 shrink-0" aria-hidden />
        </Link>
      </section>

      {/* Perfil da Organização */}
      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Building size={20} />
            Organização
          </h2>
        </div>
        <div className="p-6 space-y-4">
          <div>
            <label className="text-sm font-medium text-gray-700">Nome da Empresa</label>
            <input
              type="text"
              defaultValue="Minha Empresa Ltda"
              className="mt-1 w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Slug</label>
            <input
              type="text"
              defaultValue="minha-empresa"
              className="mt-1 w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </section>

      {/* Perfil do Usuário */}
      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <User size={20} />
            Perfil
          </h2>
        </div>
        <div className="p-6 space-y-4">
          <div>
            <label className="text-sm font-medium text-gray-700">Nome</label>
            <input
              type="text"
              defaultValue="João Silva"
              className="mt-1 w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Email</label>
            <input
              type="email"
              defaultValue="joao@kash.com"
              className="mt-1 w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </section>

      {/* Notificações */}
      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Bell size={20} />
            Notificações
          </h2>
        </div>
        <div className="divide-y divide-gray-100">
          <ToggleRow label="Email de transações" defaultChecked />
          <ToggleRow label="Resumo semanal" defaultChecked />
          <ToggleRow label="Alertas de orçamento" />
        </div>
      </section>

      {/* Segurança */}
      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Lock size={20} />
            Segurança
          </h2>
        </div>
        <button className="w-full p-4 flex items-center justify-between hover:bg-gray-50">
          <span className="text-gray-700">Alterar senha</span>
          <ChevronRight size={20} className="text-gray-400" />
        </button>
      </section>

      {/* Sair */}
      <button className="w-full flex items-center justify-center gap-2 bg-red-50 text-red-600 py-4 rounded-xl font-semibold hover:bg-red-100 transition-colors">
        <LogOut size={20} />
        Sair
      </button>
    </div>
  );
}

function ToggleRow({ label, defaultChecked = false }: { label: string; defaultChecked?: boolean }) {
  return (
    <div className="p-4 flex items-center justify-between">
      <span className="text-gray-700">{label}</span>
      <label className="relative inline-flex items-center cursor-pointer">
        <input type="checkbox" defaultChecked={defaultChecked} className="sr-only peer" />
        <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-100 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
      </label>
    </div>
  );
}
