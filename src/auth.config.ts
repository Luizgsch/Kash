import type { NextAuthConfig } from "next-auth";
import Google from "next-auth/providers/google";
import Resend from "next-auth/providers/resend";

/** Rota raiz = Dashboard (`src/app/page.tsx`). */
const DASHBOARD_PATH = "/";

/**
 * Lê a URL pública da aplicação das variáveis de ambiente e alinha os aliases
 * que o Auth.js / NextAuth reconhecem (`NEXTAUTH_URL` e `AUTH_URL`).
 * Em produção defina uma delas (ex.: `https://app.seudominio.com`).
 */
function syncPublicAuthUrlFromEnv(): void {
  const explicit =
    process.env.NEXTAUTH_URL?.trim() || process.env.AUTH_URL?.trim();
  if (!explicit) return;
  if (!process.env.AUTH_URL?.trim()) process.env.AUTH_URL = explicit;
  if (!process.env.NEXTAUTH_URL?.trim()) process.env.NEXTAUTH_URL = explicit;
}

syncPublicAuthUrlFromEnv();

/** Sem isso, sessão e middleware falham; em dev usamos fallback só para rodar localmente. */
function authSecret(): string | undefined {
  const fromEnv =
    process.env.NEXTAUTH_SECRET?.trim() || process.env.AUTH_SECRET?.trim();
  if (fromEnv) return fromEnv;
  if (process.env.NODE_ENV !== "production") {
    return "kash-dev-only-secret-defina-AUTH_SECRET-no-env";
  }
  return undefined;
}

const resendKey = process.env.RESEND_API_KEY?.trim();
const resendFrom = process.env.EMAIL_FROM?.trim() ?? "Kash <onboarding@resend.dev>";

/** v5 infere `AUTH_GOOGLE_*`; muitos projetos ainda usam `GOOGLE_CLIENT_*` (v4). */
const googleClientId =
  process.env.AUTH_GOOGLE_ID?.trim() || process.env.GOOGLE_CLIENT_ID?.trim();
const googleClientSecret =
  process.env.AUTH_GOOGLE_SECRET?.trim() ||
  process.env.GOOGLE_CLIENT_SECRET?.trim();

const providers: NextAuthConfig["providers"] = [
  Google({
    clientId: googleClientId,
    clientSecret: googleClientSecret,
    /**
     * Permite vincular Google a um `User` já criado (ex.: cadastro com senha) quando o e-mail
     * coincide. O Google confirma o e-mail no perfil OIDC; sem isso ocorre OAuthAccountNotLinked.
     * @see https://errors.authjs.dev#oauthaccountnotlinked
     */
    allowDangerousEmailAccountLinking: true,
  }),
];
if (resendKey) {
  providers.push(
    Resend({
      apiKey: resendKey,
      from: resendFrom,
    })
  );
}

/**
 * Config usada no Middleware (Edge): sem Prisma/adapter.
 * Deve permanecer alinhada com `auth.ts` (JWT, callbacks, páginas).
 */
export const authConfig = {
  secret: authSecret(),
  providers,
  session: {
    strategy: "jwt",
    maxAge: 30 * 24 * 60 * 60,
    updateAge: 24 * 60 * 60,
  },
  pages: { signIn: "/login" },
  callbacks: {
    redirect({ url, baseUrl }) {
      const dashboard = new URL(DASHBOARD_PATH, baseUrl).toString();

      let target: URL;
      try {
        target = new URL(url, baseUrl);
      } catch {
        return dashboard;
      }

      if (target.origin !== new URL(baseUrl).origin) {
        return dashboard;
      }

      // Fluxos internos do Auth.js (callbacks, erros na rota API)
      if (target.pathname.startsWith("/api/auth")) {
        return url.startsWith("/") ? `${baseUrl}${url}` : target.toString();
      }

      // Após login (ou callbackUrl inválido): sempre o Dashboard, local ou produção
      return dashboard;
    },
    authorized({ request, auth: session }) {
      const path = request.nextUrl.pathname;
      const isSignIn =
        path === "/auth/signin" ||
        path.startsWith("/auth/signin/") ||
        path === "/auth/register" ||
        path.startsWith("/auth/register/") ||
        path === "/login" ||
        path.startsWith("/login/");
      if (isSignIn) return true;
      return !!session?.user;
    },
    jwt({ token, user }) {
      if (user?.id) token.id = user.id;
      return token;
    },
    session({ session, token }) {
      if (session.user && token.id) session.user.id = token.id as string;
      return session;
    },
  },
  trustHost: true,
} satisfies NextAuthConfig;
