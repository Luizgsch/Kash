"use client";

import { useCallback, useEffect, useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { FolderKanban, Pencil, Trash2, Plus, type LucideIcon } from "lucide-react";

import {
  createCategory,
  createWallet,
  deleteCategory,
  deleteWallet,
  updateCategory,
  updateWallet,
} from "@/app/actions/wallets-categories";

export type WalletManageRow = {
  id: string;
  name: string;
  transactionCount: number;
  categories: {
    id: string;
    name: string;
    transactionCount: number;
  }[];
};

type Props = { initialWallets: WalletManageRow[] };

type Feedback = { tone: "error" | "info"; text: string } | null;

const WALLET_DELETE_CONFIRM =
  "Isso apagará permanentemente todos os registros e categorias desta classe. Tem certeza?";

export function ManageSpacesClient({ initialWallets }: Props) {
  const router = useRouter();
  const [wallets, setWallets] = useState<WalletManageRow[]>(initialWallets);
  const [pending, startTransition] = useTransition();
  const [feedback, setFeedback] = useState<Feedback>(null);

  useEffect(() => {
    setWallets(initialWallets);
  }, [initialWallets]);

  const [addingClass, setAddingClass] = useState(false);
  const [newClassName, setNewClassName] = useState("");

  const [editingWalletId, setEditingWalletId] = useState<string | null>(null);
  const [editWalletDraft, setEditWalletDraft] = useState("");

  const [editingCategoryId, setEditingCategoryId] = useState<string | null>(null);
  const [editCategoryDraft, setEditCategoryDraft] = useState("");

  const [newCatByWallet, setNewCatByWallet] = useState<Record<string, { name: string }>>({});

  const getNewCat = useCallback(
    (walletId: string) => newCatByWallet[walletId] ?? { name: "" },
    [newCatByWallet],
  );

  const setNewCat = (walletId: string, part: Partial<{ name: string }>) => {
    setNewCatByWallet((prev) => ({
      ...prev,
      [walletId]: { ...(prev[walletId] ?? { name: "" }), ...part },
    }));
  };

  const run = useCallback(
    (action: () => Promise<{ ok: boolean; message?: string; transactionCount?: number }>, onOk?: () => void) => {
      setFeedback(null);
      startTransition(async () => {
        const r = await action();
        if (!r.ok) {
          const tc =
            "transactionCount" in r && typeof r.transactionCount === "number"
              ? r.transactionCount
              : 0;
          const extra = tc > 0 ? ` (${tc} trans.)` : "";
          setFeedback({ tone: "error", text: (r.message ?? "Erro") + extra });
          return;
        }
        onOk?.();
        router.refresh();
      });
    },
    [router],
  );

  const onCreateClass = () => {
    const name = newClassName.trim();
    if (!name || pending) return;
    run(() => createWallet(name), () => {
      setNewClassName("");
      setAddingClass(false);
    });
  };

  const onSaveWalletEdit = (wallet: WalletManageRow) => {
    if (!editWalletDraft.trim() || pending) return;
    const id = wallet.id;
    const name = editWalletDraft.trim();
    run(() => updateWallet(id, name), () => setEditingWalletId(null));
  };

  const onDeleteWallet = (w: WalletManageRow) => {
    if (!window.confirm(WALLET_DELETE_CONFIRM)) return;
    run(() => deleteWallet(w.id));
  };

  const onAddCategory = (wallet: WalletManageRow) => {
    const draft = getNewCat(wallet.id);
    if (!draft.name.trim() || pending) return;
    run(
      () =>
        createCategory({
          walletId: wallet.id,
          name: draft.name,
        }),
      () => {
        setNewCat(wallet.id, { name: "" });
      },
    );
  };

  const onSaveCategoryEdit = () => {
    if (!editingCategoryId || !editCategoryDraft.trim() || pending) return;
    const id = editingCategoryId;
    const name = editCategoryDraft.trim();
    run(() => updateCategory({ categoryId: id, name }), () => setEditingCategoryId(null));
  };

  const onDeleteCategory = (wallet: WalletManageRow, c: WalletManageRow["categories"][0]) => {
    if (c.transactionCount > 0) {
      setFeedback({
        tone: "error",
        text: `Não é possível excluir “${c.name}”: existem ${c.transactionCount} transação(ões) vinculadas.`,
      });
      return;
    }
    if (!window.confirm(`Excluir a categoria “${c.name}”?`)) return;
    run(() => deleteCategory(c.id));
  };

  return (
    <div className="space-y-4">
      {feedback ? (
        <p
          className={
            feedback.tone === "error"
              ? "text-rose-400 text-sm"
              : "text-slate-300 text-sm"
          }
          role="status"
        >
          {feedback.text}
        </p>
      ) : null}

      <div className="flex items-center justify-between gap-2">
        <h2 className="text-sm font-medium text-slate-400">Classes (espaços)</h2>
        {!addingClass ? (
          <button
            type="button"
            onClick={() => {
              setAddingClass(true);
              setNewClassName("");
            }}
            className="inline-flex items-center gap-1.5 text-sm font-semibold text-emerald-500 hover:text-emerald-400 py-1.5 px-2 -mr-2 rounded-lg"
          >
            <Plus size={18} />
            Nova classe
          </button>
        ) : null}
      </div>

      {addingClass ? (
        <div className="bg-slate-900 rounded-2xl border border-slate-800 p-4 space-y-3">
          <label className="block text-xs font-medium text-slate-500">Nome da nova classe</label>
          <input
            value={newClassName}
            onChange={(e) => setNewClassName(e.target.value)}
            placeholder="Ex.: mercado, loja"
            className="w-full px-3 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/40"
            autoFocus
            disabled={pending}
          />
          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={() => {
                setAddingClass(false);
                setNewClassName("");
              }}
              className="px-4 py-2 text-sm text-slate-400 hover:text-slate-200"
            >
              Cancelar
            </button>
            <button
              type="button"
              onClick={onCreateClass}
              disabled={pending || !newClassName.trim()}
              className="px-4 py-2 rounded-xl text-sm font-semibold bg-emerald-500 text-slate-950 disabled:opacity-50"
            >
              Salvar
            </button>
          </div>
        </div>
      ) : null}

      {wallets.length === 0 ? (
        <p className="text-slate-500 text-sm">Nenhuma classe ainda. Crie a primeira acima.</p>
      ) : null}

      <ul className="space-y-4">
        {wallets.map((w) => (
          <li key={w.id}>
            <section className="bg-slate-900 rounded-2xl border border-slate-800 overflow-hidden">
              <div className="p-3 sm:p-4 border-b border-slate-800 flex items-start gap-2">
                <div className="p-2 rounded-xl bg-slate-800/80 shrink-0" aria-hidden>
                  <FolderKanban size={20} className="text-emerald-500" />
                </div>
                <div className="min-w-0 flex-1">
                  {editingWalletId === w.id ? (
                    <div className="space-y-2">
                      <input
                        value={editWalletDraft}
                        onChange={(e) => setEditWalletDraft(e.target.value)}
                        className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/40"
                        disabled={pending}
                      />
                      <div className="flex flex-wrap gap-2">
                        <button
                          type="button"
                          onClick={() => onSaveWalletEdit(w)}
                          disabled={pending}
                          className="text-sm font-semibold text-emerald-500"
                        >
                          Salvar
                        </button>
                        <button
                          type="button"
                          onClick={() => setEditingWalletId(null)}
                          className="text-sm text-slate-400"
                        >
                          Cancelar
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div>
                      <div className="font-semibold text-slate-100 break-words">{w.name}</div>
                      {w.transactionCount > 0 ? (
                        <p className="text-xs text-slate-500 mt-0.5">
                          {w.transactionCount} transação(ões) nesta classe
                        </p>
                      ) : null}
                    </div>
                  )}
                </div>
                {editingWalletId === w.id ? null : (
                  <div className="flex items-center gap-0.5 shrink-0 -mr-1">
                    <IconButton
                      label="Renomear classe"
                      onClick={() => {
                        setEditingWalletId(w.id);
                        setEditWalletDraft(w.name);
                      }}
                      icon={Pencil}
                    />
                    <IconButton
                      label="Excluir classe"
                      onClick={() => onDeleteWallet(w)}
                      icon={Trash2}
                      danger
                    />
                  </div>
                )}
              </div>

              <div className="p-3 sm:p-4 space-y-2">
                {w.categories.length === 0 ? (
                  <p className="text-slate-500 text-sm">Nenhuma categoria. Adicione abaixo.</p>
                ) : (
                  <ul className="space-y-1.5">
                    {w.categories.map((c) => (
                      <li
                        key={c.id}
                        className="flex items-center gap-2 pl-1 pr-0 py-1.5 rounded-xl hover:bg-slate-800/40"
                      >
                        {editingCategoryId === c.id ? (
                          <div className="flex-1 space-y-2 min-w-0">
                            <input
                              value={editCategoryDraft}
                              onChange={(e) => setEditCategoryDraft(e.target.value)}
                              className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/40"
                              placeholder="🍕 Nome (emoji opcional)"
                              disabled={pending}
                            />
                            <div className="flex gap-2">
                              <button
                                type="button"
                                onClick={() => onSaveCategoryEdit()}
                                className="text-sm font-semibold text-emerald-500"
                                disabled={pending}
                              >
                                Salvar
                              </button>
                              <button
                                type="button"
                                onClick={() => setEditingCategoryId(null)}
                                className="text-sm text-slate-400"
                              >
                                Cancelar
                              </button>
                            </div>
                          </div>
                        ) : (
                          <>
                            <div className="min-w-0 flex-1 pl-0.5">
                              <p className="text-slate-200 break-words">{c.name}</p>
                              {c.transactionCount > 0 ? (
                                <p className="text-[11px] text-slate-500">
                                  {c.transactionCount} transação(ões)
                                </p>
                              ) : null}
                            </div>
                            <div className="flex items-center gap-0.5 shrink-0">
                              <IconButton
                                label="Editar categoria"
                                onClick={() => {
                                  setEditingCategoryId(c.id);
                                  setEditCategoryDraft(c.name);
                                }}
                                icon={Pencil}
                              />
                              <IconButton
                                label="Excluir categoria"
                                onClick={() => onDeleteCategory(w, c)}
                                icon={Trash2}
                                danger
                              />
                            </div>
                          </>
                        )}
                      </li>
                    ))}
                  </ul>
                )}

                <div className="pt-1 border-t border-slate-800/80 mt-2">
                  <p className="text-xs font-medium text-slate-500 mb-2">Nova categoria nesta classe</p>
                  <p className="text-[11px] text-slate-600 mb-2 leading-snug">
                    Cada categoria criada aqui aparece na grelha de acesso rápido em Adicionar. Em Adicionar
                    também dá para lançar só com valor e Salvar, sem escolher categoria.
                  </p>
                  <div className="flex flex-wrap items-center justify-end gap-2">
                    <input
                      value={getNewCat(w.id).name}
                      onChange={(e) => setNewCat(w.id, { name: e.target.value })}
                      placeholder="Ex.: 🍕 Jantar"
                      className="min-w-0 flex-1 basis-[12rem] px-3 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/40"
                      disabled={pending}
                    />
                    <button
                      type="button"
                      onClick={() => onAddCategory(w)}
                      disabled={pending || !getNewCat(w.id).name.trim()}
                      className="text-sm font-semibold text-emerald-500 disabled:opacity-50 shrink-0 py-2.5 px-1"
                    >
                      Adicionar
                    </button>
                  </div>
                </div>
              </div>
            </section>
          </li>
        ))}
      </ul>
    </div>
  );
}

function IconButton({
  label,
  onClick,
  icon: Icon,
  danger,
}: {
  label: string;
  onClick: () => void;
  icon: LucideIcon;
  danger?: boolean;
}) {
  return (
    <button
      type="button"
      aria-label={label}
      onClick={onClick}
      className={`p-2 rounded-lg transition-colors ${
        danger
          ? "text-slate-500 hover:text-rose-400 hover:bg-rose-500/10"
          : "text-slate-500 hover:text-slate-200 hover:bg-slate-800/80"
      }`}
    >
      <Icon size={20} />
    </button>
  );
}
