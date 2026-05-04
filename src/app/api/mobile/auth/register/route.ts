import { prisma } from "@/lib/prisma";
import bcrypt from "bcryptjs";
import { SignJWT } from "jose";
import { NextRequest, NextResponse } from "next/server";

const SECRET = new TextEncoder().encode(
  process.env.AUTH_SECRET ?? process.env.NEXTAUTH_SECRET ?? "kash-mobile-secret"
);

export async function POST(req: NextRequest) {
  try {
    const { email, password, name } = await req.json();

    if (!email || !password) {
      return NextResponse.json({ error: "E-mail e senha obrigatórios" }, { status: 400 });
    }

    const normalizedEmail = String(email).toLowerCase();

    const existing = await prisma.user.findUnique({
      where: { email: normalizedEmail },
    });

    if (existing) {
      return NextResponse.json({ error: "E-mail já cadastrado" }, { status: 409 });
    }

    const hashedPassword = await bcrypt.hash(String(password), 10);

    const organization = await prisma.organization.create({
      data: {
        name: name ? `${name}'s Kash` : "My Kash",
        wallets: {
          create: {
            name: "Carteira Principal",
          },
        },
      },
      include: {
        wallets: true,
      },
    });

    const user = await prisma.user.create({
      data: {
        email: normalizedEmail,
        password: hashedPassword,
        name: name ?? email.split("@")[0],
        organizationId: organization.id,
      },
    });

    const defaultWalletId = organization.wallets[0]?.id ?? "";

    const token = await new SignJWT({
      sub: user.id,
      orgId: organization.id,
      walletId: defaultWalletId,
    })
      .setProtectedHeader({ alg: "HS256" })
      .setExpirationTime("30d")
      .sign(SECRET);

    return NextResponse.json(
      {
        token,
        userId: user.id,
        organizationId: organization.id,
        defaultWalletId,
        name: user.name ?? "",
        email: user.email ?? "",
      },
      { status: 201 }
    );
  } catch (err) {
    console.error("[mobile/auth/register]", err);
    return NextResponse.json({ error: "Erro interno" }, { status: 500 });
  }
}
