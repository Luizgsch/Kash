/**
 * Protege rotas da app (exc. `api`, estáticos, `favicon`): sem sessão válida o Auth.js
 * redireciona para `pages.signIn` → `/login` (`src/auth.config.ts`).
 *
 * Edge: não importa `auth.ts` (Prisma). Usa `NextAuth(authConfig)` com JWT e os mesmos
 * `callbacks` que o servidor (`trustHost`, `secret`, etc.).
 */
import NextAuth from "next-auth";

import { authConfig } from "@/auth.config";

const { auth } = NextAuth(authConfig);

export default auth;

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico).*)",
  ],
};
