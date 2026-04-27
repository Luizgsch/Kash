import { Plus, Mail } from "lucide-react";

export default function TeamPage() {
  return (
    <div className="space-y-6">
      <header className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Equipe</h1>
          <p className="text-gray-500">Gerencie os membros da organização</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors">
          <Plus size={20} />
          <span className="hidden md:inline">Convidar</span>
        </button>
      </header>

      {/* Stats */}
      <section className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
          <p className="text-sm text-gray-500">Total de Membros</p>
          <p className="text-2xl font-bold text-gray-900">12</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
          <p className="text-sm text-gray-500">Administradores</p>
          <p className="text-2xl font-bold text-gray-900">3</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 md:col-span-1">
          <p className="text-sm text-gray-500">Convites Pendentes</p>
          <p className="text-2xl font-bold text-gray-900">2</p>
        </div>
      </section>

      {/* Membros */}
      <section className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900">Membros</h2>
        </div>
        <ul className="divide-y divide-gray-100">
          {[
            { name: "João Silva", email: "joao@kash.com", role: "Admin" },
            { name: "Maria Santos", email: "maria@kash.com", role: "Admin" },
            { name: "Pedro Costa", email: "pedro@kash.com", role: "Membro" },
            { name: "Ana Oliveira", email: "ana@kash.com", role: "Membro" },
          ].map((member, i) => (
            <li key={i} className="p-4 flex items-center justify-between hover:bg-gray-50">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-semibold">
                  {member.name.charAt(0)}
                </div>
                <div>
                  <p className="font-medium text-gray-900">{member.name}</p>
                  <div className="flex items-center gap-2 text-sm text-gray-500">
                    <Mail size={14} />
                    {member.email}
                  </div>
                </div>
              </div>
              <span
                className={`px-3 py-1 rounded-full text-xs font-medium ${
                  member.role === "Admin"
                    ? "bg-purple-100 text-purple-700"
                    : "bg-gray-100 text-gray-600"
                }`}
              >
                {member.role}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
