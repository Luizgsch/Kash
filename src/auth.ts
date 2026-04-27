import NextAuth from "next-auth";
import { PrismaAdapter } from "@auth/prisma-adapter";
import Credentials from "next-auth/providers/credentials";
import bcrypt from "bcryptjs";

import { authConfig } from "@/auth.config";
import { prisma } from "@/lib/prisma";

/** Em produção na Vercel, defina `AUTH_DEBUG=1` para logs detalhados do Auth.js (remova depois). */
const authDebug =
  process.env.AUTH_DEBUG === "true" ||
  process.env.AUTH_DEBUG === "1" ||
  process.env.NODE_ENV !== "production";

/**
 * Cookies seguros em HTTPS (Vercel) ou quando a URL pública já é https.
 * Fora disso, deixamos indefinido para o Auth.js inferir (ex.: http://localhost).
 */
function secureCookiesFromEnv(): boolean | undefined {
  if (process.env.VERCEL === "1") return true;
  const publicUrl =
    process.env.AUTH_URL?.trim() ||
    process.env.NEXTAUTH_URL?.trim() ||
    "";
  if (publicUrl.startsWith("https://")) return true;
  return undefined;
}

export const { handlers, auth, signIn, signOut } = NextAuth({
  ...authConfig,
  debug: authDebug,
  useSecureCookies: secureCookiesFromEnv(),
  adapter: PrismaAdapter(prisma),
  providers: [
    ...authConfig.providers,
    Credentials({
      id: "credentials",
      name: "credentials",
      credentials: {
        email: { label: "E-mail", type: "email" },
        password: { label: "Senha", type: "password" },
      },
      async authorize(credentials) {
        const rawEmail = credentials?.email;
        const rawPassword = credentials?.password;
        if (typeof rawEmail !== "string" || typeof rawPassword !== "string") {
          return null;
        }
        const email = rawEmail.trim().toLowerCase();
        const password = rawPassword;
        if (!email || !password) return null;

        const user = await prisma.user.findUnique({
          where: { email },
          select: { id: true, email: true, name: true, password: true },
        });
        if (!user?.email || !user.password) return null;

        const ok = await bcrypt.compare(password, user.password);
        if (!ok) return null;

        return {
          id: user.id,
          email: user.email,
          name: user.name ?? undefined,
        };
      },
    }),
  ],
});
