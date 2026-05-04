import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

const NAME_MAX = 120;

export async function GET(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);

    const wallets = await prisma.wallet.findMany({
      where: { organizationId: claims.orgId },
      select: {
        id: true,
        name: true,
        _count: { select: { transactions: true } },
        categories: {
          select: {
            id: true,
            name: true,
            _count: { select: { transactions: true } },
          },
          orderBy: { name: "asc" },
        },
      },
      orderBy: { createdAt: "asc" },
    });

    const result = wallets.map((w) => ({
      id: w.id,
      name: w.name,
      transactionCount: w._count.transactions,
      categories: w.categories.map((c) => ({
        id: c.id,
        name: c.name,
        transactionCount: c._count.transactions,
      })),
    }));

    return NextResponse.json(result);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body = await req.json();

    const name = (body.name ?? "").trim().slice(0, NAME_MAX);
    if (!name) return NextResponse.json({ error: "Nome obrigatório." }, { status: 400 });

    const wallet = await prisma.wallet.create({
      data: { name, organizationId: claims.orgId },
      select: { id: true, name: true },
    });

    return NextResponse.json(wallet, { status: 201 });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
