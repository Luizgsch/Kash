/**
 * Rota App Router do Auth.js / NextAuth (`/api/auth/*`).
 *
 * - **Credentials** (e-mail + senha): validação com bcrypt e usuário no Postgres em
 *   `src/auth.ts` (`CredentialsProvider` + `PrismaAdapter`).
 * - **Sessão**: `strategy: "jwt"`, `maxAge` 30 dias (e `updateAge` 24 h) em
 *   `src/auth.config.ts` — mesma base usada no middleware (Edge, sem Prisma).
 * - **Variáveis**: `DATABASE_URL` via `prisma.config.ts` + `dotenv`; segredo de sessão
 *   com `NEXTAUTH_SECRET` ou `AUTH_SECRET` em `src/auth.config.ts` (`authSecret()`).
 */
import { handlers } from "@/auth";

export const { GET, POST } = handlers;
