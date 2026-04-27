/**
 * Proteção de rotas no Edge (Vercel).
 *
 * Em `next-auth@5`, `withAuth` de `next-auth/middleware` foi removido (o módulo lança
 * erro). O equivalente suportado é o `auth` retornado por `NextAuth(...)`, usado como
 * default export — mesmo fluxo descrito em
 * https://authjs.dev/getting-started/migrating-to-v5
 *
 * Não importar `@/auth` aqui: lá entram Prisma, adapter e bcryptjs (incompatíveis com
 * Edge). Apenas `@/auth.config` (JWT + callbacks, sem Node-only).
 */
import NextAuth from "next-auth";

import { authConfig } from "@/auth.config";

const secret =
  process.env.NEXTAUTH_SECRET?.trim() ||
  process.env.AUTH_SECRET?.trim() ||
  authConfig.secret;

export default NextAuth({
  ...authConfig,
  secret,
}).auth;

/**
 * - Exclui `api` para não interceptar `/api/auth/*` (o `auth` interno chama
 *   `/api/auth/session`; rodar middleware nessa rota causaria recursão / 500).
 * - Exclui login e páginas públicas de auth: o callback `authorized` já liberaria,
 *   mas evitar o matcher reduz trabalho e elimina risco de loop de redirecionamento.
 */
export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico|login(?:/|$)|auth/signin(?:/|$)|auth/register(?:/|$)).*)",
  ],
};
