import { prisma } from "@/lib/prisma";
import bcrypt from "bcryptjs";
import { SignJWT } from "jose";
import { NextRequest, NextResponse } from "next/server";

const SECRET = new TextEncoder().encode(
  process.env.AUTH_SECRET ?? process.env.NEXTAUTH_SECRET ?? "kash-mobile-secret"
);

export async function POST(req: NextRequest) {
  try {
    const { email, password } = await req.json();

    if (!email || !password) {
      return NextResponse.json({ error: "E-mail e senha obrigatórios" }, { status: 400 });
    }

    const user = await prisma.user.findUnique({
      where: { email: String(email).toLowerCase() },
      include: {
        organization: {
          include: {
            wallets: { take: 1, orderBy: { id: "asc" } },
          },
        },
      },
    });

    if (!user?.password) {
      return NextResponse.json({ error: "Credenciais inválidas" }, { status: 401 });
    }

    const valid = await bcrypt.compare(String(password), user.password);
    if (!valid) {
      return NextResponse.json({ error: "Credenciais inválidas" }, { status: 401 });
    }

    if (!user.organizationId) {
      return NextResponse.json({ error: "Conta sem organização. Complete o onboarding no site." }, { status: 403 });
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
      name: user.name ?? "",
      email: user.email ?? "",
    });
  } catch (err) {
    console.error("[mobile/auth]", err);
    return NextResponse.json({ error: "Erro interno" }, { status: 500 });
  }
}
