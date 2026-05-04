import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function GET(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const walletId = new URL(req.url).searchParams.get("walletId");

    if (!walletId) {
      return NextResponse.json({ error: "walletId obrigatório" }, { status: 400 });
    }

    // Verify wallet belongs to org
    const wallet = await prisma.wallet.findFirst({
      where: { id: walletId, organizationId: claims.orgId },
    });
    if (!wallet) {
      return NextResponse.json({ error: "Espaço não encontrado" }, { status: 404 });
    }

    const categories = await prisma.category.findMany({
      where: { walletId },
      orderBy: { name: "asc" },
      select: { id: true, name: true },
    });

    return NextResponse.json(categories);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    return NextResponse.json({ error: message }, { status: message.includes("Token") ? 401 : 500 });
  }
}

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const { walletId, name } = await req.json();

    if (!walletId || !name?.trim()) {
      return NextResponse.json({ error: "walletId e name obrigatórios" }, { status: 400 });
    }

    const wallet = await prisma.wallet.findFirst({
      where: { id: walletId, organizationId: claims.orgId },
    });
    if (!wallet) {
      return NextResponse.json({ error: "Espaço não encontrado" }, { status: 404 });
    }

    const category = await prisma.category.create({
      data: { name: name.trim(), walletId },
      select: { id: true, name: true },
    });

    return NextResponse.json(category, { status: 201 });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    if (message.includes("Unique constraint")) {
      return NextResponse.json({ error: "Categoria já existe neste espaço" }, { status: 409 });
    }
    return NextResponse.json({ error: message }, { status: message.includes("Token") ? 401 : 500 });
  }
}
