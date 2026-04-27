/**
 * Rota App Router do Auth.js / NextAuth (`/api/auth/*`).
 *
 * - **Credentials** (e-mail + senha): validação com bcryptjs e usuário no Postgres em
 *   `src/auth.ts` (`CredentialsProvider` + `PrismaAdapter`).
 * - **Sessão**: `strategy: "jwt"`, `maxAge` 30 dias (e `updateAge` 24 h) em
 *   `src/auth.config.ts` — mesma base usada no middleware (Edge, sem Prisma).
 * - **Variáveis**: `DATABASE_URL` via `prisma.config.ts` + `dotenv`; segredo de sessão
 *   com `NEXTAUTH_SECRET` ou `AUTH_SECRET` em `src/auth.config.ts` (`authSecret()`).
 * - **Neon / Prisma**: se o login OAuth retornar 500 com `PrismaClientKnownRequestError`
 *   (código P2021 etc.), rode migrações no banco de produção: `npx prisma migrate deploy`
 *   com o mesmo `DATABASE_URL` da Vercel.
 */
import type { NextRequest } from "next/server";

import { handlers } from "@/auth";

function logAuthHandlerError(err: unknown, method: string) {
  const isPrismaKnown =
    err instanceof Error &&
    err.name === "PrismaClientKnownRequestError" &&
    typeof (err as { code?: string }).code === "string";

  if (isPrismaKnown) {
    const { code, message, meta } = err as unknown as {
      code: string;
      message: string;
      meta?: unknown;
    };
    console.error(
      `[api/auth] PrismaClientKnownRequestError ${method}:`,
      code,
      message,
      meta ?? null
    );
    return;
  }

  if (err instanceof Error) {
    console.error(`[api/auth] ${method}:`, err.message, err.stack);
    return;
  }
  console.error(`[api/auth] ${method}:`, err);
}

export async function GET(req: NextRequest) {
  try {
    return await handlers.GET(req);
  } catch (err) {
    logAuthHandlerError(err, "GET");
    throw err;
  }
}

export async function POST(req: NextRequest) {
  try {
    return await handlers.POST(req);
  } catch (err) {
    logAuthHandlerError(err, "POST");
    throw err;
  }
}
