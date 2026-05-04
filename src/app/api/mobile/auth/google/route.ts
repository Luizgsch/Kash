import { prisma } from "@/lib/prisma";
import { SignJWT } from "jose";
import { NextRequest, NextResponse } from "next/server";

const SECRET = new TextEncoder().encode(
  process.env.AUTH_SECRET ?? process.env.NEXTAUTH_SECRET ?? "kash-mobile-secret"
);

interface GoogleTokenInfo {
  iss: string;
  aud: string;
  sub: string;
  email: string;
  email_verified: string;
  name?: string;
  error_description?: string;
}

export async function POST(req: NextRequest) {
  try {
    const { idToken } = await req.json();

    if (!idToken) {
      return NextResponse.json({ error: "idToken obrigatório" }, { status: 400 });
    }

    // Verifica o token com Google
    const tokenRes = await fetch(
      `https://oauth2.googleapis.com/tokeninfo?id_token=${idToken}`
    );
    const tokenInfo: GoogleTokenInfo = await tokenRes.json();

    if (!tokenRes.ok || tokenInfo.error_description) {
      return NextResponse.json({ error: "Token do Google inválido" }, { status: 401 });
    }

    if (tokenInfo.email_verified !== "true") {
      return NextResponse.json({ error: "E-mail Google não verificado" }, { status: 401 });
    }

    // Valida audience (Web Client ID deve estar em GOOGLE_CLIENT_ID)
    const expectedClientId = process.env.GOOGLE_CLIENT_ID;
    if (expectedClientId && tokenInfo.aud !== expectedClientId) {
      return NextResponse.json({ error: "Token inválido para este app" }, { status: 401 });
    }

    const email = tokenInfo.email.toLowerCase();

    const user = await prisma.user.findUnique({
      where: { email },
      include: {
        organization: {
          include: {
            wallets: { take: 1, orderBy: { id: "asc" } },
          },
        },
      },
    });

    if (!user) {
      return NextResponse.json(
        { error: "Conta não encontrada. Cadastre-se em kash.vercel.app" },
        { status: 401 }
      );
    }

    if (!user.organizationId) {
      return NextResponse.json(
        { error: "Conta sem organização. Complete o onboarding no site." },
        { status: 403 }
      );
    }

    const defaultWalletId = user.organization?.wallets[0]?.id ?? "";

    const token = await new SignJWT({
      sub: user.id,
      orgId: user.organizationId,
      walletId: defaultWalletId,
    })
      .setProtectedHeader({ alg: "HS256" })
      .setExpirationTime("30d")
      .sign(SECRET);

    return NextResponse.json({
      token,
      userId: user.id,
      organizationId: user.organizationId,
      defaultWalletId,
      name: user.name ?? tokenInfo.name ?? "",
      email: user.email ?? "",
    });
  } catch (err) {
    console.error("[mobile/auth/google]", err);
    return NextResponse.json({ error: "Erro interno" }, { status: 500 });
  }
}
